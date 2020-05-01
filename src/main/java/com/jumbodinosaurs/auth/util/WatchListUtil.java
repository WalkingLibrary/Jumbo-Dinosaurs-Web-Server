package com.jumbodinosaurs.auth.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class WatchListUtil
{
    private static ConcurrentHashMap<String, FloatUser> watchList = new ConcurrentHashMap<String, FloatUser>();
    private static final int coolDownMinutes = 10; // minutes
    public static final int maxStrikes = 10;
    
    public static void strikeUser(String ip)
    {
        FloatUser user = getUser(ip);
        user.addStrike();
        watchList.put(ip, user);
    }
    
    public static FloatUser getUser(String ip)
    {
        
        if(watchList.containsKey(ip))
        {
            return watchList.get(ip);
        }
        
        return new FloatUser(0);
        
    }
    
   
    public static boolean shouldAcceptRequest(String ip)
    {
        if(!watchList.containsKey(ip))
        {
            return true;
        }
        System.out.println("Added " + ip);
        FloatUser user = getUser(ip);
        LocalDateTime now = LocalDateTime.now();
        
        
        if(user.getLastUpdateTime().isBefore(now.minusMinutes(coolDownMinutes)))
        {
            user.removeStrike();
            watchList.put(ip, user);
            return shouldAcceptRequest(ip);
        }
        
        
        if(user.getStrikes() > maxStrikes)
        {
            return false;
        }
        
        return true;
    }
}
