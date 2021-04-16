package com.jumbodinosaurs.webserver.commands.general;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.devlib.util.OperatorConsole;
import com.jumbodinosaurs.webserver.util.OptionIdentifier;
import com.jumbodinosaurs.webserver.util.OptionUtil;

import java.util.ArrayList;

public class addHiddenDir extends Command
{
    
    @Override
    public MessageResponse getExecutedMessage()
            throws WaveringParametersException
    {
        System.out.println("Enter a new Dir");
        String hiddenDirName = OperatorConsole.getEnsuredAnswer();
        
        Option<ArrayList<String>> hiddenDirsOption = new Option<ArrayList<String>>(OptionUtil.getAllowedHiddenDirs(),
                                                                                   OptionIdentifier.hiddenDirs.getIdentifier());
        hiddenDirsOption.getOption().add(hiddenDirName);
        OptionUtil.setOption(hiddenDirsOption);
        
        return new MessageResponse(hiddenDirName + " was added to the allowed Hidden Dirs List");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Sets the dir where Files are retrieved from";
    }
}
