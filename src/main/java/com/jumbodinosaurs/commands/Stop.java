package com.jumbodinosaurs.commands;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.log.SessionLogger;
import com.jumbodinosaurs.util.PostWriter;

public class Stop extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        if(SessionLogger.sessions != null)
        {
            while(SessionLogger.sessions.size() > 0 && PostWriter.postsToWrite.size() > 0)
            {
            
            }
        }
        System.out.println("Shutting Down");
        System.exit(3);
        return new MessageResponse("If you see this something is wrong");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Shuts down the web server in a graceful manner";
    }
}
