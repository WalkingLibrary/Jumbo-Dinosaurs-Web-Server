package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.ServerControl;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.tasks.ScheduledServerTask;
import com.jumbodinosaurs.tasks.ServerTask;
import com.jumbodinosaurs.tasks.StartUpTask;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SetupServer extends ServerTask
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
        ArrayList<Class> startUpTasksClasses = ReflectionUtil.getSubClasses(StartUpTask.class);
        ArrayList<StartUpTask> startUpTasks = new ArrayList<StartUpTask>();
        for(Class classType: startUpTasksClasses)
        {
            try
            {
                startUpTasks.add((StartUpTask) classType.newInstance());
            }
            catch(ReflectiveOperationException e)
            {
                e.printStackTrace();
            }
        }
        
        System.out.println("\n\nStarting Pre Init Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.isPreInitPhase())
            {
                System.out.println("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
        System.out.println("\n\nStarting Post Init Phase");
        for(StartUpTask task : startUpTasks)
        {
            if(task.isPostInitPhase())
            {
                System.out.println("Starting Task: " + task.getClass().getSimpleName());
                task.run();
            }
        }
        System.out.println("\n\n");
        
        ArrayList<Class> scheduledServerTasksClasses = ReflectionUtil.getSubClasses(ScheduledServerTask.class);
        ArrayList<ScheduledServerTask> scheduledServerTasks = new ArrayList<ScheduledServerTask>();
        
        for(Class classType : scheduledServerTasksClasses)
        {
            try
            {
                ScheduledServerTask task =
                        (ScheduledServerTask) classType.getConstructor(ScheduledThreadPoolExecutor.class)
                                                       .newInstance(ServerControl.getThreadScheduler());
                scheduledServerTasks.add(task);
            }
            catch(ReflectiveOperationException e)
            {
                e.printStackTrace();
            }
        }
        ServerControl.setScheduledServerTasks(scheduledServerTasks);
        System.out.println("Server Setup Task Complete");
    }
}
