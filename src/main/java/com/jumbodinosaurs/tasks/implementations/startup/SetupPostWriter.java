package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.util.PostWriter;

public class SetupPostWriter extends StartUpTask
{
    
    public SetupPostWriter()
    {
        super(Phase.Initialization);
    }
    
    @Override
    public void run()
    {
    
        PostWriter.initializePostWriter();
    }
    
}
