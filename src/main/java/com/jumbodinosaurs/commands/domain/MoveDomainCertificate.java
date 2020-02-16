package com.jumbodinosaurs.commands.domain;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.netty.CertificateManager;

public class MoveDomainCertificate extends Command
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
