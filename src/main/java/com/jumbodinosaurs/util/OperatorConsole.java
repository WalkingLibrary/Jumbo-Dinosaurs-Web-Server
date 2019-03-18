package com.jumbodinosaurs.util;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.netty.SessionHandler;
import com.jumbodinosaurs.objects.Session;
import com.jumbodinosaurs.objects.User;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class OperatorConsole implements Runnable
{
    private static final String[] commands = {"/?", "/help",// 0 1
            "/placeholder", "/cleardomains", // 2 3
            "/adddomain", "/editdomain", // 4 5
            "/stop", "/stats",//6 7
            "/toggledebug", "/togglesslredirect",
            "/toggleallowpost", "/toggleuserlock ",
    "/togglewhitelist", "/whitelistadd"};//8 9
    private ArrayList<String> queue = new ArrayList<String>();



    //private String mostRequestedFile;
    private static boolean debug;
    private static int hitsToday = 0;
    private static LocalDate today = LocalDate.now();
    private static int totalHits = 0;
    private static int exceptions = 0;
    private static boolean allowPost = true;
    public static boolean whitelist = false;
    public static ArrayList<String> whitelistedIps = new ArrayList<String>();


    public OperatorConsole()
    {
        this.exceptions = 0;
        this.debug = true;

        File logFile = DataController.getLogsJson();
        String fileContents = DataController.getFileContents(logFile);
        Type type = new TypeToken<ArrayList<Session>>(){}.getType();
        ArrayList<Session> sessions = new Gson().fromJson(fileContents, type);
        if(sessions != null)
        {
            this.totalHits = sessions.size();
            this.hitsToday = 0;

            for (Session session:sessions)
            {
                LocalDateTime when = session.getDateTime();
                if(when != null && this.today.isEqual(when.toLocalDate()))
                {
                    hitsToday++;
                }
            }
        }
        else
        {
            hitsToday = 0;
            totalHits = 0;
        }
        System.out.println("Console Online");
    }


    public static synchronized void printMessageFiltered(String message, boolean debugMessage, boolean exception)
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
        while (true)
        {
            String command = "";
            command += input.nextLine();
            command = command.trim().toLowerCase();

            if (command.length() >= 1 && command.substring(0, 1).equals("/"))
            {

                //Requesting Help
                if (command.contains(this.commands[0]) || command.contains(this.commands[1]))
                {
                    System.out.println("Commands: ");
                    for (String str : this.commands)
                    {
                        System.out.println(str);
                    }
                }
                else if (command.contains(commands[6]))///stop
                {
                    if(SessionLogger.sessions != null)
                    {
                        while(SessionLogger.sessions.size() > 0)
                        {

                        }
                    }
                    System.out.println("Shutting Down");
                    System.exit(3);
                }
                else if(command.contains(commands[7]))///stats
                {
                    updateTodaysDate();
                    System.out.println("Total Hits: " + totalHits);
                    System.out.println("Hits Today: " + hitsToday);
                    System.out.println("Exceptions: " + exceptions);
                }
                else if(command.contains(commands[8]))///toggledebug
                {
                    if(this.debug)
                    {
                        System.out.println("Debug Mode is Now Off");
                        this.debug = false;
                    }
                    else
                    {
                        System.out.println("Debug Mode is Now On");
                        this.debug = true;
                    }
                }
                else if(command.contains(commands[9]))//toggleSSLRedirect
                {
                    if(SessionHandler.redirectToSSL)
                    {
                        SessionHandler.redirectToSSL = false;
                        System.out.println("HTTP requests will no longer be Redirected to HTTPS");
                    }
                    else
                    {
                        SessionHandler.redirectToSSL = true;
                        System.out.println("HTTP requests will now try to Redirect To HTTPS");
                    }

                }
                else if(command.contains(commands[10]))
                {
                    if(allowPost)
                    {
                        System.out.println("Server Will No Longer Accept Post Requests");
                    }
                    else
                    {
                        System.out.println("Server Will now Accept Post Requests");
                    }
                    allowPost = !allowPost;
                }
                else if(command.contains(commands[11]))
                {
                    String username = command.substring(commands[11].length());
                    
                    User userToLock = CredentialsManager.getUser(username);
                    if(userToLock != null)
                    {
                        User updatedUserInfo = userToLock.clone();
                        updatedUserInfo.setAccountLocked(!userToLock.isAccountLocked());
                        if(userToLock.isAccountLocked())
                        {
                            System.out.println("Unlocking: " + username);
                        }
                        else
                        {
                            System.out.println("Locking: " + username);
                        }
                        if(CredentialsManager.modifyUser(userToLock, updatedUserInfo))
                        {
                            System.out.println("User Toggled successfully");
                        }
                        else
                        {
                            System.out.println("User not toggled");
                        }
                    }
                    else
                    {
                        System.out.println("User Doesn't exist");
                    }
                    
                }
                else if(command.contains(commands[12]))
                {
                    if(whitelist)
                    {
                        System.out.println("White list is now off");
                    }
                    else
                    {
                        System.out.println("White list is now on");
                    }
                    whitelist = !whitelist;
                   
                    
                    
                }
                else if(command.contains(commands[13]))
                {
                    String ip = command.substring(commands[12].length() - 2);
                    whitelistedIps.add(ip);
                    System.out.println("I.P. added: " + ip);
                }
                else
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
