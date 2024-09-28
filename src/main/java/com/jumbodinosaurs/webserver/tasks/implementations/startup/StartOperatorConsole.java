package com.jumbodinosaurs.webserver.tasks.implementations.startup;


import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.devlib.util.OperatorConsole;

public class StartOperatorConsole extends StartUpTask
{

    public static boolean serviceMode = true;

    public StartOperatorConsole()
    {
        super(0);
    }

    @Override
    public void run()
    {
        if (serviceMode)
        {
            LogManager.consoleLogger.warn("Skipping Loading Operator Console In Service Mode");
            return;
        }
        Thread commandThread = new Thread(new OperatorConsole());
        commandThread.start();
    }
    
}
