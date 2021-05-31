package com.jumbodinosaurs.webserver.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.ContentTypeUtil;

public class SetupContentTypeMap extends StartUpTask
{
    
    @Override
    public void run()
    {
        ContentTypeUtil.loadMappings();
    }
}
