package com.jumbodinosaurs.log;

public class LogManager
{
    private static SessionLogger logger;
    
    public static void initializeLogger()
    {
        //For logging sessions thread
        logger = new SessionLogger();
        Thread loggerThread = new Thread(logger);
        loggerThread.start();
    }
    
    public static void log(Session session)
    {
        logger.addSession(session);
    }
}
