package com.jumbodinosaurs.commands;

import com.jumbodinosaurs.devlib.commands.CommandWithParameters;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.objects.User;
import com.jumbodinosaurs.util.CredentialsManager;

public class ToggleUserLock extends CommandWithParameters
{
    
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        
        try
        {
            String username = this.getParameters().get(0).getParameter();
            String outputMessage = "";
            User userToLock = CredentialsManager.getUser(username);
            if(userToLock != null)
            {
                User updatedUserInfo = userToLock.clone();
                updatedUserInfo.setAccountLocked(!userToLock.isAccountLocked());
                if(userToLock.isAccountLocked())
                {
                    outputMessage += "Unlocking: " + username+"\n";
                }
                else
                {
                    outputMessage +="Locking: " + username+"\n";
                }
                if(CredentialsManager.modifyUser(userToLock, updatedUserInfo))
                {
                    outputMessage +="User Toggled successfully"+"\n";
                }
                else
                {
                    outputMessage +="User not toggled"+"\n";
                }
            }
            
            return new MessageResponse(outputMessage);
        }
        catch(Exception e)
        {
            throw new WaveringParametersException(e);
        }
    
       
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Toggles the lock on a specified user";
    }
    
}
