package com.jumbodinosaurs.util.operatorcommands;


import java.util.ArrayList;

public class Help extends OperatorCommand
{
    private ArrayList<String> commands;
    
    public Help(String command, ArrayList<String> commands)
    {
        super(command);
        this.commands = commands;
    }
    
    
    public void execute()
    {
        System.out.println("Commands: ");
        for(String str : this.commands)
        {
            System.out.println(str);
        }
    }
}
