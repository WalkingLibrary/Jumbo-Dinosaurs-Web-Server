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
                Thread.sleep(10);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Logging Session",false, true);
        }
    }


    public void log(Session session)
    {
        OperatorConsole.addHit(session);
        //GSON Objects for writeing and dealing with json
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        JsonParser parser = new JsonParser();

        try
        {
            //File with log json
            File logFile = this.dataIO.getLogsJson();
            //Read in log json
            String fileContents = this.dataIO.getFileContents(logFile);


            //try parsing file with json parser
            JsonElement element = null;
            try
            {
                element = parser.parse(fileContents);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                OperatorConsole.printMessageFiltered("Error Parsing Json",false, true);
            }
            JsonObject sessionList = new JsonObject();
            //If file already has contents
            if (element != null &&
                    element.isJsonObject() &&
                    element.getAsJsonObject().getAsJsonArray("loglist") != null &&
                    element.getAsJsonObject().getAsJsonArray("loglist").isJsonArray())
            {
                //Just add session
                sessionList = element.getAsJsonObject();
                sessionList.getAsJsonObject().getAsJsonArray("loglist").add(gson.toJson(session, Session.class));
            }
            else
            {
                //else make new loglist and add session
                sessionList.add("loglist", new JsonArray());
                sessionList.getAsJsonArray("loglist").add(gson.toJson(session, Session.class));
            }
            //write contents of sessionlist to logFile and close()
            PrintWriter logOut = new PrintWriter(logFile);
            logOut.write(sessionList.toString());
            logOut.close();
            OperatorConsole.printMessageFiltered("Session Logged", true, false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Writing to Logs",false, true);
        }
    }
}
