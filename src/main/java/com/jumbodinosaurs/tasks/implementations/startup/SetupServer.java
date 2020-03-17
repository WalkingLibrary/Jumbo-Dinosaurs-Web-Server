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
        ServerController.generalLogger.info("Running SetUp Server Task");
        ArrayList<StartUpTask> startUpTasks = TaskUtil.getStartUpTasks();
        ServerController.generalLogger.info("Starting Pre-Initialization Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.getPhase().equals(Phase.PreInitialization))
            {
                ServerController.generalLogger.info("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
    
        ServerController.generalLogger.info("Starting Initialization Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.getPhase().equals(Phase.Initialization))
            {
                ServerController.generalLogger.info("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
        ServerController.generalLogger.info("Starting Post Initialization Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.getPhase().equals(Phase.PostInitialization))
            {
                ServerController.generalLogger.info("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
        
       
        ArrayList<ScheduledTask> scheduledServerTasks = TaskUtil.getScheduledTasks(ServerController.getThreadScheduler());
        
        ServerController.setScheduledServerTasks(scheduledServerTasks);
        ServerController.generalLogger.info("Server Setup Task Complete");
    }
}
