package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.util.OperatorConsole;

public class ToggleWhiteList extends OperatorCommand
{
    public ToggleWhiteList(String command)
    {
        super(command);
    }
    
    public void execute()
    {
        if(OperatorConsole.whitelist)
        {
            System.out.println("White list is now off");
        }
        else
        {
            System.out.println("White list is now on");
        }
        OperatorConsole.whitelist = !OperatorConsole.whitelist;
    }
}
