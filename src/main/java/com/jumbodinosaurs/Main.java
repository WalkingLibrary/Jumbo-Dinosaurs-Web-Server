package com.jumbodinosaurs;

import com.google.gson.Gson;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.objects.RuntimeArguments;

import java.io.File;
import java.util.Scanner;


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
                        String contents = GeneralUtil.scanFileContents(fileToRead);
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
                RuntimeArguments defaultRuntimeArguments = new RuntimeArguments("", true);
                controler = new ServerControl(defaultRuntimeArguments);
            }
            else
            {
                System.exit(0);
            }
        }
        
    }
}
