package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.tasks.StartUpTask;
import com.jumbodinosaurs.util.LinuxUtil;

public class UnpackLinuxScripts extends StartUpTask
{
    @Override
    public boolean isPreInitPhase()
    {
        return false;
    }
    
    @Override
    public void run()
    {
        LinuxUtil.unpackScripts();
    }
}
