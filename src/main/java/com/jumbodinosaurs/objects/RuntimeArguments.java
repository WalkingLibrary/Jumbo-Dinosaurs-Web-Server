package com.jumbodinosaurs.objects;

import java.util.ArrayList;

public class RuntimeArguments
{
    private ArrayList<Domain> domains;
    private String captchaKey;
    private ArrayList<Email> emails;
    private boolean testMode;
    
    
    public RuntimeArguments(ArrayList<Domain> domains, String captchaKey, ArrayList<Email> emails, boolean testMode)
    {
        this.domains = domains;
        this.captchaKey = captchaKey;
        this.emails = emails;
        this.testMode = testMode;
    }
    
    public RuntimeArguments()
    {
    }
    
    
    public ArrayList<Domain> getDomains()
    {
        return domains;
    }

    public String getCaptchaKey()
    {
        return captchaKey;
    }
    

    public ArrayList<Email> getEmails()
    {
        return emails;
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
