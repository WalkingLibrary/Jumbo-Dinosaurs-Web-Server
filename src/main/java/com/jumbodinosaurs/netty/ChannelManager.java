package com.jumbodinosaurs.netty;

import com.jumbodinosaurs.netty.initializer.ConnectionListenerInitializer;

import java.util.ArrayList;

public class ChannelManager
{
    private static ArrayList<ConnectionListenerInitializer> connectionListeners =
            new ArrayList<ConnectionListenerInitializer>();
    
    
    public static void addConnectionListener(ConnectionListenerInitializer listener)
    {
        startInitializer(listener);
        connectionListeners.add(listener);
    }
    
    private static void startInitializer(ConnectionListenerInitializer listener)
    {
        Thread initializerThread = new Thread(listener);
        initializerThread.start();
    }
    
    public static ArrayList<ConnectionListenerInitializer> getConnectionListeners()
    {
        return connectionListeners;
    }
    
    public static void removeAndShutdownListener(ConnectionListenerInitializer listener)
    {
        listener.shutDown();
        connectionListeners.remove(listener);
    }
}
