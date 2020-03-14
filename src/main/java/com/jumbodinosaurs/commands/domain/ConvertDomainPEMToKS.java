package com.jumbodinosaurs.commands.domain;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.netty.CertificateManager;

import java.io.IOException;

public class ConvertDomainPEMToKS extends DomainCommand
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
        try
        {
            CertificateManager.convertPemToKS(secureDomain);
            return new MessageResponse("Converted Certificate for " + domain);
        }
        catch(IOException e)
        {
            return new MessageResponse(e.getMessage());
        }
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Allows The user to convert a domains certificate from PEM to KS";
    }
}
