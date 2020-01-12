package com.jumbodinosaurs.objects;

public class CaptchaResponse
{
    private boolean success;
    private String hostname;
    private double score;
    private String action;

    public CaptchaResponse(boolean success, String hostname, double score, String action)
    {
        this.success = success;
        this.hostname = hostname;
        this.score = score;
        this.action = action;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getHostname()
    {
        return hostname;
    }

    public double getScore()
    {
        return score;
    }

    public String getAction()
    {
        return action;
    }
}
