package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.netty.ChannelManager;
import com.jumbodinosaurs.netty.handler.http.handler.HTTPSessionHandler;
import com.jumbodinosaurs.netty.initializer.DefaultConnectListenerInitializer;
import com.jumbodinosaurs.netty.initializer.SecureConnectListenerInitializer;
import com.jumbodinosaurs.tasks.StartUpTask;

public class SetupHTTP extends StartUpTask
{
    @Override
    public void run()
    {
        DefaultConnectListenerInitializer port80 = new DefaultConnectListenerInitializer(80, new HTTPSessionHandler());
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
            SecureConnectListenerInitializer port443 = new SecureConnectListenerInitializer(443,
                                                                                            new HTTPSessionHandler());
            ChannelManager.addConnectionListener(port443);
        }
        else
        {
            System.out.println("Skipping Secure Listener Creation");
        }
    }
    
    @Override
    public boolean isPreInitPhase()
    {
        return false;
    }
}
