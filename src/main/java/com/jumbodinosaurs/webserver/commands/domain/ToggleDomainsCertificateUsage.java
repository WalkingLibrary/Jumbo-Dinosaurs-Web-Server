package com.jumbodinosaurs.webserver.commands.domain;

import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.webserver.commands.OperatorConsole;
import com.jumbodinosaurs.webserver.domain.DomainManager;
import com.jumbodinosaurs.webserver.domain.util.SecureDomain;

public class ToggleDomainsCertificateUsage extends DomainCommand
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter Domain: ");
        String domainName = OperatorConsole.getEnsuredAnswer();
        SecureDomain domainToEdit = (SecureDomain) DomainManager.getDomain(domainName);
        if(domainToEdit == null)
        {
            return new MessageResponse("No Domain Found matching " + domainName);
        }
        domainToEdit.setHasCertificateFile(!domainToEdit.hasCertificateFile());
        DomainManager.updateDomain(domainToEdit);
        return new MessageResponse(domainName + " will use certificate: " + domainToEdit.hasCertificateFile());
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Allows the user to change the certificate usage of a specified domain";
    }
}
