package com.jumbodinosaurs.database.objects;

import java.time.LocalDateTime;

public class PastPing
{
    private String ip;
    private LocalDateTime date;
    private boolean goodPing;
    private int timesPinged;
    
    
    
    public PastPing()
    {
    }
    
    public PastPing(String ip, LocalDateTime date, String message)
    {
        this.ip = ip;
        this.date = date;
        if(message != null)
        {
            this.goodPing = true;
        }
        else
        {
            this.goodPing = false;
        }
        this.timesPinged = 0;
    }
    
    
    public PastPing(String ip, LocalDateTime date, String message, int timesPinged)
    {
        this.ip = ip;
        this.date = date;
        if(message != null)
        {
            this.goodPing = true;
        }
        else
        {
            this.goodPing = false;
        }
        this.timesPinged = timesPinged;
    }
    
    public String getIp()
    {
        return ip;
    }
    
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    
    public LocalDateTime getDate()
    {
        return date;
    }
    
    public void setDate(LocalDateTime date)
    {
        this.date = date;
    }
    
    public boolean isGoodPing()
    {
        return goodPing;
    }
    
    public void setGoodPing(boolean goodPing)
    {
        this.goodPing = goodPing;
    }
    
    public int getTimesPinged()
    {
        return timesPinged;
    }
    
    public void setTimesPinged(int timesPinged)
    {
        this.timesPinged = timesPinged;
    }
}
