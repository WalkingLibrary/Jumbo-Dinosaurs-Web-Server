package com.jumbodinosaurs.objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;

public class User
{
    private String username;
    private String password;
    private String token;
    private String tokenRandom;
    private String tokenRandomToSend;
    private String emailCode;
    private String email;
    private boolean tokenIsOneUse;
    private boolean emailVerified;
    private boolean accountLocked;
    private LocalDateTime joinDate;
    private LocalDateTime emailDateTime;
    private LocalDateTime lastLoginDate;
    private LocalDateTime tokenDate;
    private String version;
    
    public User(String username, String password, String email, boolean emailVerified, LocalDateTime joinDate)
    {
        this.username = username;
        this.password = password;
        this.email = email;
        this.emailVerified = emailVerified;
        this.joinDate = joinDate;
        this.version = ".1";
    }
    
    public User(String username, String password, String token, String tokenRandom, String emailCode, String email, boolean tokenIsOneUse, boolean emailVerified, boolean accountLocked, LocalDateTime joinDate, LocalDateTime emailDateTime, LocalDateTime lastLoginDate, LocalDateTime tokenDate, String version)
    {
        this.username = username;
        this.password = password;
        this.token = token;
        this.tokenRandom = tokenRandom;
        this.emailCode = emailCode;
        this.email = email;
        this.tokenIsOneUse = tokenIsOneUse;
        this.emailVerified = emailVerified;
        this.accountLocked = accountLocked;
        this.joinDate = joinDate;
        this.emailDateTime = emailDateTime;
        this.lastLoginDate = lastLoginDate;
        this.tokenDate = tokenDate;
        this.version = version;
    }
    
    
    public static String generateRandom()
    {
        String whiteListedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
        String random = "";
        while(random.length() <= 100)
        {
            int randomNumber = (int) (Math.random() * whiteListedCharacters.length());
            random += whiteListedCharacters.toCharArray()[randomNumber];
        }
        return random;
    }
    
    
    public static String generateRandomEmailCode()
    {
        String whiteListedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
        String random = "";
        while(random.length() <= 10)
        {
            int randomNumber = (int) (Math.random() * whiteListedCharacters.length());
            random += whiteListedCharacters.toCharArray()[randomNumber];
        }
        return random;
    }
    
    public User clone()
    {
        User clone = new Gson().fromJson(new Gson().toJson(this), this.getClass());
        return clone;
    }
    
    
    public String getUserInfoJson()
    {
        JsonObject info = new JsonObject();
        info.addProperty("username", this.username);
        info.addProperty("joindate", this.joinDate.toString());
        info.addProperty("emailVerified", this.emailVerified);
        //Debug
        return info.toString();
    }
    
    
   
    
    
    public boolean equals(User user)
    {
        String thisJSON = new Gson().toJson(this);
        String userJSON = new Gson().toJson(user);
        return thisJSON.equals(userJSON);
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public void setToken(String token)
    {
        this.token = token;
    }
    
    public String getTokenRandom()
    {
        return tokenRandom;
    }
    
    public void setTokenRandom(String tokenRandom)
    {
        this.tokenRandom = tokenRandom;
    }
    
    public String getEmailCode()
    {
        return emailCode;
    }
    
    public void setEmailCode(String emailCode)
    {
        this.emailCode = emailCode;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public boolean isTokenIsOneUse()
    {
        return tokenIsOneUse;
    }
    
    public void setTokenIsOneUse(boolean tokenIsOneUse)
    {
        this.tokenIsOneUse = tokenIsOneUse;
    }
    
    public boolean isEmailVerified()
    {
        return emailVerified;
    }
    
    public void setEmailVerified(boolean emailVerified)
    {
        this.emailVerified = emailVerified;
    }
    
    public boolean isAccountLocked()
    {
        return accountLocked;
    }
    
    public void setAccountLocked(boolean accountLocked)
    {
        this.accountLocked = accountLocked;
    }
    
    public LocalDateTime getJoinDate()
    {
        return joinDate;
    }
    
    public void setJoinDate(LocalDateTime joinDate)
    {
        this.joinDate = joinDate;
    }
    
    public LocalDateTime getEmailDateTime()
    {
        return emailDateTime;
    }
    
    public void setEmailDateTime(LocalDateTime emailDateTime)
    {
        this.emailDateTime = emailDateTime;
    }
    
    public LocalDateTime getLastLoginDate()
    {
        return lastLoginDate;
    }
    
    public void setLastLoginDate(LocalDateTime lastLoginDate)
    {
        this.lastLoginDate = lastLoginDate;
    }
    
    public LocalDateTime getTokenDate()
    {
        return tokenDate;
    }
    
    public void setTokenDate(LocalDateTime tokenDate)
    {
        this.tokenDate = tokenDate;
    }
    
    public String getTokenRandomToSend()
    {
        return tokenRandomToSend;
    }
    
    public void setTokenRandomToSend(String tokenRandomToSend)
    {
        this.tokenRandomToSend = tokenRandomToSend;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
}
