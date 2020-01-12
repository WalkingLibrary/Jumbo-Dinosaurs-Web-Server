package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.util.OperatorConsole;

public class ToggleDebugMessages extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        String outputMessage = "";
        if(OperatorConsole.debug)
        {
            outputMessage += "Debug Messages will not be displayed"+ "\n";
        }
        else
        {
            outputMessage += "Debug Messages will be displayed"+ "\n";
        }
        OperatorConsole.debug = !OperatorConsole.debug;
        return new MessageResponse(outputMessage);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Toggles the servers ability to send debug messages";
    }
}
