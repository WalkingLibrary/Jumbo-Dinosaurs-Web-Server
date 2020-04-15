package com.jumbodinosaurs.auth.server;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class User
{
    private String username;
    private String hashedPassword;
    private String email;
    private LocalDateTime joinDate;
    private ArrayList<AuthToken> tokens;
    
    
    public User(String username, String hashedPassword, String email)
    {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.joinDate = LocalDateTime.now();
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getHashedPassword()
    {
        return hashedPassword;
    }
    
    public void setHashedPassword(String hashedPassword)
    {
        this.hashedPassword = hashedPassword;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public LocalDateTime getJoinDate()
    {
        return joinDate;
    }
    
    
    public boolean addToken(AuthToken token)
    {
        for(AuthToken authToken : tokens)
        {
            if(token.getUse().equals(authToken.getUse()))
            {
                return false;
            }
        }
        tokens.add(token);
        return false;
    }
    
    public AuthToken getToken(String use)
    {
        for(AuthToken authToken : tokens)
        {
            if(authToken.getUse().equals(use))
            {
                return authToken;
            }
        }
        return null;
    }
    
    public boolean removeToken(AuthToken token)
    {
        for(int i = 0; i < tokens.size(); i++)
        {
            AuthToken authToken = tokens.get(i);
            if(authToken.equals(token))
            {
                tokens.remove(i);
                return true;
            }
        }
        return false;
    }
    
    
}
