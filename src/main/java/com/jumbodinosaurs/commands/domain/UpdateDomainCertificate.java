package com.jumbodinosaurs.commands.domain;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.netty.CertificateManager;

import java.io.IOException;

public class UpdateDomainCertificate extends DomainCommand
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter Domain: ");
        String domain = OperatorConsole.getEnsuredAnswer();
        SecureDomain domainObject = DomainManager.getDomain(domain);
        if(domainObject == null)
        {
            return new MessageResponse("No Domain found matching " + domain);
        }
        try
        {
            CertificateManager.renewCertificate(domainObject);
    
            return new MessageResponse("Done Renewing " + domain + "'s certificate");
        }
        catch(IOException e)
        {
            return new MessageResponse(e.getMessage());
        }
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Updates the specified Domain's SSL Certificate";
    }
}
