package com.jumbodinosaurs.util.operatorcommands;


import com.jumbodinosaurs.util.OperatorConsole;

public class ToggleAllowPost extends OperatorCommand
{
    
    
    public ToggleAllowPost(String command)
    {
        super(command);
    }
    
    public void execute()
    {
        if(OperatorConsole.allowPost)
        {
            System.out.println("Server Will No Longer Accept Post Requests");
        }
        else
        {
            System.out.println("Server Will now Accept Post Requests");
        }
        OperatorConsole.allowPost = !OperatorConsole.allowPost;
    }
}
