package com.jumbodinosaurs;

import com.google.gson.Gson;
import com.jumbodinosaurs.objects.RuntimeArguments;
import com.jumbodinosaurs.util.DataController;

import java.io.File;
import java.util.Scanner;

/* @Author Caleb
 * @Date March 15, 2019
 *
 *  Version .6
 *  This is a Work in progress program that's goal is to server popular media types to people over the internet.
 *  Currently the Server can server HTTP requests for POST and GET. POST is limited to operators design and GET
 *  allows any file in the Shared Directory to be requested. Currently not just any type of file can be served from
 *  the Shared Folder but supported types are zip, png, html, ico, js, css, and just about any plain txt media.
 *  In the future I hope to add support for other types of media.
 *
 *  I've been working hard on allowing POST and currently have a system to allow users to authenticate and post things.
 *  In the future I hope to add more post objects such as comments and have user specific request similar to getUserInfo.
 *  Most POST commands are tied/developed for my website front end and are probably not conventional.
 *
 *  I've changed the way that arguments are given to the program. You now just give it a path to a file. This file should be a
 *  json object representing RuntimeArguments. JSON is great for this as it allows you to skip out on arguments you don't have/use.
 *  When given no arguments the program will default if told too. Then will use a default Server Control Constructor.
 *
 *  Example argument -p "W:/Java/arguments.json"
 *
 *  I've changed the file structure and keep most connection Classes in netty and object class like Session in objects.
 *  More utility based classes are in util.
 *
 *  The domain renewal for a dynamic I.P. is no longer tied down to an .sh script.
 *
 *  Versions before this one have different storage design for logs so make a back up if you actually use my program.
 *
 *
 *
 *
 * Old Message
 *  *@Date January 28, 2019
 * This Program is a Work In Progress being designed to properly serve HTML and most commonly used Photo File types
 *  to your most commonly used web-browsers. As of the date above it can server HTML, PNG, and ICO with plans to
 *  support other Photo File types. This Program or "Server" allows you to hook your domain name up with a dynamic IP
 *  using Google's Dynamic IP API. The Server also has a few safe Guards for malicious requests from clients. It
 *  restricts all GET requests to a single Directory and only allows the files within that Directory to be requested.
 *  The Server Currently does not support Post requests.
 *
 *  Google API with arguments
 *  Arguments Should be given in this order
 *     Username Password Domain
 *  If multiple domain continue this pattern
 *
 *  https://username:password@domains.google.com/nic/update?hostname=subdomain.yourdomain.com
 *
 *
 *
 */
public class Main
{
    private static ServerControl controler;
    
    public static void main(String[] args)
    {
        Scanner userInput = new Scanner(System.in);
        if(args.length > 0)
        {
            for(int i = 0; i < args.length; i++)
            {
                if(args[i].equals("-p"))
                {
                    if(i + 1 < args.length)
                    {
                        String path = args[i + 1];
                        File fileToRead = new File(path);
                        String contents = DataController.getFileContents(fileToRead);
                        RuntimeArguments arguments = new Gson().fromJson(contents, RuntimeArguments.class);
                        controler = new ServerControl(arguments);
                    }
                    else
                    {
                        System.out.println("Unrecognized Argument(s)");
                        System.exit(0);
                    }
                }
            }
        }
        else
        {
            System.out.println("No Arguments Given.\n Continue with default Server Controller? (y/n)");
            String response = userInput.next();
            if(response.toLowerCase().contains("y") || response.toLowerCase().contains("yes"))
            {
                controler = new ServerControl();
            }
            else
            {
                System.exit(0);
            }
        }
        
    }
}
