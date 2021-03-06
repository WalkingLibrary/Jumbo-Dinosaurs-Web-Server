package com.jumbodinosaurs.webserver.log;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionLogManager
{
    private static Logger sessionLogger = LoggerFactory.getLogger("SessionLogger");
    
    
    public static void log(Session session)
    {
        String sessionSaveData = new Gson().toJson(session) + ",";
        sessionLogger.info(sessionSaveData);
    }
}
