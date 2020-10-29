package com.jumbodinosaurs.webserver.commands.domain;

import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.webserver.commands.OperatorConsole;
import com.jumbodinosaurs.webserver.domain.DomainManager;
import com.jumbodinosaurs.webserver.domain.util.SecureDomain;
import com.jumbodinosaurs.webserver.domain.util.UpdatableDomain;

import java.util.Scanner;

public class AddDomain extends DomainCommand
{
    
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        
        
        System.out.println("Enter Domain: ");
        System.out.println("Example: www.jumbodinosaurs.com");
        String domain = OperatorConsole.getEnsuredAnswer();
        
        System.out.println("Is the Domain Updatable?(y/n)");
        Scanner userInputScanner = new Scanner(System.in);
        String userInput = "";
        userInput = userInputScanner.nextLine();
        if(userInput.toLowerCase().contains("n"))
        {
            SecureDomain domainObject = new SecureDomain(domain);
            DomainManager.addDomain(domainObject);
            return new MessageResponse("Added Domain: " + domain);
        }
        
        
        //since the domain is update able we need the credentials needed to update it
        String username, password;
        System.out.println("Enter the Username: ");
        username = OperatorConsole.getEnsuredAnswer();
        System.out.println("Enter the Password: ");
        password = OperatorConsole.getEnsuredAnswer();
        
        UpdatableDomain domainObject = new UpdatableDomain(domain, username, password);
        DomainManager.addDomain(domainObject);
        return new MessageResponse("Added Updatable Domain: " + domain);
        
    }
    
    
    @Override
    public String getHelpMessage()
    {
        return "Allows the User To Add a Domain.";
    }
}
