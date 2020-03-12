package com.jumbodinosaurs;


import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.devlib.reflection.exceptions.NoSuchJarAttribute;
import com.jumbodinosaurs.devlib.task.ScheduledTask;
import com.jumbodinosaurs.objects.RuntimeArguments;
import com.jumbodinosaurs.tasks.implementations.startup.SetupServer;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class ServerController
{
    
    private static String version;
    private static RuntimeArguments arguments;
    private static ScheduledThreadPoolExecutor threadScheduler = new ScheduledThreadPoolExecutor(4);
    private static ArrayList<ScheduledTask> scheduledServerTasks = new ArrayList<ScheduledTask>();
    
    
    
    public ServerController(RuntimeArguments arguments)
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
        System.out.println("Starting Jumbo Dinosaurs " + version);//G
        if(arguments == null)
        {
            System.out.println("Test Mode: " + ServerController.arguments.isInTestMode());
        }
        ServerController.arguments = arguments;
        SetupServer task = new SetupServer();
        task.run();
    }
    
    public static RuntimeArguments getArguments()
    {
        return arguments;
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
