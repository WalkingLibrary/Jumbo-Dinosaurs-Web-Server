package com.jumbodinosaurs.auth.exceptions;

public class NoSuchUserException extends Exception
{
    public NoSuchUserException()
    {
    }
    
    public NoSuchUserException(String message)
    {
        super(message);
    }
    
    public NoSuchUserException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public NoSuchUserException(Throwable cause)
    {
        super(cause);
    }
    
    public NoSuchUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
