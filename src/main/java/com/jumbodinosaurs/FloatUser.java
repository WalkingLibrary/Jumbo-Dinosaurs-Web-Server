package com.jumbodinosaurs;

public class FloatUser
{

    private String ip;
    private String date;
    private int loginStrikes;
    private int emailStrikes;
    private boolean captchaLocked;
    private boolean emailQuerryLocked;

    public FloatUser(String ip, String date, int loginStrikes, int emailStrikes, boolean captchaLocked, boolean emailQuerryLocked)
    {
        this.ip = ip;
        this.date = date;
        this.loginStrikes = loginStrikes;
        this.emailStrikes = emailStrikes;
        this.captchaLocked = captchaLocked;
        this.emailQuerryLocked = emailQuerryLocked;
    }


    public String getIp()
    {
        return ip;
    }

    public String getDate()
    {
        return date;
    }

    public int getLoginStrikes()
    {
        return loginStrikes;
    }


    public void setDate(String date)
    {
        this.date = date;
    }

    public boolean isCaptchaLocked()
    {
        return captchaLocked;
    }

    public void setCaptchaLocked(boolean captchaLocked)
    {
        this.captchaLocked = captchaLocked;
    }


    public void setLoginStrikes(int loginStrikes)
    {
        this.loginStrikes = loginStrikes;
    }

    public boolean equals(FloatUser user)
    {
        return this.ip.equals(user.ip);
    }


    public int getEmailStrikes()
    {
        return emailStrikes;
    }

    public void setEmailStrikes(int emailStrikes)
    {
        this.emailStrikes = emailStrikes;
    }

    public boolean isEmailQuerryLocked()
    {
        return emailQuerryLocked;
    }

    public void setEmailQuerryLocked(boolean emailQuerryLocked)
    {
        this.emailQuerryLocked = emailQuerryLocked;
    }
}
