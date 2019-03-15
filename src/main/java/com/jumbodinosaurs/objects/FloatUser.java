package com.jumbodinosaurs.objects;

public class  FloatUser
{

    private String ip;
    private String date;
    private String unlockedDate;
    private int loginStrikes;
    private int emailStrikes;
    private boolean captchaLocked;
    private boolean emailQueryLocked;
    
    public FloatUser(String ip, String date, String unlockedDate, int loginStrikes, int emailStrikes, boolean captchaLocked, boolean emailQueryLocked)
    {
        this.ip = ip;
        this.date = date;
        this.unlockedDate = unlockedDate;
        this.loginStrikes = loginStrikes;
        this.emailStrikes = emailStrikes;
        this.captchaLocked = captchaLocked;
        this.emailQueryLocked = emailQueryLocked;
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

    public boolean isEmailQueryLocked()
    {
        return emailQueryLocked;
    }

    public void setEmailQueryLocked(boolean emailQueryLocked)
    {
        this.emailQueryLocked = emailQueryLocked;
    }
    
    public String getUnlockedDate()
    {
        return unlockedDate;
    }
    
    public void setUnlockedDate(String unlockedDate)
    {
        this.unlockedDate = unlockedDate;
    }
}
