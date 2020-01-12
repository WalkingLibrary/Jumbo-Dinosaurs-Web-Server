package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.util.OperatorConsole;

public class ToggleSSLRedirect extends Command
{
    
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        String outputMessage = "";
        if(OperatorConsole.redirectToSSL)
        {
            outputMessage += "HTTP requests will no longer be Redirected to HTTPS" + "\n";
        }
        else
        {
            outputMessage += "HTTP requests will now try to Redirect To HTTPS"+ "\n";
        }
        OperatorConsole.redirectToSSL = !OperatorConsole.redirectToSSL;
        return new MessageResponse(outputMessage);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Toggles the servers ability to redirect users to a secure connection";
    }
}
