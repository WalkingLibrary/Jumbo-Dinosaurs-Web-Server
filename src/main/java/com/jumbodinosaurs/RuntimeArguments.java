package com.jumbodinosaurs;

import java.util.ArrayList;

public class RuntimeArguments
{
    private ArrayList<Domain> domains;
    private String captchaKey;
    private String certificateKey;

    public RuntimeArguments(ArrayList<Domain> domains, String captchaKey, String certificateKey)
    {
        this.domains = domains;
        this.captchaKey = captchaKey;
        this.certificateKey = certificateKey;
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
}
