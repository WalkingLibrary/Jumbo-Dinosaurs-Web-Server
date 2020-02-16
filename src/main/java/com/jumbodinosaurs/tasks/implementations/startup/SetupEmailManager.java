package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.email.EmailManager;
import com.jumbodinosaurs.tasks.StartUpTask;
import com.jumbodinosaurs.util.ServerUtil;

public class SetupEmailManager extends StartUpTask
{
    @Override
    public boolean isPreInitPhase()
    {
        return false;
    }
    
    @Override
    public void run()
    {
        EmailManager.initializeEmails(ServerUtil.serverDataDir);
    }
}
