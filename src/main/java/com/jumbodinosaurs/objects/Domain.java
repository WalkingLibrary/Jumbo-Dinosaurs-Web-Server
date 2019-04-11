package com.jumbodinosaurs.objects;

import com.google.gson.Gson;

import java.io.File;
import java.time.LocalDateTime;

public class Domain
{
    private String domain;
    private String username;
    private String password;
    private String certificatePassword;
    private LocalDateTime lastGoodUpdateDate;
    private transient File certificateFile;
    
    public Domain()
    {
    }
    
    
    public Domain clone()
    {
        return new Gson().fromJson(new Gson().toJson(this), this.getClass());
    }
    
    
    
    public String getDomain()
    {
        return domain;
    }
    
    public String getSecondLevelDomainName()
    {
        System.out.println(this.domain);
        String domainName = this.domain;
        if(domainName.contains("."))
        {
            domainName = domainName.substring(0, domainName.lastIndexOf("."));
            System.out.println(domainName);
            if(domainName.contains("."))
            {
                domainName = domainName.substring(domainName.lastIndexOf(".")  + 1);
                System.out.println(domainName);
            }
        }
        return domainName;
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
    
    
    public LocalDateTime getLastGoodUpdateDate()
    {
        return lastGoodUpdateDate;
    }
    
    public void setLastGoodUpdateDate(LocalDateTime lastGoodUpdateDate)
    {
        this.lastGoodUpdateDate = lastGoodUpdateDate;
    }
}
