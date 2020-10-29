package com.jumbodinosaurs.webserver.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.webserver.util.LinuxUtil;

public class UnpackLinuxScripts extends StartUpTask
{
    public UnpackLinuxScripts()
    {
        super(Phase.PostInitialization);
    }
    
    @Override
    public void run()
    {
        LinuxUtil.unpackScripts();
    }
}
