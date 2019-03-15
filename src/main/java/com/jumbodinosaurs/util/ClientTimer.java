package com.jumbodinosaurs.util;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ClientTimer extends Timer
{
    private static boolean stopped;

    public ClientTimer(int num, ActionListener listener)
    {
        super(num, listener);
        stopped = false;
    }

    public void start()
    {
        stopped = false;
        super.start();
    }

    public void stop()
    {
        stopped = true;
        super.stop();
    }

    //if true then not running
    public boolean getStatus()
    {
        return stopped;
    }

}
