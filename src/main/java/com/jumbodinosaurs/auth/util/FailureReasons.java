package com.jumbodinosaurs.auth.util;

public enum FailureReasons
{
    MISSING_ATTRIBUTES, INCORRECT_PASSWORD, INCORRECT_TOKEN, NO_DATABASE, MISSING_USER, SERVER_ERROR,
    ACCOUNT_NOT_ACTIVATED;
    
    FailureReasons()
    {
    }
}
