package com.jumbodinosaurs;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.devlib.reflection.exceptions.NoSuchJarAttribute;
import com.jumbodinosaurs.devlib.task.ScheduledTask;
import com.jumbodinosaurs.log.LogManager;
import com.jumbodinosaurs.tasks.implementations.startup.SetupServer;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class ServerController
{
    
    private static String version;
    private static ScheduledThreadPoolExecutor threadScheduler = new ScheduledThreadPoolExecutor(4);
    private static ArrayList<ScheduledTask> scheduledServerTasks = new ArrayList<ScheduledTask>();
    
    
    public ServerController()
    {
        String attributeKey = "Jumbo-Dinosaurs-WebServer-Version";
        try
        {
            version = ReflectionUtil.getAttribute(attributeKey);
        }
        catch(NoSuchJarAttribute e)
        {
            version = "Development Environment";
        }
        LogManager.consoleLogger.info("Starting Jumbo Dinosaurs " + version);//G
        SetupServer task = new SetupServer();
        task.run();
    }
    
    
    
    public static ScheduledThreadPoolExecutor getThreadScheduler()
    {
        return threadScheduler;
    }
    
    
    public static ArrayList<ScheduledTask> getScheduledServerTasks()
    {
        return scheduledServerTasks;
    }
    
    public static void setScheduledServerTasks(ArrayList<ScheduledTask> scheduledServerTasks)
    {
        ServerController.scheduledServerTasks = scheduledServerTasks;
    }
    
    
    
    private static void setLogLevel(String logLevel, String packageName)
    {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger(packageName);
        System.out.println(packageName + " current logger level: " + logger.getLevel());
        System.out.println(" You entered: " + logLevel);
        
        logger.setLevel(Level.toLevel(logLevel));
    }
    
   
}
