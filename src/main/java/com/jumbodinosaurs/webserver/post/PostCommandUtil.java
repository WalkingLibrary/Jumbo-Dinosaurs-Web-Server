package com.jumbodinosaurs.webserver.post;

import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.webserver.post.exceptions.NoSuchPostCommand;

import java.util.ArrayList;

public class PostCommandUtil
{
    
    private static final ArrayList<Class> postCommandClasses = ReflectionUtil.getSubClasses(PostCommand.class);
    
    public static PostCommand getPostCommand(String commandName) throws NoSuchPostCommand
    {
        for(PostCommand command : PostCommandUtil.getPostCommands())
        {
            if(command.getNameCommand().equals(commandName))
            {
                return command;
            }
        }
        throw new NoSuchPostCommand("No Post command found matching " + commandName);
    }
    
    private static ArrayList<PostCommand> getPostCommands()
    {
        ArrayList<PostCommand> postCommands = new ArrayList<PostCommand>();
        for(Class clazz : postCommandClasses)
        {
            try
            {
                postCommands.add((PostCommand) clazz.newInstance());
            }
            catch(IllegalAccessException | InstantiationException error)
            {
                LogManager.consoleLogger.error(error.getMessage(), error);
                throw new IllegalStateException("Reflection Error");
            }
        }
        return postCommands;
    }
    
    
}
