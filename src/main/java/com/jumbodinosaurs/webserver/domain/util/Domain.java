package com.jumbodinosaurs.webserver.domain.util;

import com.google.gson.Gson;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.webserver.auth.server.captcha.CaptchaKey;
import com.jumbodinosaurs.webserver.util.ServerUtil;

import java.io.File;

public abstract class Domain
{
    
    
    private String domain;
    private String type;
    private CaptchaKey captchaKey;
    private transient File getDir;
    private transient File postDir;
    
    public Domain(String domain)
    {
        this.domain = domain;
        this.type = getClass().getSimpleName();
        this.getDir = GeneralUtil.checkFor(ServerUtil.getDirectory, getSecondLevelDomainName());
        this.postDir = GeneralUtil.checkFor(ServerUtil.postDirectory, getSecondLevelDomainName());
     
    }
    
    public Domain clone()
    {
        return new Gson().fromJson(new Gson().toJson(this), this.getClass());
    }
    
    public String getSecondLevelDomainName()
    {
    
        String[] domains = this.domain.split("\\.");
        if(domains.length >= 2)
        {
            return domains[domains.length - 2];
        }
        return domains[0];
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public File getGetDir()
    {
        return getDir;
    }
    
    public void setGetDir(File getDir)
    {
        this.getDir = getDir;
    }
    
    public File getPostDir()
    {
        return postDir;
    }
    
    public void setPostDir(File postDir)
    {
        this.postDir = postDir;
    }
    
    @Override
    public String toString()
    {
        return "Domain{" + "domain='" + domain + '\'' + '}';
    }
    
    
    public String getType()
    {
        return type;
    }
    
    public CaptchaKey getCaptchaKey()
    {
        return captchaKey;
    }
    
    public void setCaptchaKey(CaptchaKey captchaKey)
    {
        this.captchaKey = captchaKey;
    }
}
