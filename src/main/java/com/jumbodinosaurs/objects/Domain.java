package com.jumbodinosaurs.objects;

import java.io.File;

public class Domain
{
    private String domain;
    private String username;
    private String password;
    private String certificatePassword;
    private transient File certificateFile;
    
    
    public Domain(String domain,
                  String username,
                  String password,
                  String certificatePassword,
                  File certificateFile)
    {
        this.domain = domain;
        this.username = username;
        this.password = password;
        this.certificatePassword = certificatePassword;
        this.certificateFile = certificateFile;
    }
    
    
    public String getDomain()
    {
        return domain;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getCertificatePassword()
    {
        return certificatePassword;
    }
    
    public void setCertificatePassword(String certificatePassword)
    {
        this.certificatePassword = certificatePassword;
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
