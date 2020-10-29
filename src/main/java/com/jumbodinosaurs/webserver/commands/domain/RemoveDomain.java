package com.jumbodinosaurs.webserver.commands.domain;

import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.webserver.commands.OperatorConsole;
import com.jumbodinosaurs.webserver.domain.DomainManager;

public class RemoveDomain extends DomainCommand
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter Domain: ");
        String domainToRemove = OperatorConsole.getEnsuredAnswer();
        DomainManager.removeDomain(domainToRemove);
        return new MessageResponse("Removed " + domainToRemove);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Allows the user to remove a domain from the domain manager";
    }
}
