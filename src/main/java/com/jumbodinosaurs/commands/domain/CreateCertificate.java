package com.jumbodinosaurs.commands.domain;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.email.NoSuchEmailException;
import com.jumbodinosaurs.devlib.options.NoSuchOptionException;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.netty.CertificateManager;

public class CreateCertificate extends Command
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
        String certificatePassword;
        System.out.println("Enter Certificate Password: ");
        certificatePassword = OperatorConsole.getEnsuredAnswer();
        domainToEdit.setCertificatePassword(certificatePassword);
        
        try
        {
            CertificateManager.setupSecureDomain(domainToEdit);
            domainToEdit.setHasCertificateFile(true);
            DomainManager.updateDomain(domainToEdit);
            return new MessageResponse("Created Certificate for " + domainName);
        }
        catch(NoSuchEmailException e)
        {
            return new MessageResponse("Missing Email From Email Manager");
        }
        catch(NoSuchOptionException e)
        {
            return new MessageResponse("No Email Set For The Server");
        }
    }
    
    @Override
    public String getHelpMessage()
    {
        return null;
    }
}
