package com.jumbodinosaurs.webserver.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.email.Email;
import com.jumbodinosaurs.devlib.email.EmailManager;
import com.jumbodinosaurs.devlib.email.GoogleAPIEmail;
import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.webserver.util.ServerUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
        for(Email email : EmailManager.getEmails())
        {
            if(email instanceof GoogleAPIEmail)
            {
                try
                {
                    ((GoogleAPIEmail) email).activate();
                }
                catch(GeneralSecurityException | IOException e)
                {
                    throw new IllegalStateException(e.getMessage());
                }
            }
        }
    }
}
