package com.jumbodinosaurs.commands.email;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.email.Email;
import com.jumbodinosaurs.devlib.email.EmailManager;

public class RemoveEmail extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter the email to remove: ");
        String emailToRemove = OperatorConsole.getEnsuredAnswer();
        Email tempEmail = new Email(emailToRemove, "");
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
