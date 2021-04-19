package com.jumbodinosaurs.webserver.commands.discord;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.devlib.util.OperatorConsole;
import com.jumbodinosaurs.webserver.util.OptionIdentifier;
import com.jumbodinosaurs.webserver.util.OptionUtil;

public class SetWebHook extends Command
{
    @Override
    public MessageResponse getExecutedMessage()
            throws WaveringParametersException
    {
        System.out.println("Enter the Webhook: ");
        String webHook = OperatorConsole.getEnsuredAnswer();
        Option<String> currentWebHook = OptionUtil.optionsManager.getOption(OptionIdentifier.webhook.getIdentifier(),
                                                                            "");
        currentWebHook.setOption(webHook);
        OptionUtil.setOption(currentWebHook);
        return new MessageResponse("Set the Web Hook");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Sets the Web Hook Errors are sent to";
    }
}
