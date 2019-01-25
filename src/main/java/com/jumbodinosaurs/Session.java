package com.jumbodinosaurs;


import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class Session
{
    private String who;
    private String when;
    private String message;
    private String messageSent;
    private transient Channel channel;
    private transient ClientTimer timeOut = new ClientTimer(2500, new ComponentsListener());


    public Session(Channel channel, String message) throws Exception
    {
        this.who = channel.remoteAddress().toString();
        this.when = new Date().toString();
        this.channel = channel;
        this.message = message;
        System.out.println("Message From Client: \n" + this.message);
    }

    public Session(String who, String when, String message, String messageSent)
    {
        this.who = who;
        this.when = when;
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

    public void setMessageSent(String messageSent)
    {
        this.messageSent = messageSent;
    }

    public String getWhen()
    {
        return this.when;
    }


    public void send(String message)
    {
        this.channel.write(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    public void flush()
    {
        this.channel.flush();
    }


    private class ComponentsListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(timeOut))
            {
                timeOut.stop();
            }
        }
    }


}
