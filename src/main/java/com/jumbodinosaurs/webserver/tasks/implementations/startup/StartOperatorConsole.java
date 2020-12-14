package com.jumbodinosaurs.webserver.tasks.implementations.startup;


import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.webserver.commands.OperatorConsole;

public class StartOperatorConsole extends StartUpTask
{
    
    public StartOperatorConsole()
    {
        super(0);
    }
    
    @Override
    public void run()
    {
        Thread commandThread = new Thread(new OperatorConsole());
        commandThread.start();
    }
    
}
