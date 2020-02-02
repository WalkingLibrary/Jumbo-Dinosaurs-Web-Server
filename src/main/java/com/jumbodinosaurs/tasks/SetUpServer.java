package com.jumbodinosaurs.tasks;

import com.jumbodinosaurs.netty.handler.SessionHandler;
import com.jumbodinosaurs.util.ServerUtil;

public class SetUpServer extends ServerTask
{
    private SessionHandler sessionHandler;
    
    public SetUpServer(SessionHandler sessionHandler)
    {
        this.sessionHandler = sessionHandler;
    }
    
    @Override
    public void run()
    {
        /*
         *
         *
         *
         */
        
        ServerUtil.setHost();
        
    }
}
