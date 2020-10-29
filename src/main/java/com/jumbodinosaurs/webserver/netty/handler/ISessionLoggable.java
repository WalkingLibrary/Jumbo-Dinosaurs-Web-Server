package com.jumbodinosaurs.webserver.netty.handler;

public interface ISessionLoggable
{
    String getMessageReceived();
    
    String getMessageSent();
}
