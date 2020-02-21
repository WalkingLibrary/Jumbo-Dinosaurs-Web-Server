package com.jumbodinosaurs.commands;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.util.OptionIdentifier;
import com.jumbodinosaurs.util.OptionUtil;

public class ToggleDebugMode extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        
        Option<Boolean> debugMode = new Option<Boolean>(!OptionUtil.isInDebugMode(), OptionIdentifier.debugMode.getIdentifier());
        OptionUtil.setOption(debugMode);
        String outputMessage = "";
        if(OptionUtil.isInDebugMode())
        {
            outputMessage += "The server is now in debugMode"+ "\n";
        }
        else
        {
            outputMessage += "The server is no longer in debugMode"+ "\n";
        }
        return new MessageResponse(outputMessage);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Toggles the servers ability to send debug messages";
    }
}
