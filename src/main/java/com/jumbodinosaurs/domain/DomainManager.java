package com.jumbodinosaurs.domain;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.devlib.json.GsonUtil;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.domain.util.Domain;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.netty.CertificateManager;
import com.jumbodinosaurs.util.ServerUtil;

import java.io.File;
import java.util.ArrayList;

public class DomainManager
{
    private static ArrayList<SecureDomain> domains;
    private static File domainDir = GeneralUtil.checkFor(ServerUtil.serverDataDir, "Domains");
    private static File domainMemory = GeneralUtil.checkFor(domainDir, "domain.json");
    
    
    
    
    public static void initializeDomains()
    {
        domains = loadDomains();
        if(domains == null)
        {
            domains = new ArrayList<SecureDomain>();
        }
        refreshCertificateFiles();
        for(Domain domain: domains)
        {
            domain.setGetDir(GeneralUtil.checkFor(ServerUtil.getDirectory, domain.getSecondLevelDomainName()));
            domain.setPostDir(GeneralUtil.checkFor(ServerUtil.postDirectory, domain.getSecondLevelDomainName()));
        }
    }
    
    public static SecureDomain getDomain(String domainName)
    {
        for(SecureDomain domain: domains)
        {
           if(domain.getDomain().equals(domainName))
            {
                return domain;
            }
        }
        return null;
    }
    
    public static void addDomain(SecureDomain domain)
    {
        domains.add(domain);
        saveDomains();
    }
    
    public static void removeDomain(SecureDomain domain)
    {
        domains.remove(domain);
        saveDomains();
    }
    
    public static void removeDomain(String domain)
    {
        removeDomain(getDomain(domain));
    }
    
    //Use the methods in this class to modify domains
    private static void saveDomains()
    {
        saveDomains(domains);
    }
    
    private static void saveDomains(ArrayList<SecureDomain> domains)
    {
        GsonUtil.saveObjectsToHolderList(domainMemory, domains, Domain.class);
    }
    
    private static ArrayList<SecureDomain> loadDomains()
    {
        ArrayList<SecureDomain> domains = new ArrayList<SecureDomain>();
        try
        {
            domains = GsonUtil.readObjectHoldersList(domainMemory, Domain.class, new TypeToken<ArrayList<SecureDomain>>(){});
        }
        catch(JsonParseException e)
        {
            e.printStackTrace();
            throw new IllegalStateException("Domain Data is Not Loadable");
        }
        
        if(domains != null)
        {
            for(Domain domain: domains)
            {
                domain.setType(domain.getClass().getSimpleName());
            }
        }
        return domains;
    }
    
    public static void refreshCertificateFiles()
    {
        File[] certificates = GeneralUtil.listFilesRecursive(CertificateManager.certificateDirectory);
    
        for(SecureDomain domain : domains)
        {
            if(domain.hasCertificateFile())
            {
                boolean hasCertificate = false;
                for(File certificate : certificates)
                {
                    //Certificates Should be JKS with .ks ending
                    //Example www.jumbodinosaurs.com.ks -> www.jumbodinosaurs.com
                    String certificateFileDomainName = ServerUtil.getTypelessName(certificate);
        
                    if(certificateFileDomainName.equals(domain.getDomain()))
                    {
                        ((SecureDomain)domain).setCertificateFile(certificate);
                        hasCertificate = true;
                    }
                }
                
                if(!hasCertificate)
                {
                    throw new IllegalStateException("Missing certificate for " + domain.getDomain());
                }
            }
        }
    }
    
    public static ArrayList<SecureDomain> getDomains()
    {
        return domains;
    }
    
    //Returns true if a domain was updated
    public static boolean updateDomain(SecureDomain updatedDomain)
    {
        for(Domain domain: domains)
        {
            if(domain.getDomain().equals(updatedDomain.getDomain()))
            {
                domains.remove(domain);
                domains.add(updatedDomain);
                saveDomains();
                return true;
            }
        }
        return false;
    }
}
