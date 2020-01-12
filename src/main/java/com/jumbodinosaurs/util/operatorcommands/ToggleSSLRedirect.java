package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.util.OperatorConsole;

public class ToggleSSLRedirect extends OperatorCommand
{
    
    
    public ToggleSSLRedirect(String command)
    {
        super(command);
    }
    
    public void execute()
    {
        if(OperatorConsole.redirectToSSL)
        {
            OperatorConsole.redirectToSSL = false;
            System.out.println("HTTP requests will no longer be Redirected to HTTPS");
        }
        else
        {
            OperatorConsole.redirectToSSL = true;
            System.out.println("HTTP requests will now try to Redirect To HTTPS");
        }
        
    }
    
    public void execute(String parameter) {}
}
