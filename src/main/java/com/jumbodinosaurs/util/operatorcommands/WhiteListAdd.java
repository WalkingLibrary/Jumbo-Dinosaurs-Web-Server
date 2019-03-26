package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.util.OperatorConsole;

public class WhiteListAdd extends OperatorCommandWithParameter
{
    public WhiteListAdd(String command)
    {
        super(command);
    }
    
    public void execute()
    {
        String ip = this.getParameter();
        OperatorConsole.whitelistedIps.add(ip);
        System.out.println("I.P. added: " + ip);
    }
}
