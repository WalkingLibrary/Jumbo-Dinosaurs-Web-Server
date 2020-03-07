package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.netty.ChannelManager;
import com.jumbodinosaurs.netty.handler.http.HTTPHandler;
import com.jumbodinosaurs.netty.initializer.DefaultHTTPConnectListenerInitializer;
import com.jumbodinosaurs.netty.initializer.SecureHTTPConnectListenerInitializer;
import com.jumbodinosaurs.tasks.StartUpTask;

public class SetupHTTP extends StartUpTask
{
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
            System.out.println("Skipping Secure Listener Creation");
        }
    }
    
    @Override
    public boolean isPreInitPhase()
    {
        return false;
    }
}
