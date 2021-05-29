package com.jumbodinosaurs.webserver.commands.general;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.OptionsManager;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.devlib.util.OperatorConsole;
import com.jumbodinosaurs.webserver.util.OptionUtil;

public class SetOptions extends Command
{
    @Override
    public MessageResponse getExecutedMessage()
            throws WaveringParametersException
    {
        System.out.println("Enter the New Options Json");
        String optionsJson = OperatorConsole.getEnsuredAnswer();
        GeneralUtil.writeContents(OptionUtil.optionsJson, optionsJson, false);
        OptionUtil.setOptionsManager(new OptionsManager(OptionUtil.optionsJson));
        return new MessageResponse("The Options Json Has Been Set and the Options Manager has been Reset");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Set all The Options At Once";
    }
}
