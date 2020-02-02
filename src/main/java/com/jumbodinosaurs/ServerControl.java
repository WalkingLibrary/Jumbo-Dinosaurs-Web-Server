package com.jumbodinosaurs;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.domain.util.Domain;
import com.jumbodinosaurs.objects.RuntimeArguments;
import com.jumbodinosaurs.tasks.UpdateDNS;
import com.jumbodinosaurs.util.ServerUtil;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ServerControl
{
    
    private static Thread commandThread, port80Thread, port443Thread;
    private static ServerUtil dataIO;
    private static RuntimeArguments arguments;
    private static ArrayList<Domain> updatableDomains = new ArrayList<Domain>();
    
    
    public static String version = "0.0.6";
    private static ScheduledThreadPoolExecutor threadScheduler;
    
    
    public ServerControl()
    {
        System.out.println("Starting Jumbo Dinosaurs " + version);
        threadScheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(4);
        dataIO = new ServerUtil(false);
        commandThread = new Thread(new OperatorConsole());
        commandThread.start();
        OperatorConsole.redirectToSSL = false;
       
    }
    
    public ServerControl(RuntimeArguments arguments)
    {
        
        //TODO make Server Tasks
        //TODO make initlization
        System.out.println("Starting Jumbo Dinosaurs .6");//G
        ServerControl.arguments = arguments;
        System.out.println("Test Mode: " + ServerControl.arguments.isInTestMode());
        
        if(arguments.getDomains() != null && arguments.getDomains().size() > 0)
        {
            dataIO = new ServerUtil(true);
            threadScheduler.scheduleAtFixedRate(new UpdateDNS(), 1, 1, TimeUnit.HOURS);
        }
        else
        {
            dataIO = new ServerUtil(false);
        }
        
        commandThread = new Thread(new OperatorConsole());
        commandThread.start();
        
      
        
        
    }
    
    public static RuntimeArguments getArguments()
    {
        return arguments;
    }
    
}
