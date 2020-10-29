package com.jumbodinosaurs.webserver.auth.server.captcha;

public class CaptchaKey
{
    private String siteKey;
    private String secretKey;
    
    
    public CaptchaKey(String siteKey, String secretKey)
    {
        this.siteKey = siteKey;
        this.secretKey = secretKey;
    }
    
    public CaptchaKey()
    {
    }
    
    public String getSiteKey()
    {
        return siteKey;
    }
    
    public void setSiteKey(String siteKey)
    {
        this.siteKey = siteKey;
    }
    
    public String getSecretKey()
    {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }
    
}
