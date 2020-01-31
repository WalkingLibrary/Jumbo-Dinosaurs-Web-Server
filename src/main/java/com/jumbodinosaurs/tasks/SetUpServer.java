package com.jumbodinosaurs.tasks;

import com.jumbodinosaurs.netty.handler.SessionHandler;

public class SetUpServer implements Runnable
{
    private SessionHandler sessionHandler;
    
    public SetUpServer(SessionHandler sessionHandler)
    {
        this.sessionHandler = sessionHandler;
    }
    
    @Override
    public void run()
    {
    
    }
}
