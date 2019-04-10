package com.jumbodinosaurs.util;

import java.awt.event.ActionListener;

public class Timer extends javax.swing.Timer
{
    
    
    public Timer(int delay, ActionListener listener, boolean startOnInit, int initialDelay)
    {
        super(delay, listener);
        if(!startOnInit)
        {
            this.stop();
        }
        super.setInitialDelay(initialDelay);
    }
    
    
    public Timer(int delay, ActionListener listener, boolean startOnInit)
    {
        super(delay, listener);
        if(!startOnInit)
        {
            this.stop();
        }
    }
    
    public void start()
    {
        super.start();
    }

    public void stop()
    {
        super.stop();
    }

}
