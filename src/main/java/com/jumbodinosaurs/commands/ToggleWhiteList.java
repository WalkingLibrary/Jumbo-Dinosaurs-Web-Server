package com.jumbodinosaurs.commands;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;

public class ToggleWhiteList extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        String outputMessage = "";
        if(OperatorConsole.whitelist)
        {
            outputMessage += "White list is now off"+ "\n";
        }
        else
        {
            outputMessage += "White list is now on" + "\n";
        }
        OperatorConsole.whitelist = !OperatorConsole.whitelist;
        return new MessageResponse(outputMessage);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Toggles the servers whitelist";
    }
}
