package com.jumbodinosaurs.tasks.implementations.scheduled;

import com.jumbodinosaurs.devlib.netty.ChannelManager;
import com.jumbodinosaurs.devlib.netty.ConnectionListenerInitializer;
import com.jumbodinosaurs.devlib.task.ScheduledTask;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.log.LogManager;
import com.jumbodinosaurs.netty.CertificateManager;
import com.jumbodinosaurs.netty.handler.http.HTTPHandler;
import com.jumbodinosaurs.netty.initializer.SecureHTTPConnectListenerInitializer;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UpdateDomainCertificates extends ScheduledTask
{
    
    
    public UpdateDomainCertificates(ScheduledThreadPoolExecutor executor)
    {
        super(executor);
    }
    
    
    @Override
    public int getPeriod()
    {
        return 24;
    }
    
    @Override
    public TimeUnit getTimeUnit()
    {
        return TimeUnit.HOURS;
    }
    
    
    @Override
    public void run()
    {
        /*
         * Process for updating domain certificates
         * Go through domains and update certificates if need
         * Restart HTTPS Channels with new context if need
         */
    
        LogManager.consoleLogger.debug("Checking Domain Certificates");
        boolean channelNeedsRestart = false;
        
        //Go through domains and update certificates if need
        for(SecureDomain domain : DomainManager.getDomains())
        {
            if(domain.hasCertificateFile())
            {
                try
                {
                    /*
                     * Process for updating a certificate
                     * Load the certificate
                     * Check the expiration date
                     * update if needed
                     * flag the channel to be restarted
                     */
                    
                    //Load the certificate
                    KeyStore keyStore = KeyStore.getInstance("JKS");
                    keyStore.load(new FileInputStream(domain.getCertificateFile()),
                                  domain.getCertificatePassword().toCharArray());
                    
                    /* Check the expiration date
                     * https://stackoverflow.com/questions/44838084/java-code-to-display-expiration-date-of-certificates-in-a-java-keystore
                     * Certificate can house more than one certificate and date?
                     * So we go through the certificate and get the closes date.
                     *
                     *
                     */
                    Date dateToCheck = null;
                    Enumeration enumeration = keyStore.aliases();
                    while(enumeration.hasMoreElements())
                    {
                        String alias = (String) enumeration.nextElement();
                        Date certExpiryDate = ((X509Certificate) keyStore.getCertificate(alias)).getNotAfter();
                        if(dateToCheck == null)
                        {
                            dateToCheck = certExpiryDate;
                        }
                        else
                        {
                            if(dateToCheck.after(certExpiryDate))
                            {
                                dateToCheck = certExpiryDate;
                            }
                        }
                    }
    
                    //update if needed
                    if(dateToCheck != null)
                    {
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime convertedDateToCheck =
                                LocalDateTime.ofInstant(dateToCheck.toInstant(), ZoneId.systemDefault());
                   
                        if(now.plusDays(1).isAfter(convertedDateToCheck))
                        {
                            //UPDATE CERTIFICATE
                            CertificateManager.updateDomainCertificate(domain);
                            //flag the channel to be restarted
                            channelNeedsRestart = true;
                        }
                    }
                    
                }
                catch(Exception e)
                {
                    LogManager.consoleLogger.error(e.getMessage(), e);
                }
            }
        }
        
        
        //Restart HTTPS Channels with new context if need
        if(channelNeedsRestart)
        {
            /*
             * Process for restarting the HTTPS Channel
             * Get the Channel Listener on port 443 from the ChannelManager
             * Restart it with new certificate context
             */
    
            //Get the Channel Listener on port 443 from the ChannelManager
            int httpsPort = 443;
            ConnectionListenerInitializer port443Listener = null;
            for(ConnectionListenerInitializer listener: ChannelManager.getConnectionListeners())
            {
                if(listener.getPort() == httpsPort)
                {
                    ChannelManager.removeAndShutdownListener(listener);
                    break;
                }
            }
            
            
            //Restart it with new certificate context
            DomainManager.refreshCertificateFiles();
            SecureHTTPConnectListenerInitializer port443 = new SecureHTTPConnectListenerInitializer(443, new HTTPHandler());
            ChannelManager.addConnectionListener(port443);
            
            
        }
    }
}
