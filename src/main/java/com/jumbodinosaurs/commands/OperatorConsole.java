package com.jumbodinosaurs.commands;


import com.jumbodinosaurs.ServerController;
import com.jumbodinosaurs.devlib.commands.CommandManager;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;

import java.util.Scanner;

public class OperatorConsole implements Runnable
{
    
    //TODO make statistics manager
    
    
    public OperatorConsole()
    {
        ServerController.consoleLogger.info("Console Online");
    }
    
    public static String getEnsuredAnswer()
    {
        String ensuredAnswer = null;
        Scanner userInputScanner = new Scanner(System.in);
        String userInput = "";
        do
        {
            if(ensuredAnswer != null)
            {
                System.out.println("Re-Enter: ");
            }
            ensuredAnswer = userInputScanner.nextLine();
            System.out.println("Is this correct: \"" + ensuredAnswer + "\" (y/n)");
            userInput = userInputScanner.nextLine();
        }
        while(!userInput.toLowerCase().contains("y"));
        
        return ensuredAnswer;
    }
    
    public void run()
    {
        Scanner input = new Scanner(System.in);
        CommandManager.refreshCommands();
        while(true)
        {
            try
            {
                String userInput = "";
                userInput += input.nextLine();
                if(!userInput.equals(""))
                {
                    try
                    {
                        MessageResponse response = CommandManager.filter(userInput, true);
                        if(response == null)
                        {
                            System.out.println("Unrecognized command /help or /? for more Help." + "");
                        }
                        else
                        {
                            System.out.println(response.getMessage());
                        }
                    }
                    catch(WaveringParametersException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            catch(Exception e)
            {
                System.out.println("Un-caught Exception in Operator Console");
                e.printStackTrace();
            }
        }
    }
}
