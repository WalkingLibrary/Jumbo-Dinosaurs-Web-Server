package com.jumbodinosaurs;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Scanner;

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
    private final ClientTimer serverInt = new ClientTimer(5000, new ComponentsListener());//Five Second Timer
    private final ClientTimer domainInt = new ClientTimer(3600000, new ComponentsListener());//One Hour Timer
    private final ClientTimer fiveMin = new ClientTimer(300000, new ComponentsListener());//Five Minute Timer
    private int count = 0;
    private int startUpTries = 5;
    private String[][] credentials;
    private String[] domains;
    private boolean[] domainsInit;


    public ServerControl()
    {
        System.out.println("Starting Jumbo Dinosaurs .5");
        this.dataIO = new DataController();
        this.initServer();

    }

    public ServerControl(String[][] credentials, String[] domains)
    {
        System.out.println("Starting Jumbo Dinosaurs .5");
        this.credentials = credentials;
        this.domains = domains;
        this.domainsInit = new boolean[this.credentials.length];
        this.dataIO = new DataController(this.domains);
        this.domainInt.start();
        this.intDomain();
        this.initServer();

    }


    private void intDomain()
    {
        try
        {
            //Process for int Domain
            //First try to tell google domains to update with sh script
            //read from renew.txt for code from google
            //if code is a success status code then domain is initiated else start a 5 min timer to try again in 5 min
            for (int i = 0; i < this.credentials.length; i++)
            {

                //https://username:password@domains.google.com/nic/update?hostname=subdomain.yourdomain.com
                //Credentials Should be in Username Password Domain order
                Runtime.getRuntime().exec("sudo bash reNewDomain.sh " +
                        this.credentials[i][0] +
                        " " +
                        this.credentials[i][1] +
                        " " +
                        this.domains[i]);

                File wgetOutput = new File(System.getProperty("user.dir") + "/renew.txt");
                System.out.println("wgetOutput Path: " + wgetOutput.getPath());

                String fileContents = "";
                Scanner input = new Scanner(wgetOutput);
                while (input.hasNextLine())
                {
                    fileContents += input.nextLine();
                }
                System.out.println(fileContents);


                if (fileContents.contains("good") ||
                        fileContents.contains("nochg"))
                {
                    this.domainsInit[i] = true;
                }

            }

            boolean allInit = true;
            for (boolean isInited : this.domainsInit)
            {
                if (!isInited)
                {
                    allInit = false;
                    break;
                }
            }

            if (allInit)
            {
                System.out.println("Domain Initialized");
                this.fiveMin.stop();
            }
            else if (this.fiveMin.getStatus())
            {
                System.out.println("A Domain Failed To Initialize Starting 5 Min Timer");
                this.fiveMin.start();
            }
            else
            {
                System.out.println("A Domain Failed To Initialize");
            }
        }
        catch (Exception e)
        {
            System.out.println("Error Setting Up Initializing Domain(s)");
            e.printStackTrace();
            System.out.println(e.getCause());
        }
    }

    /*
     * @Function:
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

            Runnable userInput = new OperatorConsole(this.dataIO);
            this.commandThread = new Thread(userInput);
            this.commandThread.start();


            if (!this.serverInt.getStatus())
            {
                this.serverInt.stop();
            }
            bootstrap.bind(80).sync().channel().closeFuture().sync();
        }
        catch (Exception e)
        {
            System.out.println("Error Creating Server on port 80");
            e.printStackTrace();
            System.out.println(e.getCause());
            this.serverInt.start();
        }
        finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }






    private class ComponentsListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource().equals(serverInt))
            {
                if (count < 5)
                {
                    initServer();
                    count++;
                }
                else if (count > 5)
                {
                    System.out.println("Tried " + startUpTries + " Times and Failed");
                    System.exit(1);
                }
            }
            else if (e.getSource().equals(domainInt))
            {
                intDomain();
            }
            else if (e.getSource().equals(fiveMin))
            {
                intDomain();
            }
        }
    }
}
