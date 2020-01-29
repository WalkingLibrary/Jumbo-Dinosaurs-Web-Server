package com.jumbodinosaurs.tasks;

import com.jumbodinosaurs.netty.handler.SessionHandler;

public class InitilizeSessionHandler implements Runnable
{
    private SessionHandler sessionHandler;
    
    public InitilizeSessionHandler(SessionHandler sessionHandler)
    {
        this.sessionHandler = sessionHandler;
    }
    
    @Override
    public void run()
    {
    
    }
}
