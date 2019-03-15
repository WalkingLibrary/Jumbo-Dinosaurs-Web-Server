package com.jumbodinosaurs.objects;


import com.jumbodinosaurs.util.ClientTimer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

public class Session
{
    private String who;
    private LocalDateTime datetime;
    private String message;
    private String messageSent;
    private transient Channel channel;
    private transient ClientTimer timeOut = new ClientTimer(2500, new ComponentsListener());
    
    
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
    
    public void send(String message)
    {
        this.channel.write(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
    
    public void flush()
    {
        this.channel.flush();
    }
    
    @Override
    public String toString()
    {
        String toString = "Session: \n" + "Who: " + this.who + "\n\n" + "Date: " + this.datetime.toString() + "\n\n" + "Message: " + this.message + "\n\n" + "Message Sent: " + this.messageSent + "\n\n\n\n";
        
        return toString;
    }
    
    
    private class ComponentsListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource().equals(timeOut))
            {
                timeOut.stop();
            }
        }
    }
    
    
}
