package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.log.LogManager;

public class SetupLogger extends StartUpTask
{
    public SetupLogger()
    {
        super(Phase.Initialization);
    }
    
    @Override
    public void run()
    {
        LogManager.initializeLogger();
    }
    
}
