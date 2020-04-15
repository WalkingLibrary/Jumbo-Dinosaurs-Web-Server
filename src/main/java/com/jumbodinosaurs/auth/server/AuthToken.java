package com.jumbodinosaurs.auth.server;

import com.jumbodinosaurs.util.PasswordStorage;

import java.time.LocalDateTime;
import java.util.Objects;

public class AuthToken
{
    private String use;
    private LocalDateTime mintDate;
    private LocalDateTime expirationDate;
    private String ip;
    private String hash;
    
    public AuthToken(String use,
                     String ip,
                     String token,
                     LocalDateTime expirationDate) throws PasswordStorage.CannotPerformOperationException
    {
        this.use = use;
        this.ip = ip;
        this.mintDate = LocalDateTime.now();
        this.expirationDate = expirationDate;
        this.hash = PasswordStorage.createHash(getHashString(token));
    }
    
    public String getHashString(String token)
    {
        return this.ip + this.mintDate + token;
    }
    
    public LocalDateTime getMintDate()
    {
        return mintDate;
    }
    
    public boolean isValidToken(String token) throws PasswordStorage.InvalidHashException,
                                                             PasswordStorage.CannotPerformOperationException
    {
        if(LocalDateTime.now().isAfter(expirationDate))
        {
            return false;
        }
    
    
        if(!PasswordStorage.verifyPassword(getHashString(token), hash))
        {
            return false;
        }
    
        return true;
    }
    
    public String getUse()
    {
        return use;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        AuthToken authToken = (AuthToken) o;
        return Objects.equals(use, authToken.use) &&
                       Objects.equals(mintDate, authToken.mintDate) &&
                       Objects.equals(expirationDate, authToken.expirationDate) &&
                       Objects.equals(ip, authToken.ip) &&
                       Objects.equals(hash, authToken.hash);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(use, mintDate, expirationDate, ip, hash);
    }
}
