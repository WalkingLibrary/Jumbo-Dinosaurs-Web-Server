package com.jumbodinosaurs.webserver.commands.general;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.devlib.util.OperatorConsole;
import com.jumbodinosaurs.webserver.util.OptionIdentifier;
import com.jumbodinosaurs.webserver.util.OptionUtil;

import java.io.File;
import java.util.ArrayList;

public class addGETDir extends Command
{
    @Override
    public MessageResponse getExecutedMessage()
            throws WaveringParametersException
    {
        System.out.println("Enter a new Dir");
        String path = OperatorConsole.getEnsuredAnswer();
        
        Option<ArrayList<String>> getDirPaths = new Option<ArrayList<String>>(OptionUtil.getGETDirPaths(),
                                                                              OptionIdentifier.getDirPath.getIdentifier());
        try
        {
            File newDir = new File(path);
            if(!newDir.isDirectory())
            {
                return new MessageResponse(path + "\nPath Given was not a Directory.");
            }
            getDirPaths.getOption().add(newDir.getAbsolutePath());
        }
        catch(NullPointerException e)
        {
            return new MessageResponse(path + "\nFile Path given doesn't exist");
        }
        
        OptionUtil.setOption(getDirPaths);
        
        return new MessageResponse(path + " was added to GET Paths List");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Sets the dir where Files are retrieved from";
    }
}
