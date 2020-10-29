package com.jumbodinosaurs.webserver.auth.util;

import java.time.LocalDateTime;

public class FloatUser
{
    private int strikes;
    private LocalDateTime lastUpdateTime;
    
    public FloatUser(int strikes)
    {
        this.strikes = strikes;
        lastUpdateTime = LocalDateTime.now();
    }
   
    public void addStrike()
    {
        this.strikes += 1;
        if(this.strikes > WatchListUtil.maxStrikes + 1)
        {
            this.strikes = WatchListUtil.maxStrikes + 1;
        }
        lastUpdateTime = LocalDateTime.now();
    }
    
    public void removeStrike()
    {
        this.strikes -= 1;
        if(this.strikes < 0)
        {
            this.strikes = 0;
        }
        lastUpdateTime = LocalDateTime.now();
    }
    
    public int getStrikes()
    {
        return strikes;
    }
    
    public void setStrikes(int strikes)
    {
        this.strikes = strikes;
    }
    
    public LocalDateTime getLastUpdateTime()
    {
        return lastUpdateTime;
    }
    
    public void setLastUpdateTime(LocalDateTime lastUpdateTime)
    {
        this.lastUpdateTime = lastUpdateTime;
    }
}
