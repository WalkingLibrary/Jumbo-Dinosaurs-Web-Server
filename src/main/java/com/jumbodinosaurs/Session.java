package com.jumbodinosaurs;


import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;

public class Session
{
    private String who;
    private String date;
    private String time;
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
        this.date = LocalDate.now().toString();
        this.time = LocalTime.now().toString();
        this.channel = channel;
        this.message = message;
        OperatorConsole.printMessageFiltered("Message From Client: \n" + this.message,true,false);
    }

    public Session(String who, String date, String time, String message, String messageSent)
    {
        this.who = who;
        this.date = date;
        this.time = time;
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

    public String getTime()
    {
        return this.time;
    }

    public String getDate()
    {
        return this.date;
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
