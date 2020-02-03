package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.tasks.StartUpTask;

public class SetupDomains extends StartUpTask
{
    @Override
    public void run()
    {
        DomainManager.initializeDomains();
    }
    
    @Override
    public boolean isPreInitPhase()
    {
        return true;
    }
}
