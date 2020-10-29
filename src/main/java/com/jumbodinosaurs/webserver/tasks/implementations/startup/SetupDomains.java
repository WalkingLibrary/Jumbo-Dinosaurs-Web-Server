package com.jumbodinosaurs.webserver.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.webserver.domain.DomainManager;

public class SetupDomains extends StartUpTask
{
    public SetupDomains()
    {
        super(Phase.Initialization);
    }
    
    @Override
    public void run()
    {
        DomainManager.initializeDomains();
    }
    
}
