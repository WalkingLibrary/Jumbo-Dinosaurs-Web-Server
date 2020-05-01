package com.jumbodinosaurs.auth.server;

import com.jumbodinosaurs.util.PasswordStorage;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;

public class AuthToken
{
    private String use;
    private LocalDateTime mintDate;
    private LocalDateTime expirationDate;
    private String ip;
    private String base64HashedToken;
    
    public AuthToken(String use,
                     String ip,
                     String token,
                     LocalDateTime expirationDate) throws PasswordStorage.CannotPerformOperationException
    {
        this.use = use;
        this.ip = ip;
        this.mintDate = LocalDateTime.now();
        this.expirationDate = expirationDate;
        this.base64HashedToken = Base64.getEncoder()
                                       .encodeToString(PasswordStorage.createHash(getHashString(token)).getBytes());
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
    
        String hashedToken = new String(Base64.getDecoder().decode(base64HashedToken.getBytes()),
                                        StandardCharsets.UTF_8);
    
        if(!PasswordStorage.verifyPassword(getHashString(token), hashedToken))
        {
            return false;
        }
    
        return true;
    }
    
    public boolean hasExpired()
    {
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(this.expirationDate);
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
                       Objects.equals(base64HashedToken, authToken.base64HashedToken);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(use, mintDate, expirationDate, ip, base64HashedToken);
    }
    
    @Override
    public String toString()
    {
        return "AuthToken{" + "use='" + use + '\'' + ", mintDate=" + mintDate + ", expirationDate=" + expirationDate + ", ip='" + ip + '\'' + ", base64HashedToken='" + base64HashedToken + '\'' + '}';
    }
}
