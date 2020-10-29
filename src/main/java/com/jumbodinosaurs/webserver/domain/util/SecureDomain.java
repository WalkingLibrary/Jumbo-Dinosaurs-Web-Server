package com.jumbodinosaurs.webserver.domain.util;

import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.webserver.netty.CertificateManager;

import java.io.File;

public class SecureDomain extends Domain
{
    private String certificatePassword;
    private boolean hasCertificateFile;
    private transient File certificateFile;
    
    public SecureDomain(String domain)
    {
        super(domain);
        this.hasCertificateFile = false;
    }
    
    public SecureDomain(String domain, String certificatePassword)
    {
        super(domain);
        this.certificatePassword = certificatePassword;
        this.hasCertificateFile = true;
        this.certificateFile = GeneralUtil.checkFor(CertificateManager.certificateDirectory,
                                                    domain + ".ks");
    }
    
    public String getCertificatePassword()
    {
        return certificatePassword;
    }
    
    public void setCertificatePassword(String certificatePassword)
    {
        this.certificatePassword = certificatePassword;
    }
    
    public boolean hasCertificateFile()
    {
        return hasCertificateFile;
    }
    
    public void setHasCertificateFile(boolean hasCertificateFile)
    {
        this.hasCertificateFile = hasCertificateFile;
    }
    
    public File getCertificateFile()
    {
        return certificateFile;
    }
    
    public void setCertificateFile(File certificateFile)
    {
        this.certificateFile = certificateFile;
    }
}
