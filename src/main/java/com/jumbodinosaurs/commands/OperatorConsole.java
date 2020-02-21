package com.jumbodinosaurs.commands;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.devlib.commands.CommandManager;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.log.Session;
import com.jumbodinosaurs.util.OptionUtil;
import com.jumbodinosaurs.util.ServerUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class OperatorConsole implements Runnable
{
    
    //TODO make statistics manager
    public static int hitsToday = 0;
    public static LocalDate today = LocalDate.now();
    public static int totalHits = 0;
    public static int exceptions = 0;
    public static boolean redirectToSSL;
    public static boolean sslThreadRunning;
    
    
    public OperatorConsole()
    {
        
        exceptions = 0;
        File[] oldSessionsLogs = GeneralUtil.listFilesRecursive(GeneralUtil.checkFor(ServerUtil.logsDirectory,
                                                                                     "Session " + "Logs"));
        ArrayList<Session> pastSession = new ArrayList<Session>();
        for(File logFile: oldSessionsLogs)
        {
            String fileContents = GeneralUtil.scanFileContents(logFile);
            Type type = new TypeToken<ArrayList<Session>>()
            {}.getType();
            ArrayList<Session> sessions = new Gson().fromJson(fileContents, type);
            pastSession.addAll(sessions);
        }
        
        if(pastSession != null)
        {
            totalHits = pastSession.size();
            hitsToday = 0;
            
            for(Session session : pastSession)
            {
                LocalDateTime when = session.getDateTime();
                if(when != null && today.isEqual(when.toLocalDate()))
                {
                    hitsToday++;
                }
            }
        }
        
        System.out.println("Console Online");
    }
    
    
    public static synchronized void printMessageFiltered(String message,
                                                         boolean debugMessage,
                                                         boolean exception)
    {
        //ServerUtil.writeSilentConsole(message);
        if(exception)
        {
            exceptions++;
        }
        
        
        if(debugMessage)
        {
            if(OptionUtil.isInDebugMode())
            {
                System.out.println(message);
            }
        }
        else
        {
            System.out.println(message);
        }
        
    }
    
    public static void addHit(Session session)
    {
        LocalDateTime sessionDate = session.getDateTime();
        if(today.isEqual(sessionDate.toLocalDate()))
        {
            hitsToday++;
            totalHits++;
        }
        else
        {
            updateTodaysDate();
            hitsToday++;
            totalHits++;
        }
    }
    
    public static void updateTodaysDate()
    {
        LocalDate now = LocalDate.now();
        if(!today.equals(now))
        {
            today = now;
            hitsToday = 0;
        }
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
