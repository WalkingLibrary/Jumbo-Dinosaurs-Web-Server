package com.jumbodinosaurs;

import com.jumbodinosaurs.netty.SecureSessionHandlerInitializer;
import com.jumbodinosaurs.netty.SessionHandler;
import com.jumbodinosaurs.netty.SessionHandlerInitializer;
import com.jumbodinosaurs.objects.Domain;
import com.jumbodinosaurs.objects.RuntimeArguments;
import com.jumbodinosaurs.util.ClientTimer;
import com.jumbodinosaurs.util.DataController;
import com.jumbodinosaurs.util.OperatorConsole;
import sun.misc.BASE64Encoder;

import javax.net.ssl.HttpsURLConnection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class ServerControl
{
    
    private static Thread commandThread, port80Thread, port443Thread;
    private static DataController dataIO;
    private final ClientTimer oneHourTimer = new ClientTimer(3600000, new ComponentsListener());//One Hour Timer
    private final ClientTimer fiveMinuteTimer = new ClientTimer(300000, new ComponentsListener());//Five Minute Timer
    private static RuntimeArguments arguments;
    
    
    public ServerControl()
    {
        System.out.println("Starting Jumbo Dinosaurs .6");//G
        dataIO = new DataController(false);
        commandThread = new Thread(new OperatorConsole());
        commandThread.start();
        SessionHandler.redirectToSSL = false;
        port80Thread = new Thread(new SessionHandlerInitializer());
        port80Thread.start();
    }
    
    public ServerControl(RuntimeArguments arguments)
    {
        System.out.println("Starting Jumbo Dinosaurs .6");//G
        ServerControl.arguments = arguments;
        if(arguments.getDomains() != null)
        {
            this.intDomain();
            this.oneHourTimer.start();
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
        
        if(arguments.getCertificateKey() != null)//if user has a ssl certificate
        {
            port443Thread = new Thread(new SecureSessionHandlerInitializer(arguments.getCertificateKey()));
            port443Thread.start();
        }
        else
        {
            SessionHandler.redirectToSSL = false;
        }
    }
    
    public static RuntimeArguments getArguments()
    {
        return arguments;
    }
    
    
    private void intDomain()
    {
        
        boolean[] isDomainInitialized;
        isDomainInitialized = new boolean[arguments.getDomains().size()];
        boolean allDomainsInitialized = true;
        
        //Process for Initializing Domains
        //First the code will try to tell google to update our ip with the url
        //Second The code will then read the response
        //The code will do this for each domain given through RuntimeArguments
        //If google's response is that it failed then the code will start a 5 minutes timer to try again in 5 minutes
        //If a single domain fails then all domains are tried again later -> WIP: should only be the ones that failed
        ArrayList<Domain> domains = arguments.getDomains();
        for(int i = 0; i < arguments.getDomains().size(); i++)
        {
            try
            {
                //We pass the credentials to the url in the next line
                //https://username:password@domains.google.com/nic/update?hostname=subdomain.yourdomain.com
                //Example: ksafj391 1k3o13fk1 www.jumbodinosaurs.com
                String url = "https://" + domains.get(i).getUsername() + ":" + domains.get(i).getPassword() + "@domains.google.com/nic/update?hostname=" + domains.get(i).getDomain();
                
                URL address = new URL(url);
                
                
                // open HTTPS connection
                HttpURLConnection connection = null;
                connection = (HttpsURLConnection) address.openConnection();
                connection.setRequestMethod("GET");
                String authentication = domains.get(i).getUsername() + ':' + domains.get(i).getPassword();
                BASE64Encoder encoder = new BASE64Encoder();
                String encoded = encoder.encode((authentication).getBytes(StandardCharsets.UTF_8));
                connection.setRequestProperty("Authorization", "Basic " + encoded);
                // execute HTTPS request
                int returnCode = connection.getResponseCode();
                InputStream connectionIn = null;
                if(returnCode == 200)
                {
                    connectionIn = connection.getInputStream();
                }
                else
                {
                    connectionIn = connection.getErrorStream();
                }
                
                
                BufferedReader sc = new BufferedReader(new InputStreamReader(connectionIn));
                String response = "";
                while(sc.ready())
                {
                    response += sc.readLine();
                }
                OperatorConsole.printMessageFiltered(response, true, false);
                
                if(response.contains("good") || response.contains("nochg"))
                {
                    isDomainInitialized[i] = true;
                }
            }
            catch(IOException e)
            {
                OperatorConsole.printMessageFiltered("Error Setting Up Initializing Domain(s)", false, true);
                e.printStackTrace();
            }
            
        }
        
        
        for(boolean isInitialized : isDomainInitialized)
        {
            if(!isInitialized)
            {
                allDomainsInitialized = false;
                break;
            }
        }
        
        if(allDomainsInitialized)
        {
            OperatorConsole.printMessageFiltered("Domain Initialized", true, false);
            this.fiveMinuteTimer.stop();
        }
        else if(this.fiveMinuteTimer.getStatus())
        {
            OperatorConsole.printMessageFiltered("A Domain Failed To Initialize Starting 5 Min Timer", true, false);
            this.fiveMinuteTimer.start();
        }
        else
        {
            OperatorConsole.printMessageFiltered("A Domain Failed To Initialize", true, false);
        }
        
    }
    
    
    private class ComponentsListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            intDomain();
        }
    }
}
