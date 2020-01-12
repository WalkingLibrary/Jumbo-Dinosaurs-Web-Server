package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.ServerControl;
import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.objects.Domain;
import com.jumbodinosaurs.objects.Email;
import com.jumbodinosaurs.objects.RuntimeArguments;
import com.jumbodinosaurs.util.DataController;
import com.jumbodinosaurs.util.OperatorConsole;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class StatisticsExtra extends Command
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
        outputMessage += "Time for The Server: " + LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "\n";
        outputMessage += "Users: " + DataController.getCredentialsManager().getUserCount() + "\n";
        outputMessage += "Amount of Posts: " + DataController.getAllPostsList().size() + "\n";
        if(ServerControl.getArguments() != null)
        {
            RuntimeArguments args = ServerControl.getArguments();
            
            outputMessage += "Server in Test Mode: " + args.isInTestMode() + "\n";
            if(args.getDomains() != null && args.getDomains().size() > 0)
            {
                outputMessage += "Domains Hosted: " + "\n";
                for(Domain domain : args.getDomains())
                {
                    outputMessage += domain.getDomain() + "\n";
                }
            }
            
            if(args.getEmails() != null && args.getEmails().size() > 0)
            {
                outputMessage += "Emails In Service: " + "\n";
                for(Email email : args.getEmails())
                {
                    outputMessage += email.getUsername() + "\n";
                }
            }
            
        }
        outputMessage += "White List Enabled: " + OperatorConsole.whitelist + "\n";
        
        if(OperatorConsole.whitelistedIps.size() > 0)
        {
            outputMessage += "White Listed I.P.s: " + "\n";
            for(String ip : OperatorConsole.whitelistedIps)
            {
                outputMessage += "I.P.: " + ip + "\n";
            }
        }
        else
        {
            outputMessage += "No White Listed I.P.s" + "\n";
        }
        
        return new MessageResponse(outputMessage);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Shows a basic detailing of the web server and some extra stats";
    }
    
}
