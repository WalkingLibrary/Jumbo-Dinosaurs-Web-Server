package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.util.OperatorConsole;

public class ToggleDebugMessages extends OperatorCommand
{
    public ToggleDebugMessages(String command)
    {
        super(command);
    }
    
    public void execute()
    {
        if(OperatorConsole.debug)
        {
            System.out.println("Debug Messages will not be displayed");
        }
        else
        {
            System.out.println("Debug Messages will be displayed");
        }
        OperatorConsole.debug = !OperatorConsole.debug;
    }
}
