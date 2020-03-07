package com.jumbodinosaurs.commands.domain;

import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.Domain;

public class ListDomains extends DomainCommand
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        for(Domain domain: DomainManager.getDomains())
        {
            System.out.println("Domain: " + domain.getDomain());
        }
        return new MessageResponse("These are the domains in service");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Lists The domains in Service";
    }
}
