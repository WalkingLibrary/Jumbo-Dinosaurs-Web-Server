package com.jumbodinosaurs.auth.util;

public class AuthUtil
{
    public static String generateRandomString(int size)
    {
        String whiteListedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
        String random = "";
        while(random.length() <= size)
        {
            int randomNumber = (int) (Math.random() * whiteListedCharacters.length());
            random += whiteListedCharacters.toCharArray()[randomNumber];
        }
        return random;
    }
    
    
}
