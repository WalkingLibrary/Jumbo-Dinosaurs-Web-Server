package com.jumbodinosaurs.database.exceptions;

public class NoSuchDataBaseException extends Exception
{
    public NoSuchDataBaseException()
    {
    }
    
    public NoSuchDataBaseException(String message)
    {
        super(message);
    }
    
    public NoSuchDataBaseException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public NoSuchDataBaseException(Throwable cause)
    {
        super(cause);
    }
    
    public NoSuchDataBaseException(String message,
                                   Throwable cause,
                                   boolean enableSuppression,
                                   boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
