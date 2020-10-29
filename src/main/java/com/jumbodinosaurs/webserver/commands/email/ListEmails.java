package com.jumbodinosaurs.webserver.commands.email;

import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.email.Email;
import com.jumbodinosaurs.devlib.email.EmailManager;

public class ListEmails extends EmailCommand
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        for(Email email: EmailManager.getEmails())
        {
            System.out.println("Email: " + email.getUsername());
        }
        return new MessageResponse("These are the emails in the EmailManager");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "List emails in the email Manager";
    }
}
