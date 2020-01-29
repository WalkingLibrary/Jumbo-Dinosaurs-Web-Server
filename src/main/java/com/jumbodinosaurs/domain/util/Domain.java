package com.jumbodinosaurs.domain.util;

import com.google.gson.Gson;

public class Domain
{
    private String domain;
    private String type;
    
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
}
