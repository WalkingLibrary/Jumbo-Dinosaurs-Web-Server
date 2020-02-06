package com.jumbodinosaurs.netty;

import com.jumbodinosaurs.ServerControl;
import com.jumbodinosaurs.devlib.email.Email;
import com.jumbodinosaurs.devlib.email.EmailManager;
import com.jumbodinosaurs.devlib.email.NoSuchEmailException;
import com.jumbodinosaurs.devlib.options.NoSuchOptionException;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.Domain;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.util.LinuxUtil;
import com.jumbodinosaurs.util.ServerUtil;

import java.io.File;

public class CertificateManager
{
    
    public static File certificateDirectory = GeneralUtil.checkFor(ServerUtil.serverDataDir, "Certificates");
    
    public static void updateDomainCertificates()
    {
        renewCertificates();
        for(Domain domain: DomainManager.getDomains())
        {
            if(domain instanceof SecureDomain)
            {
                convertPemToKS((SecureDomain) domain);
                moveCertificateFile(domain);
            }
        }
        
    }
    
    public static void setupSecureDomain(SecureDomain domain) throws NoSuchOptionException, NoSuchEmailException
    {
        String serversEmailsUsername = (String)ServerControl.optionsManager.getOption("email").getOption();
        Email serversEmail = EmailManager.getEmail(serversEmailsUsername);
        makeCertificate(domain, serversEmail);
        convertPemToKS(domain);
        moveCertificateFile(domain);
    }
    
    public static void moveCertificateFile(Domain domain)
    {
        String letsEncryptCertificatePath = String.format("/etc/letsencrypt/live/%s/%s.ks", domain.getDomain());
        File certificateFile = new File(letsEncryptCertificatePath);
        String certificateContents = GeneralUtil.scanFileContents(certificateFile);
        File newCertificateFileLocation = GeneralUtil.checkFor(certificateDirectory, domain.getDomain() + ".ks");
        GeneralUtil.writeContents(newCertificateFileLocation, certificateContents, false);
    }
    
    private static void renewCertificates()
    {
        String renewCommand = "sudo bash renew_certificates.sh";
        String output = LinuxUtil.execute(renewCommand, LinuxUtil.unpackedScriptsDir);
        System.out.println(output);
    }
    
    private static void makeCertificate(SecureDomain domain, Email email)
    {
        String createCommand = "sudo bash create_certificate.sh";
        createCommand += " " + domain.getGetDir().getAbsolutePath();
        createCommand += " " + domain.getDomain();
        createCommand += " " + email.getUsername();
        String output = LinuxUtil.execute(createCommand, LinuxUtil.unpackedScriptsDir);
        System.out.println(output);
    }
    
    private static void convertPemToKS(SecureDomain domain)
    {
        String convertCommand = "sudo bash convert_pem_to_ks.sh";
        convertCommand += " " + domain.getDomain();
        convertCommand += " " + domain.getCertificatePassword();
        String output = LinuxUtil.execute(convertCommand, LinuxUtil.unpackedScriptsDir);
        System.out.println(output);
    }
}
