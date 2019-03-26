package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.util.DataController;
import com.jumbodinosaurs.util.OperatorConsole;

import java.time.LocalDateTime;

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
        System.out.println("Total Hits: " + OperatorConsole.totalHits);
        System.out.println("Hits Today: " + OperatorConsole.hitsToday);
        System.out.println("Exceptions: " + OperatorConsole.exceptions);
        System.out.println("Users: " + DataController.getCredentialsManager().getUserCount());
    }
}
