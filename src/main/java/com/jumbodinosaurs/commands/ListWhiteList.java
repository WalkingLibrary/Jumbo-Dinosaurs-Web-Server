package com.jumbodinosaurs.commands;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.util.OptionUtil;

public class ListWhiteList extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        String output = "White Listed IPs:\n";
        for(String ip: OptionUtil.getWhiteList())
        {
            output += "IP: " + ip + "\n";
        }
        return new MessageResponse(output);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Lists the allowed IPs for when the whitelist is on";
    }
}
