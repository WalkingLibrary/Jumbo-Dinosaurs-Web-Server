package com.jumbodinosaurs.util;

import com.jumbodinosaurs.ServerController;
import com.jumbodinosaurs.devlib.reflection.ResourceLoaderUtil;
import com.jumbodinosaurs.devlib.util.GeneralUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LinuxUtil
{
    public static File scriptsDir = GeneralUtil.checkFor(ServerUtil.serverDataDir, "Linux Scripts");
    public static File unpackedScriptsDir = GeneralUtil.checkFor(scriptsDir, "scripts");
    
    public static void unpackScripts()
    {
        if(!isLinux())
        {
            ServerController.consoleLogger.info("Operating System is Not Linux -> Skipping Script unpacking");
            return;
        }
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
                ServerController.consoleLogger.info("Unpacking: " + scriptName);
                String scriptContents = resourceLoaderUtil.scanResource(scriptName);
                File unpackedFile = GeneralUtil.checkForLocalPath(scriptsDir, scriptName);
                GeneralUtil.writeContents(unpackedFile, scriptContents, false);
            }
            
            //run dos2unix command on all of them
            for(File file: unpackedScriptsDir.listFiles())
            {
                String output = GeneralUtil.execute("sudo dos2unix " + file.getName(),null, unpackedScriptsDir);
                ServerController.consoleLogger.debug(output);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
    }
    
    public static boolean isLinux()
    {
        String operatingSystem = System.getProperty("os.name");
        if(operatingSystem.toLowerCase().contains("windows"))
        {
            return false;
        }
        return true;
    }
    
    
}

