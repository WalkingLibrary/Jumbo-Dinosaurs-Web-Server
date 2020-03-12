package com.jumbodinosaurs.util;

import com.jumbodinosaurs.devlib.reflection.ResourceLoaderUtil;
import com.jumbodinosaurs.devlib.util.GeneralUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LinuxUtil
{
    public static File scriptsDir = GeneralUtil.checkFor(ServerUtil.serverDataDir, "Linux Scripts");
    public static File unpackedScriptsDir = GeneralUtil.checkFor(scriptsDir, "scripts");
    
    
    public static void unpackScripts()
    {
        ResourceLoaderUtil resourceLoaderUtil = new ResourceLoaderUtil();
        try
        {
            /* Process for Preping Scripts for Running
             * Unpack them from resources
             * run dos2unix command on all of them
             * profit
             *
             */
            
            //Unpack them from resources
            ArrayList<String> scripts = resourceLoaderUtil.listResources("scripts");
            for(String scriptName: scripts)
            {
                System.out.println("Unpacking: " + scriptName);
                String scriptContents = resourceLoaderUtil.scanResource(scriptName);
                File unpackedFile = GeneralUtil.checkForLocalPath(scriptsDir, scriptName);
                GeneralUtil.writeContents(unpackedFile, scriptContents, false);
            }
            
            //run dos2unix command on all of them
            for(File file: unpackedScriptsDir.listFiles())
            {
                String output = execute("sudo dos2unix " + file.getName(),null, unpackedScriptsDir);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
    }
    
    public static String execute(String command, ArrayList<String> arguments, File executionDir)
    {
        if(arguments != null && arguments.size() != 0)
        {
            for(String argument: arguments)
            {
                command += " " + argument;
            }
        }
        
        try
        {
            System.out.println("Executing Command: " + command);
            Process process = Runtime.getRuntime().exec(command, null, executionDir);
            process.waitFor();
            Scanner processScanner = new Scanner(process.getInputStream());
            String output = "";
            while(processScanner.hasNext())
            {
                output += processScanner.nextLine();
            }
            return output;
            
        }
        catch(IOException e)
        {
            return e.getMessage();
        }
        catch(InterruptedException e)
        {
            return e.getMessage();
        }
        
    }
    
}
