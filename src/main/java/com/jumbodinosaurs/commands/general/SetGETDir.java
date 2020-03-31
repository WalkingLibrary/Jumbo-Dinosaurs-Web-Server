package com.jumbodinosaurs.commands.general;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.util.ServerUtil;

import java.io.File;

public class SetGETDir extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter the Path to the new GET Dir");
        String path = OperatorConsole.getEnsuredAnswer();
        ServerUtil.getDirectory = new File(path);
        return new MessageResponse("GET Dir has been changed to " + path + " for the remainder of this session");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Sets the dir where Files are retrieved from";
    }
}
