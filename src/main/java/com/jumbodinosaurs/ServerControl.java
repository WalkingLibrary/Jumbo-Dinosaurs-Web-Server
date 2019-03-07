package com.jumbodinosaurs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/*
  Main Server Control
      Order of Initialization
         -DataController
         -Operator Console Thread then
         -Domains given via args (if any)
  After Initializing These three things the program will then Wait for incoming connections on port 80.
 */
public class ServerControl
{

    private static Thread commandThread, port80Thread, port443Thread;
    private static DataController dataIO;
    private final ClientTimer oneHourTimer = new ClientTimer(3600000, new ComponentsListener());//One Hour Timer
    private final ClientTimer fiveMinuteTimer = new ClientTimer(300000, new ComponentsListener());//Five Minute Timer
    private static RuntimeArguments arguments;


    public ServerControl()
    {
        System.out.println("Starting Jumbo Dinosaurs .5");//G
        this.dataIO = new DataController(false);
        this.commandThread = new Thread(new OperatorConsole());
        this.commandThread.start();
        SessionHandler.redirectToSSL = false;
        this.port80Thread = new Thread(new SessionHandlerInitializer());
        this.port80Thread.start();
    }

    public ServerControl(RuntimeArguments arguments)
    {
        System.out.println("Starting Jumbo Dinosaurs .5");//G
        this.arguments = arguments;
        this.dataIO = new DataController(true);
        this.commandThread = new Thread(new OperatorConsole());
        this.commandThread.start();
        this.oneHourTimer.start();
        this.intDomain();
        this.port80Thread = new Thread(new SessionHandlerInitializer());
        this.port80Thread.start();



        if(!arguments.getCertificateKey().equals(""))//if user has a ssl certificate
        {
            this.port443Thread = new Thread(new SecureSessionHandlerInitializer(arguments.getCertificateKey()));
            this.port443Thread.start();
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
            //First the code will try to tell google to update our ip with a sh script
            //Second The code will then read from renew.txt for the response from google
            //The code will do this for each domain given through (String[] args)
            //If google's response is that it failed then the code will start a 5 minutes timer to try again in 5 minutes
            //If a single domain fails then all domains are tried again late -> WIP: should only be the ones that failed
            ArrayList<Domain> domains = arguments.getDomains();
            for (int i = 0; i < arguments.getDomains().size(); i++)
            {


                //We pass the credentials to the url in the next line
                //https://username:password@domains.google.com/nic/update?hostname=subdomain.yourdomain.com
                //The sh script uses the linux command wget with the url above.
                //Credentials Should be in Username Password Domain order
                //Example: ksafj391 1k3o13fk1 www.jumbodinosaurs.com
                Runtime.getRuntime().exec("sudo bash reNewDomain.sh " +
                        domains.get(i).getUsername() +
                        " " +
                        domains.get(i).getPassword() +
                        " " +
                        domains.get(i).getDomain());
                File wgetOutput = new File(System.getProperty("user.dir") + "/renew.txt");
                OperatorConsole.printMessageFiltered("wgetOutput Path: " + wgetOutput.getPath(),true,false);
                String fileContents = this.dataIO.getFileContents(wgetOutput);
                OperatorConsole.printMessageFiltered(fileContents, true, false);

                if (fileContents.contains("good") ||
                        fileContents.contains("nochg"))
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
