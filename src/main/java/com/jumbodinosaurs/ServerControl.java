package com.jumbodinosaurs;
import com.jumbodinosaurs.netty.SecureSessionHandlerInitializer;
import com.jumbodinosaurs.netty.SessionHandler;
import com.jumbodinosaurs.netty.SessionHandlerInitializer;
import com.jumbodinosaurs.objects.Domain;
import com.jumbodinosaurs.objects.RuntimeArguments;
import com.jumbodinosaurs.util.ClientTimer;
import com.jumbodinosaurs.util.DataController;
import com.jumbodinosaurs.util.OperatorConsole;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
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
        this.dataIO = new DataController(false);
        this.commandThread = new Thread(new OperatorConsole());
        this.commandThread.start();
        SessionHandler.redirectToSSL = false;
        this.port80Thread = new Thread(new SessionHandlerInitializer());
        this.port80Thread.start();
    }

    public ServerControl(RuntimeArguments arguments)
    {
        System.out.println("Starting Jumbo Dinosaurs .6");//G
        this.arguments = arguments;
        if(arguments.getDomains() != null)
        {
            this.intDomain();
            this.oneHourTimer.start();
            this.dataIO = new DataController(true);
        }
        else
        {
            this.dataIO = new DataController(false);
        }

        this.commandThread = new Thread(new OperatorConsole());
        this.commandThread.start();


        this.port80Thread = new Thread(new SessionHandlerInitializer());
        this.port80Thread.start();

        if(arguments.getCertificateKey() != null)//if user has a ssl certificate
        {
            this.port443Thread = new Thread(new SecureSessionHandlerInitializer(arguments.getCertificateKey()));
            this.port443Thread.start();
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
        try
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
            for (int i = 0; i < arguments.getDomains().size(); i++)
            {


                //We pass the credentials to the url in the next line
                //https://username:password@domains.google.com/nic/update?hostname=subdomain.yourdomain.com
                //Example: ksafj391 1k3o13fk1 www.jumbodinosaurs.com
                String url = "https://" + domains.get(i).getUsername() + ":" + domains.get(i).getPassword() + "@domains.google.com/nic/update?hostname=" +
                        domains.get(i).getDomain();
                URL address = new URL(url);
                BufferedReader sc = new BufferedReader(new InputStreamReader(address.openStream()));
                String response = "";
                while(sc.ready())
                {
                    response += sc.readLine();
                }
                OperatorConsole.printMessageFiltered(response, true, false);

                if (response.contains("good") ||
                        response.contains("nochg"))
                {
                    isDomainInitialized[i] = true;
                }

            }


            for (boolean isInitialized : isDomainInitialized)
            {
                if (!isInitialized)
                {
                    allDomainsInitialized = false;
                    break;
                }
            }

            if (allDomainsInitialized)
            {
                OperatorConsole.printMessageFiltered("Domain Initialized", true, false);
                this.fiveMinuteTimer.stop();
            }
            else if (this.fiveMinuteTimer.getStatus())
            {
                OperatorConsole.printMessageFiltered("A Domain Failed To Initialize Starting 5 Min Timer", true, false);
                this.fiveMinuteTimer.start();
            }
            else
            {
                OperatorConsole.printMessageFiltered("A Domain Failed To Initialize", true, false);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Setting Up Initializing Domain(s)", false, true);
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
