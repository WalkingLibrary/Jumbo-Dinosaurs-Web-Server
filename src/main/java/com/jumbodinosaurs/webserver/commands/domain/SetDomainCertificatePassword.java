package com.jumbodinosaurs.webserver.commands.domain;

import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.util.OperatorConsole;
import com.jumbodinosaurs.webserver.domain.DomainManager;
import com.jumbodinosaurs.webserver.domain.util.SecureDomain;

public class SetDomainCertificatePassword extends DomainCommand
{
    
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter Domain: ");
        String domain = OperatorConsole.getEnsuredAnswer();
        SecureDomain domainToModify = DomainManager.getDomain(domain);
        if(domainToModify == null)
        {
            return new MessageResponse("No domain found matching " + domain);
        }
        System.out.println("Enter New Certificate Password: ");
        String newCertificatePassword = OperatorConsole.getEnsuredAnswer();
        domainToModify.setCertificatePassword(newCertificatePassword);
        DomainManager.updateDomain(domainToModify);
        return new MessageResponse("Changed " + domain + "'s certificate password");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Allows the user to change a domains certificate password";
    }
}
