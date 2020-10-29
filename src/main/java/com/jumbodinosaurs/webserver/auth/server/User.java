package com.jumbodinosaurs.webserver.auth.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.devlib.database.Identifiable;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;

public class User implements Identifiable
{
    private String username;
    private String base64HashedPassword;
    private boolean isActive;
    private String base64Email;
    private LocalDateTime joinDate;
    private ArrayList<AuthToken> tokens = new ArrayList<AuthToken>();
    private transient int id;
    
    
    public User(String username, String base64HashedPassword, String base64Email)
    {
        this.username = username;
        this.base64HashedPassword = base64HashedPassword;
        this.base64Email = base64Email;
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
        return new String(Base64.getDecoder().decode(base64HashedPassword.getBytes()), StandardCharsets.UTF_8);
    }
    
    public String getEmail()
    {
        return new String(Base64.getDecoder().decode(base64Email.getBytes()), StandardCharsets.UTF_8);
    }
    
    public String getBase64HashedPassword()
    {
        return base64HashedPassword;
    }
    
    public void setBase64HashedPassword(String base64HashedPassword)
    {
        this.base64HashedPassword = base64HashedPassword;
    }
    
    public String getBase64Email()
    {
        return base64Email;
    }
    
    public void setBase64Email(String base64Email)
    {
        this.base64Email = base64Email;
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
    
    public boolean removeToken(String tokenUse)
    {
        for(int i = 0; i < tokens.size(); i++)
        {
            AuthToken authToken = tokens.get(i);
            if(authToken.getUse().equals(tokenUse))
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
    
    
    @Override
    public String toString()
    {
        return "User{" +
               "username='" +
               username +
               '\'' +
               ", base64HashedPassword='" +
               base64HashedPassword +
               '\'' +
               ", isActive=" +
               isActive +
               ", base64Email='" +
               base64Email +
               '\'' +
               ", joinDate=" +
               joinDate +
               ", tokens=" +
               tokens +
               '}';
    }
    
    @Override
    public int getId()
    {
        return id;
    }
    
    @Override
    public void setId(int id)
    {
        this.id = id;
    }
}
