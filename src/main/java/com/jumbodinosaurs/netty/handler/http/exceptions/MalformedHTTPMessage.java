package com.jumbodinosaurs.netty.handler.http.exceptions;

public class MalformedHTTPMessage extends Exception
{
    public MalformedHTTPMessage()
    {
    }
    
    public MalformedHTTPMessage(String message)
    {
        super(message);
    }
    
    public MalformedHTTPMessage(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public MalformedHTTPMessage(Throwable cause)
    {
        super(cause);
    }
    
    public MalformedHTTPMessage(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
