package com.jumbodinosaurs.util.operatorcommands;

public abstract class OperatorCommand
{
    private String command;
    
    
    public OperatorCommand(String command)
    {
        this.command = command;
    }
    
    
    public abstract void execute();
    
    public String getCommand()
    {
        return command;
    }
    
    public void setCommand(String command)
    {
        this.command = command;
    }
    
}
