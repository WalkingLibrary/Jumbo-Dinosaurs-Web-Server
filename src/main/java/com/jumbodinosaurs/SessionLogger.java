package com.jumbodinosaurs;

import com.google.gson.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class SessionLogger implements Runnable
{
    private static ArrayList<Session> sessions;
    private static DataController dataIO;

    public SessionLogger(DataController dataIO)
    {
        this.dataIO = dataIO;
        this.sessions = new ArrayList<Session>();
    }

    public synchronized void addSession(Session session)
    {
        this.sessions.add(session);
    }

    public void run()
    {
        try
        {
            while (true)
            {
                if (this.sessions.size() > 0)
                {
                    this.log(this.sessions.remove(0));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error Logging Session");
        }
    }


    public void log(Session session)
    {
        //GSON Objects for writeing and dealing with json
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        JsonParser parser = new JsonParser();

        try
        {
            //File with log json
            File logFile = this.dataIO.checkFor(this.dataIO.getLogsDir(), "logs.json");
            //Read in log json
            Scanner logIn = new Scanner(logFile);
            String fileContents = "";
            while (logIn.hasNextLine())
            {
                fileContents += logIn.nextLine();
            }
            logIn.close();

            //try parsing file with json parser
            JsonElement element = null;
            try
            {
                element = parser.parse(fileContents);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Error Parsing Json");
            }
            JsonObject sessionList = new JsonObject();
            //If file already has contents
            if (element != null &&
                    element.isJsonObject() &&
                    element.getAsJsonObject().getAsJsonArray("loglist") != null &&
                    element.getAsJsonObject().getAsJsonArray("loglist").isJsonArray())
            {
                System.out.println("LOGS IS JSON");
                //Just add session
                sessionList = element.getAsJsonObject();
                sessionList.getAsJsonObject().getAsJsonArray("loglist").add(gson.toJson(session, Session.class));
            }
            else
            {
                System.out.println("LOGS IS NOT JSON");
                //else make new loglist and add session
                sessionList.add("loglist", new JsonArray());
                sessionList.getAsJsonArray("loglist").add(gson.toJson(session, Session.class));
            }
            //write contents of sessionlist to logFile and close()
            PrintWriter logOut = new PrintWriter(logFile);
            logOut.write(sessionList.toString());
            logOut.close();
        }
        catch (Exception e)
        {
            System.out.println("Error Writing to Logs");
            e.printStackTrace();
            System.out.println(e.getCause());
        }
    }
}
