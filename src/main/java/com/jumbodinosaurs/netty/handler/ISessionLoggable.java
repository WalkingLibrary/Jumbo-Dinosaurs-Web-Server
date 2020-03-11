package com.jumbodinosaurs.netty.handler;

public interface ISessionLoggable
{
    String getMessageReceived();
    
    String getMessageSent();
}
