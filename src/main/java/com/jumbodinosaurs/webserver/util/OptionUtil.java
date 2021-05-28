package com.jumbodinosaurs.webserver.util;

import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.devlib.options.OptionsManager;
import com.jumbodinosaurs.devlib.util.GeneralUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class OptionUtil
{
    private static File optionsJson = GeneralUtil.checkFor(ServerUtil.serverDataDir, "options.json");
    private static OptionsManager optionsManager = new OptionsManager(optionsJson);
    
    
    public static String getDefaultEmail()
    {
        return getOptionsManager().getOption(OptionIdentifier.email.getIdentifier(), "").getOption();
    }
    
    public static boolean isInDebugMode()
    {
        return getOptionsManager().getOption(OptionIdentifier.debugMode.getIdentifier(), false).getOption();
    }
    
    public static boolean allowPost()
    {
        return getOptionsManager().getOption(OptionIdentifier.allowPost.getIdentifier(), false).getOption();
    }
    
    public static boolean isWhiteListOn()
    {
        return getOptionsManager().getOption(OptionIdentifier.isWhiteListOn.getIdentifier(), false).getOption();
    }
    
    public static ArrayList<String> getWhiteList()
    {
        return getOptionsManager().getOption(OptionIdentifier.whiteList.getIdentifier(), new ArrayList<String>())
                                  .getOption();
    }
    
    public static boolean shouldUpgradeInsecureConnections()
    {
        return getOptionsManager().getOption(OptionIdentifier.shouldUpgradeInsecureConnections.getIdentifier(), false)
                                  .getOption();
    }
    
    public static String getServersDataBaseName()
    {
        return getOptionsManager().getOption(OptionIdentifier.userDataBaseName.getIdentifier(), "serversDataBaseName")
                                  .getOption();
    }
    
    
    public static CopyOnWriteArrayList<String> getGETDirPaths()
    {
        CopyOnWriteArrayList<String> getDirPaths = new CopyOnWriteArrayList<String>();
        getDirPaths = getOptionsManager().getOption(OptionIdentifier.getDirPath.getIdentifier(), getDirPaths)
                                         .getOption();
        //We add the Main Get Dir
        getDirPaths.add(ServerUtil.getDirectory.getAbsolutePath());
        return getDirPaths;
    }
    
    public static ArrayList<String> getAllowedHiddenDirs()
    {
        ArrayList<String> hiddenDirs = new ArrayList<String>();
        hiddenDirs.add(".well-known");
        hiddenDirs = getOptionsManager().getOption(OptionIdentifier.hiddenDirs.getIdentifier(), hiddenDirs).getOption();
        return hiddenDirs;
    }
    
    public static String getWebHook()
    {
        return getOptionsManager().getOption(OptionIdentifier.webhook.getIdentifier(), "").getOption();
    }
    
    public static <E> void setOption(Option<E> option)
    {
        getOptionsManager().setOption(option);
    }
    
    public synchronized static OptionsManager getOptionsManager()
    {
        return optionsManager;
    }
    
    public synchronized static void setOptionsManager(OptionsManager optionsManager)
    {
        OptionUtil.optionsManager = optionsManager;
    }
}
