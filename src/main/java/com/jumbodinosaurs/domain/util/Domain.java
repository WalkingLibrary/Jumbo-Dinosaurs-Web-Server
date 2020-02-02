package com.jumbodinosaurs.domain.util;

import com.google.gson.Gson;

import java.io.File;

public class Domain
{
    private String domain;
    private String type;
    private transient File getDir;
    private transient File postDir;
    
    public Domain(String domain)
    {
        this.domain = domain;
    }
    
    public Domain clone()
    {
        return new Gson().fromJson(new Gson().toJson(this), this.getClass());
    }
    
    public String getSecondLevelDomainName()
    {
        String domainName = this.domain;
        String[] domains = domainName.split(".");
        if(domains.length > 2)
        {
            return domains[domains.length - 2];
        }
        return null;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public String getType()
    {
        return type;
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
}
