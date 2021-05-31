package com.jumbodinosaurs.webserver.post.keepalive;

import com.jumbodinosaurs.webserver.auth.util.AuthSession;

public class KeepAliveSession
{
    private AuthSession authSession;
    
    public KeepAliveSession(AuthSession authSession)
    {
        this.authSession = authSession;
    }
    
    public AuthSession getAuthSession()
    {
        return authSession;
    }
}
