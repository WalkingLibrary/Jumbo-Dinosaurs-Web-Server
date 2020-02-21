package com.jumbodinosaurs.commands;


import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.util.OptionIdentifier;
import com.jumbodinosaurs.util.OptionUtil;

public class ToggleAllowPost extends Command
{
    
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        String outputMessage = "";
        boolean allowPostCurrentState = OptionUtil.allowPost();
        Option<Boolean> updatedAllowPostState = new Option<Boolean>(!allowPostCurrentState,
                                                                    OptionIdentifier.allowPost.getIdentifier());
        OptionUtil.setOption(updatedAllowPostState);
        if(OptionUtil.allowPost())
        {
            outputMessage += "The server will now accept posts" + "\n";
        }
        else
        {
            outputMessage += "The server will no longer accept posts" + "\n";
        }
        
        
        return new MessageResponse(outputMessage);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Toggles the Servers ability to accept post requests";
    }
}
