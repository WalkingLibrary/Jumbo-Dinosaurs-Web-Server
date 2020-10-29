package com.jumbodinosaurs.webserver.commands.email;

import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.email.DefaultEmail;
import com.jumbodinosaurs.devlib.email.Email;
import com.jumbodinosaurs.devlib.email.EmailManager;
import com.jumbodinosaurs.devlib.email.GoogleAPIEmail;
import com.jumbodinosaurs.webserver.commands.OperatorConsole;

public class AddEmail extends EmailCommand
{
    
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter Username: ");
        String username = OperatorConsole.getEnsuredAnswer();
        System.out.println("Is this a GoogleAPIEmail?(y/n)");
        String isGoogleAAPEmailResponse = OperatorConsole.getEnsuredAnswer();
        if(isGoogleAAPEmailResponse.contains("n"))
        {
            System.out.println("Enter Password: ");
            String password = OperatorConsole.getEnsuredAnswer();
            Email email = new DefaultEmail(username, password);
            EmailManager.addEmail(email);
            return new MessageResponse("Added " + username + " to EmailManager");
        }
    
        System.out.println("Enter the Credentials Json: ");
        String credentialsJson = OperatorConsole.getEnsuredAnswer();
        Email googleAPIEmail = new GoogleAPIEmail(username,
                                                  credentialsJson,
                                                  EmailManager.getEmailMemory().getParentFile().getAbsolutePath());
        EmailManager.addEmail(googleAPIEmail);
        return new MessageResponse("Added " + username + " to EmailManager");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Allows the user to add Emails to the Email Manager";
    }
}
