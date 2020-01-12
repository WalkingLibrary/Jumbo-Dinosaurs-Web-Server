package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.util.OperatorConsole;

public class ToggleDebug extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        String outputMessage = "";
        if(OperatorConsole.debug)
        {
            outputMessage += "Debug mode is now off" + "\n";
        }
        else
        {
            outputMessage += "Debug mode is now on" + "\n";
        }
        
        OperatorConsole.debug = !OperatorConsole.debug;
        
        return new MessageResponse(outputMessage);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Toggles the server's debug state";
    }
}
