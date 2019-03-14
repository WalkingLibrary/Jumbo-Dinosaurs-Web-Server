package com.jumbodinosaurs;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class SessionLogger implements Runnable
{
    public static ArrayList<Session> sessions;

    public SessionLogger()
    {
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


    public synchronized void log(Session session)
    {
        OperatorConsole.addHit(session);
       
        File logFile = DataController.getLogsJson();
        String fileContents = DataController.getFileContents(logFile);
        if(fileContents != null && fileContents != "")
        {
            fileContents = fileContents.substring(0, fileContents.length() - 1);
            fileContents += "," + new Gson().toJson(session) + "]";
        }
        else
        {
            fileContents = "[" + new Gson().toJson(session) + "]";
        }

        DataController.writeContents(logFile, fileContents, false);
        OperatorConsole.printMessageFiltered("Session Logged", true, false);
    }


}
