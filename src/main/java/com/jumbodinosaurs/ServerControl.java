package com.jumbodinosaurs;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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

    private static Thread commandThread;
    private static DataController dataIO;
    private final ClientTimer oneHourTimer = new ClientTimer(3600000, new ComponentsListener());//One Hour Timer
    private final ClientTimer fiveMinuteTimer = new ClientTimer(300000, new ComponentsListener());//Five Minute Timer
    private String[][] credentials;
    private String[] domains;
    private static OperatorConsole console;


    public ServerControl()
    {
        System.out.println("Starting Jumbo Dinosaurs .5");//G
        this.dataIO = new DataController();
        this.console = new OperatorConsole(this.dataIO);
        this.commandThread = new Thread(this.console);
        this.commandThread.start();
        this.initServer();

    }

    public ServerControl(String[][] credentials, String[] domains)
    {
        System.out.println("Starting Jumbo Dinosaurs .5");//G

        this.dataIO = new DataController(this.domains);
        this.console = new OperatorConsole(this.dataIO);
        this.commandThread = new Thread(this.console);
        this.commandThread.start();

        this.credentials = credentials;
        this.domains = domains;
        this.oneHourTimer.start();
        this.intDomain();


        this.initServer();
    }




    /* Code Starts BootStrapServer and Operator Console
     *
     */
    private void initServer()
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try
        {
             ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SessionHandlerInitializer(this.dataIO));
            bootstrap.bind(80).sync().channel().closeFuture().sync();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Creating Server on port 80",false, true);
        }
        finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void intDomain()
    {
        try
        {
            boolean[] isDomainInitialized;
            isDomainInitialized = new boolean[this.credentials.length];
            boolean allDomainsInitialized = true;

            //Process for Initializing Domains
            //First the code will try to tell google to update our ip with a sh script
            //Second The code will then read from renew.txt for the response from google
            //The code will do this for each domain given through (String[] args)
            //If google's response is that it failed then the code will start a 5 minutes timer to try again in 5 minutes
            //If a single domain fails then all domains are tried again late -> WIP: should only be the ones that failed
            for (int i = 0; i < this.credentials.length; i++)
            {


                //We pass the credentials to the url in the next line
                //https://username:password@domains.google.com/nic/update?hostname=subdomain.yourdomain.com
                //The sh script uses the linux command wget with the url above.
                //Credentials Should be in Username Password Domain order
                //Example: ksafj391 1k3o13fk1 www.jumbodinosaurs.com
                Runtime.getRuntime().exec("sudo bash reNewDomain.sh " +
                        this.credentials[i][0] +
                        " " +
                        this.credentials[i][1] +
                        " " +
                        this.domains[i]);
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
