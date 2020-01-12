package com.jumbodinosaurs.commands;


import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.util.DataController;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Statistics extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        OperatorConsole.updateTodaysDate();
        String outputMessage = "";
        outputMessage += "As of " + LocalDateTime.now().toString() + "\n";
        outputMessage += "Public I.P.: " + DataController.host + "\n";
        outputMessage += "Total Hits: " + OperatorConsole.totalHits + "\n";
        outputMessage += "Hits Today: " + OperatorConsole.hitsToday + "\n";
        outputMessage += "Exceptions: " + OperatorConsole.exceptions + "\n";
        outputMessage += "Debug Messages Will Be Shown: " + OperatorConsole.debug + "\n";
        outputMessage += "Time for The Server: " + LocalTime.now().getHour()  + ":" + LocalTime.now().getMinute() +
                                 "\n";
        outputMessage += "Users: " + DataController.getCredentialsManager().getUserCount() + "\n";
        outputMessage += "Amount of Posts: " + DataController.getAllPostsList().size() + "\n";
        return new MessageResponse(outputMessage);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Shows a basic detailing of the web server";
    }
}
