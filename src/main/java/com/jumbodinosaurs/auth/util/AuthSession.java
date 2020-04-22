package com.jumbodinosaurs.auth.util;

import com.jumbodinosaurs.auth.server.AuthToken;
import com.jumbodinosaurs.auth.server.User;

public class AuthSession
{
    private boolean success;
    private User user;
    private boolean passwordAuth;
    private AuthToken tokenUsed;
    private FailureReasons failureCode;
    
    public AuthSession()
    {
    }
    
    public boolean isSuccess()
    {
        return success;
    }
    
    public void setSuccess(boolean success)
    {
        this.success = success;
    }
    
    public User getUser()
    {
        return user.clone();
    }
    
    protected void setUser(User user)
    {
        this.user = user;
    }
    
    public FailureReasons getFailureCode()
    {
        return failureCode;
    }
    
    public void setFailureCode(FailureReasons failureCode)
    {
        this.failureCode = failureCode;
    }
    
    public boolean isPasswordAuth()
    {
        return passwordAuth;
    }
    
    public void setPasswordAuth(boolean passwordAuth)
    {
        this.passwordAuth = passwordAuth;
    }
    
    public AuthToken getTokenUsed()
    {
        return tokenUsed;
    }
    
    public void setTokenUsed(AuthToken tokenUsed)
    {
        this.tokenUsed = tokenUsed;
    }
}
