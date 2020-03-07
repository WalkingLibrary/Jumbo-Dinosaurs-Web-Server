package com.jumbodinosaurs.commands.email;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.email.Email;
import com.jumbodinosaurs.devlib.email.EmailManager;

public class AddEmail extends EmailCommand
{
    
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter Username: ");
        String username = OperatorConsole.getEnsuredAnswer();
        System.out.println("Enter Password: ");
        String password = OperatorConsole.getEnsuredAnswer();
        Email email = new Email(username, password);
        EmailManager.addEmail(email);
        return new MessageResponse("Added " + username + " to EmailManager");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Allows the user to add Emails to the Email Manager";
    }
}
