package com.jumbodinosaurs.netty;

import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.domain.util.Domain;
import com.jumbodinosaurs.util.ServerUtil;

import java.io.File;

public class CertificateManager
{
    
    public static File certificateDirectory = GeneralUtil.checkFor(ServerUtil.serverDataDir, "Certificates");
    
    
    public void renewCertificates()
    {
    
    }
    
    public void makeCertificate(Domain domain)
    {
    
    }
}
