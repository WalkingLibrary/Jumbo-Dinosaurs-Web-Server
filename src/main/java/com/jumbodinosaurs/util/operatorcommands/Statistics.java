package com.jumbodinosaurs.util.operatorcommands;


import com.jumbodinosaurs.util.DataController;
import com.jumbodinosaurs.util.OperatorConsole;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Statistics extends OperatorCommand
{
    
    public Statistics(String command)
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
    }
}
