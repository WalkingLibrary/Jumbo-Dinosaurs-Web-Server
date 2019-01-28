package com.jumbodinosaurs;


import com.google.gson.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class OperatorConsole implements Runnable
{
    private static final String[] commands = {"/?", "/help",// 0 1
            "/reinitphotos", "/cleardomains", // 2 3
            "/adddomain", "/editdomain", // 4 5
            "/stop", "/stats",//6 7
            "/toggledebug"};//8 9
    private static DataController dataIO;
    private ArrayList<String> queue = new ArrayList<String>();



    //private String mostRequestedFile;
    private static boolean debug = false;
    private static int hitsToday = 0;
    private static Date today = new Date();
    private static int totalHits = 0;
    private static int exceptions = 0;


    public OperatorConsole(DataController dataIO)
    {
        this.dataIO = dataIO;
        this.exceptions = 0;
        this.debug = false;

        //GSON Objects for writeing and dealing with json
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        JsonParser parser = new JsonParser();

        try
        {

        String logFileContents = this.dataIO.getFileContents(this.dataIO.getLogsJson());

            JsonElement element = null;
            try
            {
                element = parser.parse(logFileContents);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Error Parsing Json");
            }



            if (element != null &&
                    element.isJsonObject() &&
                    element.getAsJsonObject().getAsJsonArray("loglist") != null &&
                    element.getAsJsonObject().getAsJsonArray("loglist").isJsonArray())
            {
                JsonArray logJsonArray = element.getAsJsonObject().getAsJsonObject().getAsJsonArray("loglist");
                //Get Total Hits
                this.totalHits = logJsonArray.size();
                //check for hits from today
                ArrayList<Session> pastSessions = new ArrayList<Session>();
                //Load old sessions to arraylist
                for (JsonElement oldSession: logJsonArray)
                {
                    pastSessions.add(gson.fromJson(oldSession.getAsString(), Session.class));
                }
                this.today = new Date();
                for(Session session: pastSessions)
                {
                    Date when = new Date(session.getWhen());
                    if(when.getDay() == this.today.getDay()
                            && when.getYear() == this.today.getYear()
                            && when.getMonth() == this.today.getMonth())
                    {
                        this.hitsToday++;
                    }
                }


            }


        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Error Initializing Console");
        }




        System.out.println("Console Online");
    }


    public static synchronized void printMessageFiltered(String message, boolean debugMessage, boolean exception)
    {
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
        Date sessionDate = new Date(session.getWhen());
        if(sessionDate.getDay() == today.getDay()
                && sessionDate.getYear() == today.getYear()
                && sessionDate.getMonth() == today.getMonth())
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
        Date now = new Date();
        if(now.getDay() == today.getDay()
                && now.getYear() == today.getYear()
                && now.getMonth() == today.getMonth())
        {
            return;
        }
        today = now;
        hitsToday = 0;
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
                //ReInit Photos
                else if (command.contains(this.commands[2]))///reinitphotos
                {
                    try
                    {
                        Thread initThread = new Thread(this.dataIO);
                        initThread.start();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        System.out.println("Error Initializing Pictures");
                    }
                }
                else if (command.contains(commands[6]))///stop
                {
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
