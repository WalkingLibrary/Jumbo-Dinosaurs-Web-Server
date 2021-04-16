package com.jumbodinosaurs.webserver.commands.email;

import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.email.DefaultEmail;
import com.jumbodinosaurs.devlib.email.Email;
import com.jumbodinosaurs.devlib.email.EmailManager;
import com.jumbodinosaurs.devlib.util.OperatorConsole;

public class RemoveEmail extends EmailCommand
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter the email to remove: ");
        String emailToRemove = OperatorConsole.getEnsuredAnswer();
        Email tempEmail = new DefaultEmail(emailToRemove, "");
        boolean wasRemoved = EmailManager.removeEmail(tempEmail);
        if(wasRemoved)
        {
            return new MessageResponse("Removed " + emailToRemove);
        }
        return new MessageResponse(emailToRemove + " was Not Removed");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Allows The User To Remove an email for the email manager";
    }
}
