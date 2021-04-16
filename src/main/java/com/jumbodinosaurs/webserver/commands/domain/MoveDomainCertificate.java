package com.jumbodinosaurs.webserver.commands.domain;

import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.util.OperatorConsole;
import com.jumbodinosaurs.webserver.domain.DomainManager;
import com.jumbodinosaurs.webserver.domain.util.SecureDomain;
import com.jumbodinosaurs.webserver.netty.CertificateManager;

public class MoveDomainCertificate extends DomainCommand
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter Domain: ");
        String domain = OperatorConsole.getEnsuredAnswer();
        SecureDomain secureDomain = DomainManager.getDomain(domain);
        if(secureDomain == null)
        {
        
            return new MessageResponse("No Domain Found matching " + domain);
            
        }
    
        CertificateManager.moveCertificateFile(secureDomain);
        return new MessageResponse("Moved Certificate for " + domain);
      
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Moves a specified Domain's certificate files to the certificate Directory";
    }
}
