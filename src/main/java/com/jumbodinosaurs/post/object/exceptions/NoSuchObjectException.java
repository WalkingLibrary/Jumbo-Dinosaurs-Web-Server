package com.jumbodinosaurs.post.object.exceptions;

public class NoSuchObjectException extends Exception
{
    public NoSuchObjectException()
    {
    }
    
    public NoSuchObjectException(String message)
    {
        super(message);
    }
    
    public NoSuchObjectException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public NoSuchObjectException(Throwable cause)
    {
        super(cause);
    }
    
    public NoSuchObjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
