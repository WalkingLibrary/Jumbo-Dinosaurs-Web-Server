package com.jumbodinosaurs.tasks;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.util.WebUtil;
import com.jumbodinosaurs.devlib.util.objects.HttpResponse;
import com.jumbodinosaurs.domain.util.UpdatableDomain;
import sun.misc.BASE64Encoder;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateDNS implements Runnable
{
    private UpdatableDomain domain;
    
    public UpdateDNS(UpdatableDomain domain)
    {
        this.domain = domain;
    }
    
    public UpdatableDomain getDomain()
    {
        return domain;
    }
    
    public void setDomain(UpdatableDomain domain)
    {
        this.domain = domain;
    }
    
    
    
    //using google's Dynamic IP APi https://support.google.com/domains/answer/6147083?hl=en
    @Override
    public void run()
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
            String authentication = domain.getUsername() + ':' + domain.getPassword();
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
            catch(Exception e)
            {
                e.printStackTrace();
            }
            if(!wasGoodUpdate)
            {
                OperatorConsole.printMessageFiltered("Domain Failed To Update\nDomain: " + domain.getDomain(),
                                                     false,
                                                     true);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
