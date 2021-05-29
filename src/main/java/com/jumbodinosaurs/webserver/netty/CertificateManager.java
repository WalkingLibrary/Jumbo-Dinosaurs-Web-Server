package com.jumbodinosaurs.webserver.netty;

import com.jumbodinosaurs.devlib.email.Email;
import com.jumbodinosaurs.devlib.email.EmailManager;
import com.jumbodinosaurs.devlib.email.NoSuchEmailException;
import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.options.NoSuchOptionException;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.devlib.util.objects.ProcessOutput;
import com.jumbodinosaurs.webserver.domain.util.SecureDomain;
import com.jumbodinosaurs.webserver.util.LinuxUtil;
import com.jumbodinosaurs.webserver.util.OptionUtil;
import com.jumbodinosaurs.webserver.util.ServerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;

public class CertificateManager
{
    
    public static File certificateDirectory = GeneralUtil.checkFor(ServerUtil.serverDataDir, "Certificates");
    
    public static void updateDomainCertificate(SecureDomain domain)
    {
        try
        {
            renewCertificate(domain);
            convertPemToKS(domain);
            moveCertificateFile(domain);
        }
        catch(IOException | InterruptedException e)
        {
            LogManager.consoleLogger.error(e.getMessage(), e);
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
        catch(IOException | InterruptedException e)
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
            key.load(new FileInputStream(certificateFile), domain.getCertificatePassword().toCharArray());
            File newCertificateFile = GeneralUtil.checkFor(certificateDirectory, domain.getDomain() + ".ks");
            key.store(new FileOutputStream(newCertificateFile), domain.getCertificatePassword().toCharArray());
            LogManager.consoleLogger.debug("Moved " + domain.getDomain() + "'s certificate File");
        }
        catch(Exception e)
        {
            LogManager.consoleLogger.error(e.getMessage(), e);
        }
    
    }
    
    public static void renewCertificate(SecureDomain domain)
            throws IOException, InterruptedException
    {
        String renewCommand = "sudo bash renew_certificate.sh";
        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add(domain.getDomain());
        arguments.add(domain.getGetDir().getAbsolutePath());
        ProcessOutput output = GeneralUtil.execute(renewCommand, arguments, LinuxUtil.unpackedScriptsDir);
        LogManager.consoleLogger.debug(output.getSuccessOutput());
        LogManager.consoleLogger.debug(output.getFailureOutput());
    }
    
    private static void makeCertificate(SecureDomain domain, Email email)
            throws IOException, InterruptedException
    {
        String createCommand = "sudo bash create_certificate.sh";
        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add(domain.getGetDir().getAbsolutePath());
        arguments.add(domain.getDomain());
        arguments.add(email.getUsername());
        ProcessOutput output = GeneralUtil.execute(createCommand, arguments, LinuxUtil.unpackedScriptsDir);
        LogManager.consoleLogger.debug(output.getSuccessOutput());
        LogManager.consoleLogger.debug(output.getFailureOutput());
    }
    
    public static void convertPemToKS(SecureDomain domain)
            throws IOException, InterruptedException
    {
        String convertCommand = "sudo bash convert_pem_to_ks.sh";
        ArrayList<String> arguments = new ArrayList<String>();
        arguments.add(domain.getDomain());
        arguments.add(domain.getCertificatePassword());
        ProcessOutput output = GeneralUtil.execute(convertCommand, arguments, LinuxUtil.unpackedScriptsDir);
        LogManager.consoleLogger.debug(output.getSuccessOutput());
        LogManager.consoleLogger.debug(output.getFailureOutput());
    }
}
