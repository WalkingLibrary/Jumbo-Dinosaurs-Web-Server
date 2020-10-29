package com.jumbodinosaurs.webserver.commands.general;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;

public class Stop extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Shutting Down");
        System.exit(0);
        return new MessageResponse("If you see this something is wrong");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Shuts down the web server in a graceful manner";
    }
}
