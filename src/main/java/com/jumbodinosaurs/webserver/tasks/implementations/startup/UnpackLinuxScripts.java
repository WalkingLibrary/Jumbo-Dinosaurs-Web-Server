package com.jumbodinosaurs.webserver.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.webserver.util.LinuxUtil;

public class UnpackLinuxScripts extends StartUpTask
{
    public UnpackLinuxScripts()
    {
        super(2);
    }
    
    @Override
    public void run()
    {
        LinuxUtil.unpackScripts();
    }
}
