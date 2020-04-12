package com.jumbodinosaurs.database.exceptions;

public class WrongStorageFormatException extends Exception
{
    public WrongStorageFormatException()
    {
    }
    
    public WrongStorageFormatException(String message)
    {
        super(message);
    }
    
    public WrongStorageFormatException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public WrongStorageFormatException(Throwable cause)
    {
        super(cause);
    }
    
    public WrongStorageFormatException(String message,
                                       Throwable cause,
                                       boolean enableSuppression,
                                       boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
