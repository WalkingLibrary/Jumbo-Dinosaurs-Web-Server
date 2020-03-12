package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.util.LinuxUtil;

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
