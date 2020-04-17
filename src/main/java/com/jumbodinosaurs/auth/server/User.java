package com.jumbodinosaurs.auth.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class User
{
    private String username;
    private String hashedPassword;
    private boolean isActive;
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
    
    public User clone()
    {
        return new Gson().fromJson(new Gson().toJson(this), new TypeToken<User>() {}.getType());
    }
    
    public void setToken(AuthToken token)
    {
        for(int i = 0; i < tokens.size(); i++)
        {
            AuthToken authToken = tokens.get(i);
            if(token.getUse().equals(authToken.getUse()))
            {
                tokens.remove(i);
                i--;
            }
        }
        tokens.add(token);
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
    
    
    public boolean isActive()
    {
        return isActive;
    }
    
    public void setActive(boolean active)
    {
        isActive = active;
    }
}
