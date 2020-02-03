package com.jumbodinosaurs.tasks;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ScheduledServerTask extends ServerTask
{
    private Future future;
    
    public ScheduledServerTask(ScheduledThreadPoolExecutor executor)
    {
        this.future = executor.scheduleAtFixedRate(this, getInitialDelay(), getPeriod(), getTimeUnit());
    }
    
    public void stop()
    {
        future.cancel(true);
    }
    
    public abstract int getInitialDelay();
    
    public abstract int getPeriod();
    
    public abstract TimeUnit getTimeUnit();
    
}
