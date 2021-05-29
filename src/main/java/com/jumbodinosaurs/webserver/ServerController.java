package com.jumbodinosaurs.webserver;


import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.devlib.reflection.exceptions.NoSuchJarAttribute;
import com.jumbodinosaurs.devlib.task.ScheduledTask;
import com.jumbodinosaurs.devlib.util.OperatorConsole;
import com.jumbodinosaurs.webserver.auth.util.AuthUtil;
import com.jumbodinosaurs.webserver.commands.general.ToggleDebugMode;
import com.jumbodinosaurs.webserver.tasks.implementations.startup.SetupServer;
import com.jumbodinosaurs.webserver.util.OptionUtil;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class ServerController
{
    
    private static String version;
    private static final ScheduledThreadPoolExecutor threadScheduler = new ScheduledThreadPoolExecutor(4);
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
        LogManager.consoleLogger.info("Starting Jumbo Dinosaurs " +
                                      OperatorConsole.ANSI_PURPLE +
                                      version +
                                      OperatorConsole.ANSI_RESET);//G
    
        /*Set Debug Level*/
        ToggleDebugMode.toggleConsoleAppenderFilter(OptionUtil.isInDebugMode());
        LogManager.consoleLogger.info("Debug Mode: " + OptionUtil.isInDebugMode());
        LogManager.consoleLogger.debug(OperatorConsole.ANSI_CYAN + "Can You See me?" + OperatorConsole.ANSI_RESET);
    
        SetupServer task = new SetupServer();
        task.run();
    
        if(AuthUtil.testMode == true)
        {
            LogManager.consoleLogger.warn("AuthUtil is in Test Mode");
        }
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
