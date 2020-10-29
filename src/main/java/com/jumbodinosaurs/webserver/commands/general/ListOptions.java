package com.jumbodinosaurs.webserver.commands.general;


import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.webserver.util.OptionUtil;

public class ListOptions extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Options: ");
        String output = "";
        for(Option option: OptionUtil.optionsManager.getOptions())
        {
            output += option.getIdentifier();
        }
        return new MessageResponse(output);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Lists the options in the server";
    }
}
