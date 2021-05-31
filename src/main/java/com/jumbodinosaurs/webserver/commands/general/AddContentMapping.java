package com.jumbodinosaurs.webserver.commands.general;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.util.OperatorConsole;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.ContentTypeUtil;

public class AddContentMapping extends Command
{
    
    @Override
    public MessageResponse getExecutedMessage()
            throws WaveringParametersException
    {
        System.out.println("Enter File Type: ");
        String fileType = OperatorConsole.getEnsuredAnswer();
        System.out.println("Enter Mapping: ");
        String mapping = OperatorConsole.getEnsuredAnswer();
        ContentTypeUtil.setMapping(fileType, mapping);
        
        return null;
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Allows the User To Add Media Mapping";
    }
}
