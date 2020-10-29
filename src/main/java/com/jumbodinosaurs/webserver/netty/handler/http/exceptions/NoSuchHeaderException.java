package com.jumbodinosaurs.webserver.netty.handler.http.exceptions;

public class NoSuchHeaderException extends Exception
{
    public NoSuchHeaderException()
    {
    }
    
    public NoSuchHeaderException(String message)
    {
        super(message);
    }
    
    public NoSuchHeaderException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public NoSuchHeaderException(Throwable cause)
    {
        super(cause);
    }
    
    public NoSuchHeaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
