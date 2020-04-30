package com.jumbodinosaurs;


import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.devlib.reflection.exceptions.NoSuchJarAttribute;
import com.jumbodinosaurs.devlib.task.ScheduledTask;
import com.jumbodinosaurs.log.LogManager;
import com.jumbodinosaurs.post.PostCommandUtil;
import com.jumbodinosaurs.tasks.implementations.startup.SetupServer;

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
        if(AuthUtil.testMode == true)
        {
            LogManager.consoleLogger.warn("AuthUtil is in Test Mode");
        }
        PostCommandUtil.getPostCommands();
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
}
