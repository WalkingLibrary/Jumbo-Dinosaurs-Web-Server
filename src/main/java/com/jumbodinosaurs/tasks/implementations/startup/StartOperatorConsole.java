package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.tasks.StartUpTask;

public class StartOperatorConsole extends StartUpTask
{
    
    @Override
    public void run()
    {
        Thread commandThread = new Thread(new OperatorConsole());
        commandThread.start();
    }
    
    @Override
    public boolean isPreInitPhase()
    {
        return false;
    }
}
