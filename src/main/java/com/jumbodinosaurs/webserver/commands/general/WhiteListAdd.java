package com.jumbodinosaurs.webserver.commands.general;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.webserver.commands.OperatorConsole;
import com.jumbodinosaurs.webserver.util.OptionIdentifier;
import com.jumbodinosaurs.webserver.util.OptionUtil;

import java.util.ArrayList;

public class WhiteListAdd extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        try
        {
            System.out.println("Enter IP:");
            String ip = OperatorConsole.getEnsuredAnswer();
            ArrayList<String> whiteList = OptionUtil.getWhiteList();
            whiteList.add(ip);
            Option<ArrayList<String>> whiteListedIps = new Option<ArrayList<String>>(whiteList, OptionIdentifier.whiteList.getIdentifier());
            OptionUtil.setOption(whiteListedIps);
            return new MessageResponse("I.P. added: " + ip + "\n");
        }
        catch(Exception e)
        {
            throw new WaveringParametersException(e);
        }
    }
    
    @Override
    public String getHelpMessage()
    {
        return null;
    }
}
