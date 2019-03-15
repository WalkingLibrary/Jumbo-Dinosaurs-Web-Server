package com.jumbodinosaurs.objects;

import java.util.ArrayList;

public class RuntimeArguments
{
    private ArrayList<Domain> domains;
    private String captchaKey;
    private String certificateKey;
    private ArrayList<Email> emails;

    public RuntimeArguments(ArrayList<Domain> domains, String captchaKey, String certificateKey, ArrayList<Email> emails)
    {
        this.domains = domains;
        this.captchaKey = captchaKey;
        this.certificateKey = certificateKey;
        this.emails = emails;
    }


    public ArrayList<Domain> getDomains()
    {
        return domains;
    }

    public String getCaptchaKey()
    {
        return captchaKey;
    }

    public String getCertificateKey()
    {
        return certificateKey;
    }

    public ArrayList<Email> getEmails()
    {
        return emails;
    }
}
