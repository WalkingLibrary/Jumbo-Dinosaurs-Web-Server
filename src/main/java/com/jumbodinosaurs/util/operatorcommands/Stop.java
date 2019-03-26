package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.util.SessionLogger;

public class Stop extends OperatorCommand
{
    
    public Stop(String command)
    {
        super(command);
    }
    
    public void execute()
    {
        if(SessionLogger.sessions != null)
        {
            while(SessionLogger.sessions.size() > 0)
            {
            
            }
        }
        System.out.println("Shutting Down");
        System.exit(3);
    }
}
