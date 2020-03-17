package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.ServerController;
import com.jumbodinosaurs.devlib.task.*;

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
        ServerController.consoleLogger.info("Running SetUp Server Task");
        ArrayList<StartUpTask> startUpTasks = TaskUtil.getStartUpTasks();
        ServerController.consoleLogger.info("Starting Pre-Initialization Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.getPhase().equals(Phase.PreInitialization))
            {
                ServerController.consoleLogger.info("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
    
        ServerController.consoleLogger.info("Starting Initialization Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.getPhase().equals(Phase.Initialization))
            {
                ServerController.consoleLogger.info("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
        ServerController.consoleLogger.info("Starting Post Initialization Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.getPhase().equals(Phase.PostInitialization))
            {
                ServerController.consoleLogger.info("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
        
       
        ArrayList<ScheduledTask> scheduledServerTasks = TaskUtil.getScheduledTasks(ServerController.getThreadScheduler());
        
        ServerController.setScheduledServerTasks(scheduledServerTasks);
        ServerController.consoleLogger.info("Server Setup Task Complete");
    }
}
