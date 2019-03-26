package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.util.OperatorConsole;

public class ToggleDebug extends OperatorCommand
{
    public ToggleDebug(String command)
    {
        super(command);
    }
    
    public void execute()
    {
        if(OperatorConsole.debug)
        {
            System.out.println("Debug Mode is Now Off");
        }
        else
        {
            System.out.println("Debug Mode is Now On");
        }
        OperatorConsole.debug = !OperatorConsole.debug;
    }
}
