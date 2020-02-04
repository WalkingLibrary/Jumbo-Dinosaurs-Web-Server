package com.jumbodinosaurs.util;

import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.devlib.util.ResourceLoaderUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class LinuxUtil
{
    public static File scriptsDir = GeneralUtil.checkFor(ServerUtil.serverDataDir, "Linux Scripts");
    
    
    public static void unpackScripts()
    {
        ResourceLoaderUtil resourceLoaderUtil = new ResourceLoaderUtil();
        List<File> linuxScripts = resourceLoaderUtil.getFiles("scripts");
        for(File file: linuxScripts)
        {
            File unpackedFile = GeneralUtil.checkFor(scriptsDir, file.getName());
            String scriptContents = GeneralUtil.scanFileContents(file);
            GeneralUtil.writeContents(unpackedFile, scriptContents,false);
        }
        
    }
    
    public static String execute(String command, File executionDir)
    {
        try
        {
            Process process = Runtime.getRuntime().exec(command, null, executionDir);
            process.waitFor();
            Scanner proccessScanner = new Scanner(process.getInputStream());
            String output = "";
            while(proccessScanner.hasNext())
            {
                output += proccessScanner.nextLine();
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

