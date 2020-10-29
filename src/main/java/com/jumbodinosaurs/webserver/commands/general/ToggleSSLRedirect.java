package com.jumbodinosaurs.webserver.commands.general;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.webserver.util.OptionIdentifier;
import com.jumbodinosaurs.webserver.util.OptionUtil;

public class ToggleSSLRedirect extends Command
{
    
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        boolean currentStateOfRedirects = OptionUtil.shouldUpgradeInsecureConnections();
        Option<Boolean> updatedState = new Option<Boolean>(!currentStateOfRedirects,
                                                           OptionIdentifier.shouldUpgradeInsecureConnections.getIdentifier());
        OptionUtil.setOption(updatedState);
        String outputMessage = "";
        if(OptionUtil.shouldUpgradeInsecureConnections())
        {
            outputMessage += "Insecure Connections will be redirected to secure connections" + "\n";
        }
        else
        {
            outputMessage += "Insecure Connections will not be redirected to secure connections"+ "\n";
        }
        return new MessageResponse(outputMessage);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Toggles the servers ability to redirect users to a secure connection";
    }
}
