package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.ServerControl;
import com.jumbodinosaurs.objects.Domain;
import com.jumbodinosaurs.objects.Email;
import com.jumbodinosaurs.objects.RuntimeArguments;
import com.jumbodinosaurs.util.DataController;
import com.jumbodinosaurs.util.OperatorConsole;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class StatisticsExtra extends OperatorCommand
{
    public StatisticsExtra(String command)
    {
        super(command);
    }
    
    public void execute()
    {
        OperatorConsole.updateTodaysDate();
        System.out.println("As of " + LocalDateTime.now().toString());
        System.out.println("Public I.P.: " + DataController.host);
        System.out.println("Total Hits: " + OperatorConsole.totalHits);
        System.out.println("Hits Today: " + OperatorConsole.hitsToday);
        System.out.println("Exceptions: " + OperatorConsole.exceptions);
        System.out.println("Debug Messages Will Be Shown: " + OperatorConsole.debug);
        System.out.println("Time for The Server: " + LocalTime.now().getHour()  + ":" + LocalTime.now().getMinute());
        System.out.println("Users: " + DataController.getCredentialsManager().getUserCount());
        System.out.println("Amount of Posts: " + DataController.getAllPostsList().size());
        if(ServerControl.getArguments() != null)
        {
            RuntimeArguments args = ServerControl.getArguments();
            
            System.out.println("Server in Test Mode: " + args.isInTestMode());
            if(args.getDomains() != null && args.getDomains().size() > 0)
            {
                System.out.println("Domains Hosted: ");
                for(Domain domain: args.getDomains())
                {
                    System.out.println(domain.getDomain());
                }
            }
            
            if(args.getEmails() != null && args.getEmails().size() > 0)
            {
                System.out.println("Emails In Service: ");
                for(Email email: args.getEmails())
                {
                    System.out.println(email.getUsername());
                }
            }
            
        }
        System.out.println("White List Enabled: " + OperatorConsole.whitelist);
        
        if(OperatorConsole.whitelistedIps.size() > 0)
        {
            System.out.println("White Listed I.P.s: ");
            for(String ip : OperatorConsole.whitelistedIps)
            {
                System.out.println("I.P.: " + ip);
            }
        }
        else
        {
            System.out.println("No White Listed I.P.s");
        }
        
    }
}
