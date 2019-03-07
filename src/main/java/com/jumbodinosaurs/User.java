package com.jumbodinosaurs;

public class User
{
    private String username;
    private String password;
    private String tokenDate;
    private String tokenRandom;
    private String email;
    private int loginTries;
    private boolean emailVerified;

    public User(String username, String password, String tokenDate, String tokenRandom, String email, int loginTries, boolean emailVerified)
    {
        this.username = username;
        this.password = password;
        this.tokenDate = tokenDate;
        this.tokenRandom = tokenRandom;
        this.email = email;
        this.loginTries = loginTries;
        this.emailVerified = emailVerified;
    }


    public static String generateRandom()
    {
        String whiteListedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
        String random = "";
        while(random.length() < 100)
        {
            int randomNumber = (int)(Math.random() * whiteListedCharacters.length());
            random += whiteListedCharacters.toCharArray()[randomNumber];
        }
        return random;
    }


    public int getLoginTries()
    {
        return loginTries;
    }

    public void setLoginTries(int loginTries)
    {
        this.loginTries = loginTries;
    }


    public String getEmail()
    {
        return email;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getPassword()

    {
        return this.password;
    }

    public String getTokenDate()
    {
        return tokenDate;
    }

    public void setTokenDate(String tokenDate)
    {
        this.tokenDate = tokenDate;
    }

    public boolean isEmailVerified()
    {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified)
    {
        this.emailVerified = emailVerified;
    }

    public String getTokenRandom()
    {
        return tokenRandom;
    }

    public void setTokenRandom(String tokenRandom)
    {
        this.tokenRandom = tokenRandom;
    }

}
