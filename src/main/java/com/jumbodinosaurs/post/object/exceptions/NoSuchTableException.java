package com.jumbodinosaurs.post.object.exceptions;

public class NoSuchTableException extends Exception
{
    public NoSuchTableException()
    {
    }
    
    public NoSuchTableException(String message)
    {
        super(message);
    }
    
    public NoSuchTableException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public NoSuchTableException(Throwable cause)
    {
        super(cause);
    }
    
    public NoSuchTableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
