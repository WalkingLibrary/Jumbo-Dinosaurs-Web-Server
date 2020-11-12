package com.jumbodinosaurs.webserver.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.task.*;
import com.jumbodinosaurs.webserver.ServerController;

import java.util.ArrayList;

public class SetupServer extends Task
{
    
    
    @Override
    public void run()
    {
        /*
         * Set Host
         * Generate Default HTML Pages
         * Initialize Domains
         * Initialize Emails
         * Initialize Operator Console
         * Initialize Logger
         * Initialize PostWriter
         * Initialize ConnectionListeners
         *
         * Renewal Tasks
         * Initialize Renew Host
         * Initialize DNSUpdater
         * Initialize CertificateRenewer
         */
        LogManager.consoleLogger.info("Running SetUp Server Task");
        ArrayList<StartUpTask> startUpTasks = TaskUtil.getStartUpTasks();
        LogManager.consoleLogger.info("Starting Pre-Initialization Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.getPhase().equals(Phase.PreInitialization))
            {
                LogManager.consoleLogger.info("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
    
        LogManager.consoleLogger.info("Starting Initialization Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.getPhase().equals(Phase.Initialization))
            {
                LogManager.consoleLogger.info("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
        LogManager.consoleLogger.info("Starting Post Initialization Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.getPhase().equals(Phase.PostInitialization))
            {
                LogManager.consoleLogger.info("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
    
    
        ArrayList<ScheduledTask> scheduledServerTasks = TaskUtil.getScheduledTasks(ServerController.getThreadScheduler());
    
        ServerController.setScheduledServerTasks(scheduledServerTasks);
        for(ScheduledTask scheduledTask : scheduledServerTasks)
        {
            scheduledTask.start();
        }
        LogManager.consoleLogger.info("Server Setup Task Complete");
    }
}
