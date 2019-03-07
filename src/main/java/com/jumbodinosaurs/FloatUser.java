package com.jumbodinosaurs;

public class FloatUser
{

    private String ip;
    private String date;
    private int strikes;
    private boolean captchaLocked;

    public FloatUser(String ip, String date, int strikes, boolean captchaLocked)
    {
        this.ip = ip;
        this.date = date;
        this.strikes = strikes;
        this.captchaLocked = captchaLocked;
    }


    public String getIp()
    {
        return ip;
    }

    public String getDate()
    {
        return date;
    }

    public int getStrikes()
    {
        return strikes;
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


    public void setStrikes(int strikes)
    {
        this.strikes = strikes;
    }

    public boolean equals(FloatUser user)
    {
        return this.ip.equals(user.ip);
    }


}
