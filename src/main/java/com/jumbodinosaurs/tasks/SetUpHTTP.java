package com.jumbodinosaurs.tasks;

import com.jumbodinosaurs.netty.ChannelManager;
import com.jumbodinosaurs.netty.handler.SessionHandler;
import com.jumbodinosaurs.netty.initializer.DefaultConnectListenerInitializer;
import com.jumbodinosaurs.netty.initializer.SecureConnectListenerInitializer;

public class SetUpHTTP extends StartUpTask
{
    @Override
    public void run()
    {
        DefaultConnectListenerInitializer port80 = new DefaultConnectListenerInitializer(80, new SessionHandler());
        SecureConnectListenerInitializer port443 = new SecureConnectListenerInitializer(443, new SessionHandler());
        ChannelManager.addConnectionListener(port80);
        ChannelManager.addConnectionListener(port443);
    }
}
