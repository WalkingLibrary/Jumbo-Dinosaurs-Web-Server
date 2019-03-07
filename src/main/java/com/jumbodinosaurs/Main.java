package com.jumbodinosaurs;

import com.google.gson.Gson;

import java.io.File;
import java.util.Scanner;

/* @Author WalkingLibrary
 * @Date January 28, 2019
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
 *  If multiple domains continue this pattern
 *
 *  https://username:password@domains.google.com/nic/update?hostname=subdomain.yourdomain.com
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
            if (response.toLowerCase().contains("y") || response.toLowerCase().contains("yes"))
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
