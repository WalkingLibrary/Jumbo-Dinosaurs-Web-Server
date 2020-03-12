package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.email.EmailManager;
import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.util.ServerUtil;

public class SetupEmailManager extends StartUpTask
{
    
    
    public SetupEmailManager()
    {
        super(Phase.Initialization);
    }
    
    @Override
    public void run()
    {
        EmailManager.initializeEmails(ServerUtil.serverDataDir);
    }
}
