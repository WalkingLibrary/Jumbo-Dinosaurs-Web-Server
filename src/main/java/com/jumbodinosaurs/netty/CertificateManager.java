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
import java.util.ArrayList;

public class CertificateManager
{
    
    public static File certificateDirectory = GeneralUtil.checkFor(ServerUtil.serverDataDir, "Certificates");
    
    public static void updateDomainCertificates()
    {
        renewCertificates();
        for(SecureDomain domain: DomainManager.getDomains())
        {
            if(domain.hasCertificateFile())
            {
                convertPemToKS(domain);
                moveCertificateFile(domain);
            }
        }
        
    }
    
    public static void setupSecureDomain(SecureDomain domain) throws NoSuchOptionException, NoSuchEmailException
    {
        String serversEmailsUsername = OptionUtil.getDefaultEmail();
        Email serversEmail = EmailManager.getEmail(serversEmailsUsername);
        makeCertificate(domain, serversEmail);
        convertPemToKS(domain);
        moveCertificateFile(domain);
    }
    
    public static void moveCertificateFile(SecureDomain domain)
    {
        String letsEncryptCertificatePath = String.format("/etc/letsencrypt/live/%s/%s.ks", domain.getDomain(),
                                                          domain.getDomain());
        String moveCertificateCommand = "sudo bash moveFile.sh";
        ArrayList<String> arguments = new ArrayList<String >();
        arguments.add(letsEncryptCertificatePath);
        arguments.add(certificateDirectory.getAbsolutePath());
        arguments.add(domain.getDomain() + ".ks");
        String output = LinuxUtil.execute(moveCertificateCommand, arguments,LinuxUtil.unpackedScriptsDir);
        System.out.println(output);
    }
    
    private static void renewCertificates()
    {
        String renewCommand = "sudo bash renew_certificates.sh";
        String output = LinuxUtil.execute(renewCommand, null,LinuxUtil.unpackedScriptsDir);
        System.out.println(output);
    }
    
    private static void makeCertificate(SecureDomain domain, Email email)
    {
        String createCommand = "sudo bash create_certificate.sh";
        ArrayList<String> arguments = new ArrayList<String >();
        arguments.add(domain.getGetDir().getAbsolutePath());
        arguments.add(domain.getDomain());
        arguments.add(email.getUsername());
        String output = LinuxUtil.execute(createCommand, arguments,LinuxUtil.unpackedScriptsDir);
        System.out.println(output);
    }
    
    public static void convertPemToKS(SecureDomain domain)
    {
        String convertCommand = "sudo bash convert_pem_to_ks.sh";
        ArrayList<String> arguments = new ArrayList<String >();
        arguments.add(domain.getDomain());
        arguments.add(domain.getCertificatePassword());
        String output = LinuxUtil.execute(convertCommand,arguments ,LinuxUtil.unpackedScriptsDir);
        System.out.println(output);
    }
}
