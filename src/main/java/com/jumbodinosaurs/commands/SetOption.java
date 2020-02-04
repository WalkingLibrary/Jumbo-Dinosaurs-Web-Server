package com.jumbodinosaurs.commands;

import com.jumbodinosaurs.ServerControl;
import com.jumbodinosaurs.devlib.commands.CommandWithParameters;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.Option;

public class SetOption extends CommandWithParameters
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        if(this.getParameters().size() < 2)
        {
            return new MessageResponse("Not Enough Arguments Given");
        }
        String optionIdentifier = this.getParameters().get(0).getParameter();
        String optionValue = this.getParameters().get(1).getParameter();
        ServerControl.optionsManager.setOption(new Option<String>(optionValue, optionIdentifier));
        return new MessageResponse("Set " + optionIdentifier + " to " + optionValue);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Allows the user to set an option";
    }
}
