package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.ServerController;
import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.netty.ChannelManager;
import com.jumbodinosaurs.netty.handler.http.HTTPHandler;
import com.jumbodinosaurs.netty.initializer.DefaultHTTPConnectListenerInitializer;
import com.jumbodinosaurs.netty.initializer.SecureHTTPConnectListenerInitializer;

public class SetupHTTP extends StartUpTask
{
    public SetupHTTP()
    {
        super(Phase.PostInitialization);
    }
    
    @Override
    public void run()
    {
        DefaultHTTPConnectListenerInitializer port80 = new DefaultHTTPConnectListenerInitializer(80, new HTTPHandler());
        ChannelManager.addConnectionListener(port80);
        
        
        boolean needsSecureListener = false;
        for(SecureDomain domain: DomainManager.getDomains())
        {
            if(domain.hasCertificateFile())
            {
                needsSecureListener = true;
                break;
            }
        }
        
        if(needsSecureListener)
        {
            SecureHTTPConnectListenerInitializer port443 = new SecureHTTPConnectListenerInitializer(443,
                                                                                                    new HTTPHandler());
            ChannelManager.addConnectionListener(port443);
        }
        else
        {
            ServerController.consoleLogger.info("Skipping Secure Listener Creation");
        }
    }
    
}
