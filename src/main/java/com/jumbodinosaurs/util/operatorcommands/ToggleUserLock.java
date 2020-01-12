package com.jumbodinosaurs.util.operatorcommands;

import com.jumbodinosaurs.objects.User;
import com.jumbodinosaurs.util.CredentialsManager;

public class ToggleUserLock extends OperatorCommandWithParameter
{
    
    
    public ToggleUserLock(String command)
    {
        super(command);
    }
    
    public void execute()
    {
        String username = this.getParameter();
    
        User userToLock = CredentialsManager.getUser(username);
        if(userToLock != null)
        {
            User updatedUserInfo = userToLock.clone();
            updatedUserInfo.setAccountLocked(!userToLock.isAccountLocked());
            if(userToLock.isAccountLocked())
            {
                System.out.println("Unlocking: " + username);
            }
            else
            {
                System.out.println("Locking: " + username);
            }
            if(CredentialsManager.modifyUser(userToLock, updatedUserInfo))
            {
                System.out.println("User Toggled successfully");
            }
            else
            {
                System.out.println("User not toggled");
            }
        }
    }
}
