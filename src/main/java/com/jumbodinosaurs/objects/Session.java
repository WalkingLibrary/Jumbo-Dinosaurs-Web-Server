package com.jumbodinosaurs.objects;


import io.netty.channel.Channel;

import java.time.LocalDateTime;

public class Session
{
    private String who;
    private LocalDateTime datetime;
    private String message;
    private String messageSent;
    private transient Channel channel;

    
    
    public Session(Channel channel, String message) throws Exception
    {
        String ip = channel.remoteAddress().toString();
        if(ip.contains(":"))
        {
            this.who = ip.substring(0, ip.indexOf(":"));
        }
        else
        {
            this.who = channel.remoteAddress().toString();
        }
        this.datetime = LocalDateTime.now();
        this.channel = channel;
        this.message = message;
    }
    
    public Session(String who, LocalDateTime dateTime, String message, String messageSent)
    {
        this.who = who;
        this.datetime = dateTime;
        this.message = message;
        this.messageSent = messageSent;
    }
    
    
    public String getWho()
    {
        return this.who;
    }
    
    public String getMessage()
    {
        return this.message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    
    public void setMessageSent(String messageSent)
    {
        this.messageSent = messageSent;
    }
    
    public LocalDateTime getDateTime()
    {
        return this.datetime;
    }
    
    
    @Override
    public String toString()
    {
        String toString = "Session: \n" + "Who: " + this.who + "\n\n" + "Date: " + this.datetime.toString() + "\n\n" + "Message: " + this.message + "\n\n" + "Message Sent: " + this.messageSent + "\n\n\n\n";
        
        return toString;
    }
    
    
    
}
