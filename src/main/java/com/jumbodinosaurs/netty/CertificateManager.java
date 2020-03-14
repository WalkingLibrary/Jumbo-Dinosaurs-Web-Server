package com.jumbodinosaurs.netty;

import com.jumbodinosaurs.devlib.email.Email;
import com.jumbodinosaurs.devlib.email.EmailManager;
import com.jumbodinosaurs.devlib.email.NoSuchEmailException;
import com.jumbodinosaurs.devlib.options.NoSuchOptionException;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.util.LinuxUtil;
import com.jumbodinosaurs.util.OptionUtil;
import com.jumbodinosaurs.util.ServerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;

public class CertificateManager
{
    
    public static File certificateDirectory = GeneralUtil.checkFor(ServerUtil.serverDataDir, "Certificates");
    
    public static void updateDomainCertificates()
    {
        for(SecureDomain domain : DomainManager.getDomains())
        {
            if(domain.hasCertificateFile())
            {
                try
                {
                    
                    
                    renewCertificate(domain);
                    convertPemToKS(domain);
                    moveCertificateFile(domain);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static void setupSecureDomain(SecureDomain domain) throws NoSuchOptionException, NoSuchEmailException
    {
        String serversEmailsUsername = OptionUtil.getDefaultEmail();
        Email serversEmail = EmailManager.getEmail(serversEmailsUsername);
        try
        {
            makeCertificate(domain, serversEmail);
            convertPemToKS(domain);
            moveCertificateFile(domain);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void moveCertificateFile(SecureDomain domain)
    {
        try
        {
            String letsEncryptCertificatePath = String.format("/etc/letsencrypt/live/%s/%s.ks", domain.getDomain(),
                                                              domain.getDomain());
            KeyStore key = KeyStore.getInstance("JKS");
            File certificateFile = new File(letsEncryptCertificatePath);
            key.load(new FileInputStream(certificateFile) , domain.getCertificatePassword().toCharArray());
            File newCertificateFile = GeneralUtil.checkFor(certificateDirectory, domain.getDomain() + ".ks");
            key.store(new FileOutputStream(newCertificateFile), domain.getCertificatePassword().toCharArray());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public static void renewCertificate(SecureDomain domain) throws IOException
    {
        String renewCommand = "sudo bash renew_certificate.sh";
        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add(domain.getDomain());
        arguments.add(domain.getGetDir().getAbsolutePath());
        String output = GeneralUtil.execute(renewCommand, arguments, LinuxUtil.unpackedScriptsDir);
        System.out.println(output);
    }
    
    private static void makeCertificate(SecureDomain domain, Email email) throws IOException
    {
        String createCommand = "sudo bash create_certificate.sh";
        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add(domain.getGetDir().getAbsolutePath());
        arguments.add(domain.getDomain());
        arguments.add(email.getUsername());
        String output = GeneralUtil.execute(createCommand, arguments, LinuxUtil.unpackedScriptsDir);
        System.out.println(output);
    }
    
    public static void convertPemToKS(SecureDomain domain) throws IOException
    {
        String convertCommand = "sudo bash convert_pem_to_ks.sh";
        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add(domain.getDomain());
        arguments.add(domain.getCertificatePassword());
        String output = GeneralUtil.execute(convertCommand, arguments, LinuxUtil.unpackedScriptsDir);
        System.out.println(output);
    }
}
