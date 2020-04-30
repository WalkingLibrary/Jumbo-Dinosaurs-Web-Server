package com.jumbodinosaurs.post.exceptions;

public class NoSuchPostCommand extends Exception
{
    public NoSuchPostCommand()
    {
    }
    
    public NoSuchPostCommand(String message)
    {
        super(message);
    }
    
    public NoSuchPostCommand(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public NoSuchPostCommand(Throwable cause)
    {
        super(cause);
    }
    
    public NoSuchPostCommand(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
