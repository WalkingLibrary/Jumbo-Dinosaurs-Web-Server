package com.jumbodinosaurs.webserver.commands.domain;

import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.util.OperatorConsole;
import com.jumbodinosaurs.webserver.domain.DomainManager;
import com.jumbodinosaurs.webserver.domain.util.SecureDomain;
import com.jumbodinosaurs.webserver.netty.CertificateManager;

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
