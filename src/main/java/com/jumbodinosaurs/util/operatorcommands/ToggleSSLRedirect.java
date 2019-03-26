package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.netty.SessionHandler;

public class ToggleSSLRedirect extends OperatorCommand
{
    
    
    public ToggleSSLRedirect(String command)
    {
        super(command);
    }
    
    public void execute()
    {
        if(SessionHandler.redirectToSSL)
        {
            SessionHandler.redirectToSSL = false;
            System.out.println("HTTP requests will no longer be Redirected to HTTPS");
        }
        else
        {
            SessionHandler.redirectToSSL = true;
            System.out.println("HTTP requests will now try to Redirect To HTTPS");
        }
        
    }
    
    public void execute(String parameter) {}
}
