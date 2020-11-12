package com.jumbodinosaurs.webserver.tasks.implementations.scheduled;

import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.task.ScheduledTask;
import com.jumbodinosaurs.devlib.util.WebUtil;
import com.jumbodinosaurs.devlib.util.objects.HttpResponse;
import com.jumbodinosaurs.webserver.domain.DomainManager;
import com.jumbodinosaurs.webserver.domain.util.Domain;
import com.jumbodinosaurs.webserver.domain.util.UpdatableDomain;
import sun.misc.BASE64Encoder;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UpdateDNS extends ScheduledTask
{
    //TODO make it update all domains
    
    
    public UpdateDNS(ScheduledThreadPoolExecutor executor)
    {
        super(executor);
    }
    
    
    @Override
    public int getPeriod()
    {
        return 1;
    }
    
    @Override
    public TimeUnit getTimeUnit()
    {
        return TimeUnit.HOURS;
    }
    
    //using google's Dynamic IP APi https://support.google.com/domains/answer/6147083?hl=en
    //This code will run every getTimeUnit()
    @Override
    public void run()
    {
        LogManager.consoleLogger.debug("Updating Domains");
        for(Domain domain: DomainManager.getDomains())
        {
            if(domain instanceof UpdatableDomain)
            {
                try
                {
                    //Url to send info to
                    String url = "https://domain.google.com/nic/update?hostname=" + domain.getDomain();
                    URL address = new URL(url);
                    // open HTTPS connection
                    HttpURLConnection connection;
                    connection = (HttpsURLConnection) address.openConnection();
                    connection.setRequestMethod("GET");
                    //Credentials for Updating info
                    String authentication = ((UpdatableDomain) domain).getUsername() + ':' + ((UpdatableDomain) domain).getPassword();
                    BASE64Encoder encoder = new BASE64Encoder();
                    String encoded = encoder.encode((authentication).getBytes(StandardCharsets.UTF_8));
                    connection.setRequestProperty("Authorization", "Basic " + encoded);
                    //Get Response from Google
                    boolean wasGoodUpdate = false;
                    try
                    {
                        HttpResponse response = WebUtil.getResponse(connection);
                        wasGoodUpdate = response.getResponse().contains("good");
                        wasGoodUpdate = wasGoodUpdate || response.getResponse().contains("nochg");
                    }
                    catch(IOException e)
                    {
                        LogManager.consoleLogger.error(e.getMessage(), e);
                    }
                    
                    if(!wasGoodUpdate)
                    {
                        LogManager.consoleLogger.warn("Domain Failed To Update\n Domain: " + domain.getDomain());
                    }
                }
                catch(IOException e)
                {
                    LogManager.consoleLogger.error(e.getMessage(), e);
                }
            }
        }
    }
}
