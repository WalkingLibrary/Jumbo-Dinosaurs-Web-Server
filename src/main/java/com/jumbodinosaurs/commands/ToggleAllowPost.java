package com.jumbodinosaurs.commands;


import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;

public class ToggleAllowPost extends Command
{
    
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        String outputMessage = "";
        if(OperatorConsole.allowPost)
        {
            outputMessage += "The server will no longer accept post requests" + "\n";
        }
        else
        {
            outputMessage += "The server will now accept post requests" + "\n";
        }
        OperatorConsole.allowPost = !OperatorConsole.allowPost;
        return new MessageResponse(outputMessage);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Toggles the Servers ability to accept post requests";
    }
}
