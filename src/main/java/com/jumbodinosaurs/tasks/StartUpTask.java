package com.jumbodinosaurs.tasks;

public abstract class StartUpTask extends ServerTask
{
    public abstract boolean isPreInitPhase();
    
    public boolean isPostInitPhase()
    {
        return !isPreInitPhase();
    }
}
