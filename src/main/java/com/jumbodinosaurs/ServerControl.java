package com.jumbodinosaurs;

import com.jumbodinosaurs.objects.RuntimeArguments;
import com.jumbodinosaurs.tasks.ScheduledServerTask;
import com.jumbodinosaurs.tasks.implementations.startup.SetupServer;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class ServerControl
{
    
    
    public static String version = "0.0.6";
    private static RuntimeArguments arguments;
    private static ScheduledThreadPoolExecutor threadScheduler = new ScheduledThreadPoolExecutor(4);
    private static ArrayList<ScheduledServerTask> scheduledServerTasks = new ArrayList<ScheduledServerTask>();
    
    
    
    public ServerControl(RuntimeArguments arguments)
    {
        System.out.println("Starting Jumbo Dinosaurs .6");//G
        if(arguments == null)
        {
            System.out.println("Test Mode: " + ServerControl.arguments.isInTestMode());
        }
        ServerControl.arguments = arguments;
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
    
    
    public static ArrayList<ScheduledServerTask> getScheduledServerTasks()
    {
        return scheduledServerTasks;
    }
    
    public static void setScheduledServerTasks(ArrayList<ScheduledServerTask> scheduledServerTasks)
    {
        ServerControl.scheduledServerTasks = scheduledServerTasks;
    }
}
