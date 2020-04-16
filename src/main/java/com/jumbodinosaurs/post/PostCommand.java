package com.jumbodinosaurs.post;

import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.log.Session;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;

public abstract class PostCommand
{
    protected Session session;
    private String command;
    
    public PostCommand()
    {
        this.command = this.getClass().getSimpleName();
    }
    
    public String getCommand()
    {
        return command;
    }
    
    public abstract HTTPResponse getResponse(PostRequest request, AuthSession authSession);
    
    public Session getSession()
    {
        return session;
    }
    
    public void setSession(Session session)
    {
        this.session = session;
    }
}
