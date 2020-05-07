package com.jumbodinosaurs.post.object.exceptions;

public class NoSuchPostObject extends Exception
{
    public NoSuchPostObject()
    {
    }
    
    public NoSuchPostObject(String message)
    {
        super(message);
    }
    
    public NoSuchPostObject(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public NoSuchPostObject(Throwable cause)
    {
        super(cause);
    }
    
    public NoSuchPostObject(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
