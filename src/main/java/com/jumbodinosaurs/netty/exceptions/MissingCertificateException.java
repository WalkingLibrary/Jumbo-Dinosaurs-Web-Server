package com.jumbodinosaurs.netty.exceptions;

public class MissingCertificateException extends Exception
{
    public MissingCertificateException()
    {
    }
    
    public MissingCertificateException(String message)
    {
        super(message);
    }
    
    public MissingCertificateException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public MissingCertificateException(Throwable cause)
    {
        super(cause);
    }
    
    public MissingCertificateException(String message,
                                       Throwable cause,
                                       boolean enableSuppression,
                                       boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
