package com.jumbodinosaurs.util;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.objects.Session;
import com.jumbodinosaurs.util.operatorcommands.*;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class OperatorConsole implements Runnable
{
    
    
    public static boolean debug;
    public static int hitsToday = 0;
    public static LocalDate today = LocalDate.now();
    public static int totalHits = 0;
    public static int exceptions = 0;
    public static boolean allowPost = true;
    public static boolean whitelist = false;
    public static ArrayList<String> whitelistedIps = new ArrayList<String>();
    public static boolean redirectToSSL;
    public static boolean sslThreadRunning;
    
    
    public OperatorConsole()
    {
        exceptions = 0;
        debug = true;
        
        File[] oldSessionsLogs = DataController.listFilesRecursive(DataController.checkFor(DataController.logsDirectory,
                                                                                           "Session " + "Logs"));
        ArrayList<Session> pastSession = new ArrayList<Session>();
        for(File logFile: oldSessionsLogs)
        {
            String fileContents = DataController.getFileContents(logFile);
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
        //DataController.writeSilentConsole(message);
        if(exception)
        {
            exceptions++;
        }
        
        
        if(debugMessage)
        {
            if(debug)
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
    
    public static boolean allowPost()
    {
        return allowPost;
    }
    
    public void run()
    {
        Scanner input = new Scanner(System.in);
        ArrayList<OperatorCommand> commands = new ArrayList<OperatorCommand>();
        
        commands.add(new Stop("/stop"));
        commands.add(new ToggleSSLRedirect("/togglesslredirect"));
        commands.add(new ToggleUserLock("/toggleuserlock"));
        commands.add(new ToggleWhiteList("/togglewhitelist"));
        commands.add(new WhiteListAdd("/whitelistadd"));
        commands.add(new Statistics("/stats"));
        commands.add(new ToggleDebugMessages("/toggledebugmessages"));
        commands.add(new StatisticsExtra("/statsextra"));
        //add Help Commands
        ArrayList<String> commandsToOutput = new ArrayList<String>();
        for(OperatorCommand command : commands)
        {
            commandsToOutput.add(command.getCommand());
        }
        commands.add(new Help("/help", commandsToOutput));
        commands.add(new Help("/?", commandsToOutput));
        
        
        while(true)
        {
            String userInput = "";
            userInput += input.nextLine();
            
            if(userInput != null && userInput.length() >= 1 && userInput.substring(0, 1).equals("/"))
            {
                boolean commandExecuted = false;
                int index = userInput.length();
                if(userInput.contains(" "))
                {
                    index = userInput.indexOf(" ");
                }
                String userInputCommand = userInput.substring(0,index);
                for(OperatorCommand command : commands)
                {
                    if(userInput.length() >= command.getCommand().length())
                    {
                        if(userInputCommand.equals(command.getCommand()))
                        {
                            if(command instanceof OperatorCommandWithParameter)
                            {
                                String parameter = userInput.substring(command.getCommand().length() + 1);
                                ((OperatorCommandWithParameter) command).setParameter(parameter);
                                command.execute();
                                commandExecuted = true;
                                break;
                            }
                            else
                            {
                                command.execute();
                                commandExecuted = true;
                                break;
                            }
                        }
                    }
                }
                
                if(!commandExecuted)
                {
                    System.out.println("Unrecognized command /help or /? for more Help." + "");
                }
            }
            else
            {
                System.out.println("Unrecognized command /help or /? for more Help." + "");
            }
        }
    }
}
