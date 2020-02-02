package com.jumbodinosaurs.netty;

import com.jumbodinosaurs.netty.initializer.ConnectionListenerInitializer;

import java.util.ArrayList;

public class ChannelManager
{
    private static ArrayList<ConnectionListenerInitializer> connectionListeners =
            new ArrayList<ConnectionListenerInitializer>();
    
    
    public static void addConnectionListener(ConnectionListenerInitializer initializer)
    {
        startInitializer(initializer);
        connectionListeners.add(initializer);
    }
    
    private static void startInitializer(ConnectionListenerInitializer initializer)
    {
        Thread initializerThread = new Thread(initializer);
        initializerThread.start();
    }
    
}
