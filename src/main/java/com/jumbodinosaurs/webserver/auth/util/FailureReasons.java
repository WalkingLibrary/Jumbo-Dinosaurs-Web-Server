package com.jumbodinosaurs.webserver.auth.util;

public enum FailureReasons
{
    MISSING_ATTRIBUTES,
    INCORRECT_PASSWORD,
    INCORRECT_TOKEN,
    NO_DATABASE,
    MISSING_USER,
    SERVER_ERROR,
    INVALID_USERNAME,
    MISSING_USERNAME;
    
    FailureReasons()
    {
    }
    
    
    @Override
    public String toString()
    {
        return "FailureReasons{" + this.name() + "}";
    }
}
