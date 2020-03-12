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
        System.out.println("Running SetUp Server Task");
        ArrayList<StartUpTask> startUpTasks = TaskUtil.getStartUpTasks();
        System.out.println("\n");
        System.out.println("Starting Pre-Initialization Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.getPhase().equals(Phase.PreInitialization))
            {
                System.out.println("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
        
        System.out.println("\n");
        System.out.println("Starting Initialization Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.getPhase().equals(Phase.Initialization))
            {
                System.out.println("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
        System.out.println("\n");
        System.out.println("Starting Post Initialization Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.getPhase().equals(Phase.PostInitialization))
            {
                System.out.println("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
        
       
        ArrayList<ScheduledTask> scheduledServerTasks = TaskUtil.getScheduledTasks(ServerController.getThreadScheduler());
        
        ServerController.setScheduledServerTasks(scheduledServerTasks);
        System.out.println("Server Setup Task Complete");
    }
}
