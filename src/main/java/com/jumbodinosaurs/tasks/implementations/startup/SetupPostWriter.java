package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.tasks.StartUpTask;
import com.jumbodinosaurs.util.PostWriter;

public class SetupPostWriter extends StartUpTask
{
    
    @Override
    public void run()
    {
    
        PostWriter.initializePostWriter();
    }
    
    @Override
    public boolean isPreInitPhase()
    {
        return false;
    }
}
