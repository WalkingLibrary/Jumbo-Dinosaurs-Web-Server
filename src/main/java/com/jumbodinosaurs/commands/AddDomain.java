package com.jumbodinosaurs.commands;

import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.email.NoSuchEmailException;
import com.jumbodinosaurs.devlib.options.NoSuchOptionException;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.Domain;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.domain.util.UpdatableDomain;
import com.jumbodinosaurs.netty.CertificateManager;

import java.util.Scanner;

public class AddDomain extends Command
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
            Domain domainObject = new Domain(domain);
            DomainManager.addDomain(domainObject);
            return new MessageResponse("Added Domain: " + domain);
        }
     
        
        //since the domain is update able we need the credentials needed to update it
        String username, password;
        System.out.println("Enter the Username: ");
        username = OperatorConsole.getEnsuredAnswer();
        System.out.println("Enter the Password: ");
        password = OperatorConsole.getEnsuredAnswer();
    
        System.out.println("Is the Domain Securable?(y/n)");
        userInputScanner = new Scanner(System.in);
        userInput = userInputScanner.nextLine();
        if(userInput.toLowerCase().contains("n"))
        {
            UpdatableDomain domainObject = new UpdatableDomain(domain, username, password);
            DomainManager.addDomain(domainObject);
            return new MessageResponse("Added Updatable Domain: " + domain);
        }
        
        String certificatePassword;
        System.out.println("Enter Certificate Password: ");
        certificatePassword = OperatorConsole.getEnsuredAnswer();
    
        SecureDomain secureDomain = new SecureDomain(domain, username, password, certificatePassword);
        try
        {
            CertificateManager.setupSecureDomain(secureDomain);
            DomainManager.addDomain(secureDomain);
            return new MessageResponse("Added Secure Domain: " + domain);
        }
        catch(NoSuchEmailException e)
        {
            return new MessageResponse("Missing Email From Email Manager");
        }
        catch(NoSuchOptionException e)
        {
            return new MessageResponse("No Email Set For The Server");
        }
    }
    
    
    
    @Override
    public String getHelpMessage()
    {
        return "Allows the User To Add a Domain.";
    }
}
