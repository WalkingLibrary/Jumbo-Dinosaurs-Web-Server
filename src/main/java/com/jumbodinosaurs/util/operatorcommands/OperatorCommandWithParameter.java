package com.jumbodinosaurs.util.operatorcommands;

public abstract class OperatorCommandWithParameter extends OperatorCommand
{
    private String parameter;
    
    
    public OperatorCommandWithParameter(String command)
    {
        super(command);
    }
    
    public String getParameter()
    {
        return parameter;
    }
    
    public void setParameter(String parameter)
    {
        this.parameter = parameter;
    }
}
