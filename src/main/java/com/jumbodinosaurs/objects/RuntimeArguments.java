package com.jumbodinosaurs.objects;


public class RuntimeArguments
{
    private String captchaKey;
    private boolean testMode;
    
    public RuntimeArguments(String captchaKey, boolean testMode)
    {
        this.captchaKey = captchaKey;
        this.testMode = testMode;
    }
    
    public String getCaptchaKey()
    {
        return captchaKey;
    }
    
    public boolean isInTestMode()
    {
        return testMode;
    }
    
    public void setTestMode(boolean testMode)
    {
        this.testMode = testMode;
    }
}
