package com.jumbodinosaurs.auth.util;

public enum FailureReasons
{
    MISSING_ATTRIBUTES, INCORRECT_PASSWORD, INCORRECT_TOKEN, NO_DATABASE, MISSING_USER, SERVER_ERROR,
    ACCOUNT_NOT_ACTIVATED, INVALID_USERNAME, MISSING_USERNAME;
    
    FailureReasons()
    {
    }
    
    
    @Override
    public String toString()
    {
        return "FailureReasons{" + this.name() +"}";
    }
}
