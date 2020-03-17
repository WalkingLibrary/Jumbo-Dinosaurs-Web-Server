package com.jumbodinosaurs.log;

import com.google.gson.Gson;
import com.jumbodinosaurs.ServerController;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.util.ServerUtil;

import java.io.File;
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
            ServerController.consoleLogger.error("Error Logging Session", e);
        }
    }


    public synchronized void log(Session session)
    {
        //OperatorConsole.addHit(session);
        File logFile = ServerUtil.getLogFileFromDate(session.getDateTime());
        String fileContents = GeneralUtil.scanFileContents(logFile);
        if(fileContents != null && fileContents != "")
        {
            fileContents = fileContents.substring(0, fileContents.length() - 1);
            fileContents += "," + new Gson().toJson(session) + "]";
        }
        else
        {
            fileContents = "[" + new Gson().toJson(session) + "]";
        }
    
        GeneralUtil.writeContents(logFile, fileContents, false);
    }


}
