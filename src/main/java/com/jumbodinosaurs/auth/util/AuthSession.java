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
    
    public AuthSession(User user)
    {
        this.user = user;
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
        if(user != null)
        {
            return user.clone();
        }
        return null;
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
    
    @Override
    public String toString()
    {
        return "AuthSession{" + "success=" + success + ", user=" + user + ", passwordAuth=" + passwordAuth + ", tokenUsed=" + tokenUsed + ", failureCode=" + failureCode + '}';
    }
}
