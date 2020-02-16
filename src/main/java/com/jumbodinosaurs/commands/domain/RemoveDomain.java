package com.jumbodinosaurs.commands.domain;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.domain.DomainManager;

public class RemoveDomain extends Command
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
