package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.devlib.commands.CommandWithParameters;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.util.OperatorConsole;

public class WhiteListAdd extends CommandWithParameters
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        try
        {
            String ip = this.getParameters().get(0).getParameter();
            OperatorConsole.whitelistedIps.add(ip);
            String outputMessage = "I.P. added: " + ip + "\n";
            return new MessageResponse(outputMessage);
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
