package com.jumbodinosaurs.commands;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.util.OptionIdentifier;
import com.jumbodinosaurs.util.OptionUtil;

public class ToggleWhiteList extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        
        boolean isWhiteListOnCurrentValue = OptionUtil.isWhiteListOn();
        Option<Boolean> updatedIsWhiteListOn = new Option<Boolean>(!isWhiteListOnCurrentValue,
                                                                   OptionIdentifier.isWhiteListOn.getIdentifier());
        OptionUtil.setOption(updatedIsWhiteListOn);
        String outputMessage = "";
        
        if(OptionUtil.isWhiteListOn())
        {
            outputMessage += "White list is now on"+ "\n";
        }
        else
        {
            outputMessage += "White list is now off" + "\n";
        }
        return new MessageResponse(outputMessage);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Toggles the servers whitelist";
    }
}
