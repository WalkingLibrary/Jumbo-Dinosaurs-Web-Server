package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.log.LogManager;
import com.jumbodinosaurs.tasks.StartUpTask;

public class SetupLogger extends StartUpTask
{
    @Override
    public void run()
    {
        LogManager.initializeLogger();
    }
    
    @Override
    public boolean isPreInitPhase()
    {
        return false;
    }
}
