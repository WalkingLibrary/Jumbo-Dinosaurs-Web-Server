package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.tasks.StartUpTask;
import com.jumbodinosaurs.util.ServerUtil;

public class SetupHost extends StartUpTask
{
    @Override
    public boolean isPreInitPhase()
    {
        return true;
    }
    
    @Override
    public void run()
    {
        ServerUtil.setHost();
    }
}
