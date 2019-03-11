package com.jumbodinosaurs;

public class User
{
    private String username;
    private String password;
    private String tokenDate;
    private String tokenRandom;

    private String email;
    private boolean emailVerified;
    private String emailCode;
    private String emailCodeSentDate;
    private String emailCodeSentTime;
    private int emailCodesSentPastHour;


    public User(String username, String password, String tokenDate, String tokenRandom, String email, boolean emailVerified)
    {
        this.username = username;
        this.password = password;
        this.tokenDate = tokenDate;
        this.tokenRandom = tokenRandom;
        this.email = email;
        this.emailVerified = emailVerified;
    }


    public static String generateRandom()
    {
        String whiteListedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
        String random = "";
        while (random.length() <= 100)
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
        while (random.length() <= 10)
        {
            int randomNumber = (int) (Math.random() * whiteListedCharacters.length());
            random += whiteListedCharacters.toCharArray()[randomNumber];
        }
        return random;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getUsername()
    {
        return this.username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()

    {
        return this.password;
    }

    public void setPassword(String password)
    {
        this.password = password;
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

    public boolean equals(User user)
    {
        if (this.password == user.password)
        {
            if (this.username == user.getUsername())
            {
                if (this.email == user.email)
                {
                    if (this.tokenRandom == user.tokenRandom)
                    {
                        if (this.tokenDate == user.getTokenDate())
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public String getEmailCode()
    {
        return emailCode;
    }

    public void setEmailCode(String emailCode)
    {
        this.emailCode = emailCode;
    }

    public String getEmailCodeSentDate()
    {
        return emailCodeSentDate;
    }

    public void setEmailCodeSentDate(String emailCodeSentDate)
    {
        this.emailCodeSentDate = emailCodeSentDate;
    }

    public String getEmailCodeSentTime()
    {
        return emailCodeSentTime;
    }

    public void setEmailCodeSentTime(String emailCodeSentTime)
    {
        this.emailCodeSentTime = emailCodeSentTime;
    }

    public int getEmailCodesSentPastHour()
    {
        return emailCodesSentPastHour;
    }

    public void setEmailCodesSentPastHour(int emailCodesSentPastHour)
    {
        this.emailCodesSentPastHour = emailCodesSentPastHour;
    }
}
