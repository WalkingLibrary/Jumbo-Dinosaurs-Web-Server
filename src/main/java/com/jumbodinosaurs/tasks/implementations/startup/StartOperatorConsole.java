package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;

public class StartOperatorConsole extends StartUpTask
{
    
    public StartOperatorConsole()
    {
        super(Phase.PreInitialization);
    }
    
    @Override
    public void run()
    {
        Thread commandThread = new Thread(new OperatorConsole());
        commandThread.start();
    }
    
}
