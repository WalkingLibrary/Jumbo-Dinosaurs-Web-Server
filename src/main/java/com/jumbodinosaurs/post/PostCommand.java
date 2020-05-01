package com.jumbodinosaurs.post;

import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.email.Email;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.log.Session;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;

public abstract class PostCommand
{
    protected String ip;
    private String command;
    private Email serversEmail;
    
    public PostCommand()
    {
        this.command = this.getClass().getSimpleName();
    }
    
    public String getNameCommand()
    {
        return command;
    }
    
    public abstract HTTPResponse getResponse(PostRequest request, AuthSession authSession);
    
    public String getIp()
    {
        return ip;
    }
    
    public void setIp(String ip)
    {
        this.ip = ip;
    }
    
    public abstract boolean requiresSuccessfulAuth();
    
    public abstract boolean requiresPasswordAuth();
    
    public abstract boolean requiresUser();
    
    public void setServersEmail(Email serversEmail)
    {
        this.serversEmail = serversEmail;
    }
    
    public Email getServersEmail()
    {
        return serversEmail;
    }
}
