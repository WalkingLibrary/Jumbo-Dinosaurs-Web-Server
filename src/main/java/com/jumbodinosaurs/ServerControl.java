package com.jumbodinosaurs;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.netty.SecureSessionHandlerInitializer;
import com.jumbodinosaurs.netty.SessionHandlerInitializer;
import com.jumbodinosaurs.objects.Domain;
import com.jumbodinosaurs.objects.RuntimeArguments;
import com.jumbodinosaurs.objects.URLResponse;
import com.jumbodinosaurs.util.DataController;
import com.jumbodinosaurs.util.Timer;
import sun.misc.BASE64Encoder;

import javax.net.ssl.HttpsURLConnection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class ServerControl
{
    
    private static Thread commandThread, port80Thread, port443Thread;
    private static DataController dataIO;
    private final int FIVE_MINUTES_IN_MILLSECONDS = 300000;
    private final int ONE_HOUR_IN_MILLSECONDS = 3600000;
    private final Timer ONE_HOUR_TIMER = new Timer(ONE_HOUR_IN_MILLSECONDS, new ComponentsListener(), false, 0);
    private final Timer FIVE_MINUTE_TIMER = new Timer(FIVE_MINUTES_IN_MILLSECONDS, new ComponentsListener(), false);
    private static RuntimeArguments arguments;
    private static ArrayList<Domain> updatableDomains = new ArrayList<Domain>();
    
    
    public ServerControl()
    {
        System.out.println("Starting Jumbo Dinosaurs .6");//G
        System.out.println("Test Mode: " + false);
        dataIO = new DataController(false);
        commandThread = new Thread(new OperatorConsole());
        commandThread.start();
        OperatorConsole.redirectToSSL = false;
        port80Thread = new Thread(new SessionHandlerInitializer());
        port80Thread.start();
    }
    
    public ServerControl(RuntimeArguments arguments)
    {
        System.out.println("Starting Jumbo Dinosaurs .6");//G
        ServerControl.arguments = arguments;
        System.out.println("Test Mode: " + ServerControl.arguments.isInTestMode());
        
        if(arguments.getDomains() != null && arguments.getDomains().size() > 0)
        {
            for(Domain domain : arguments.getDomains())
            {
                if(domain.getUsername() != null && domain.getPassword() != null && domain.getDomain() != null)
                {
                    this.updatableDomains.add(domain);
                }
            }
            if(this.updatableDomains.size() > 0)
            {
                System.out.println("Updatable Domains:");
                for(Domain domain: this.updatableDomains)
                {
                    System.out.println(domain.getDomain());
                }
                this.ONE_HOUR_TIMER.start();
            }
            
            dataIO = new DataController(true);
            
        }
        else
        {
            dataIO = new DataController(false);
        }
        
        commandThread = new Thread(new OperatorConsole());
        commandThread.start();
        
        
        port80Thread = new Thread(new SessionHandlerInitializer());
        port80Thread.start();
        
        
        port443Thread = new Thread(new SecureSessionHandlerInitializer());
        port443Thread.start();
    }
    
    public static RuntimeArguments getArguments()
    {
        return arguments;
    }
    
    
    //using google's Dynamic IP APi https://support.google.com/domains/answer/6147083?hl=en
    private void updateDNSRecords()
    {
        LocalDateTime now = LocalDateTime.now();
        
        //Process for Updating DNS Records
        //for each domain we check it's last good update date and then make a HTTPSURLConnection accordingly
        //then read the response
        //if the response is good or nochg we set the last goodupdateDate of the domain to now
        //we then check to make sure each updatable domain updated successfully and if one failed we start a five min
        // timer to try again
        // either way we check the status of the 5 min timer and make sure it's on and off when it needs to be
        boolean allDomainsUpdatedSuccessfully = true;
        for(Domain domain : updatableDomains)
        {
            if(domain.getLastGoodUpdateDate() == null || now.minusMinutes((long) 55).isAfter(domain.getLastGoodUpdateDate()))
            {
                try
                {
                    //Url to send info to
                    String url = "https://domains.google.com/nic/update?hostname=" + domain.getDomain();
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
                    URLResponse response = DataController.getResponse(connection);
                    if(response != null)
                    {
                        if(response.getResponse().contains("good") || response.getResponse().contains("nochg"))
                        {
                            domain.setLastGoodUpdateDate(now);
                        }
                        else
                        {
                            allDomainsUpdatedSuccessfully = false;
                            OperatorConsole.printMessageFiltered("Domain Failed To Update\nDomain: " + domain.getDomain(),
                                                                 false,
                                                                 true);
                        }
                    }
                    else
                    {
                        allDomainsUpdatedSuccessfully = false;
                        OperatorConsole.printMessageFiltered("Domain Failed To Update\nDomain: " + domain.getDomain(),
                                                             false,
                                                             true);
                    }
                }
                catch(IOException e)
                {
                    OperatorConsole.printMessageFiltered("Exception: Domain Failed To Update\nDomain: " + domain.getDomain(),
                                                         false,
                                                         true);
                    e.printStackTrace();
                }
                
                
            }
        }
        
        if(allDomainsUpdatedSuccessfully || this.FIVE_MINUTE_TIMER.isRunning())
        {
            OperatorConsole.printMessageFiltered("All Domains Successfully Updated", true, false);
            this.FIVE_MINUTE_TIMER.stop();
        }
        else if(!this.FIVE_MINUTE_TIMER.isRunning())
        {
            this.FIVE_MINUTE_TIMER.start();
        }
    }
    
    
    private class ComponentsListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            updateDNSRecords();
        }
    }
}
