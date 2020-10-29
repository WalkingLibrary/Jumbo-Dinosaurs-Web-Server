package com.jumbodinosaurs.webserver.util;

import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.devlib.options.OptionsManager;
import com.jumbodinosaurs.devlib.util.GeneralUtil;

import java.io.File;
import java.util.ArrayList;

public class OptionUtil
{
    private static File optionsJson = GeneralUtil.checkFor(ServerUtil.serverDataDir, "options.json");
    public static OptionsManager optionsManager = new OptionsManager(optionsJson);
    
    
    public static String getDefaultEmail()
    {
        return optionsManager.getOption(OptionIdentifier.email.getIdentifier(), "").getOption();
    }
    
    public static boolean isInDebugMode()
    {
        return optionsManager.getOption(OptionIdentifier.debugMode.getIdentifier(), false).getOption();
    }
    
    public static boolean allowPost()
    {
        return optionsManager.getOption(OptionIdentifier.allowPost.getIdentifier(), false).getOption();
    }
    
    public static boolean isWhiteListOn()
    {
        return optionsManager.getOption(OptionIdentifier.isWhiteListOn.getIdentifier(), false).getOption();
    }
    
    public static ArrayList<String> getWhiteList()
    {
        return optionsManager.getOption(OptionIdentifier.whiteList.getIdentifier(), new ArrayList<String>())
                             .getOption();
    }
    
    public static boolean shouldUpgradeInsecureConnections()
    {
        return optionsManager.getOption(OptionIdentifier.shouldUpgradeInsecureConnections.getIdentifier(), false)
                             .getOption();
    }
    
    public static String getServersDataBaseName()
    {
        return optionsManager.getOption(OptionIdentifier.userDataBaseName.getIdentifier(), "serversDataBaseName")
                             .getOption();
    }
    
    
    public static String getGETDirPath()
    {
        return optionsManager.getOption(OptionIdentifier.getDirPath.getIdentifier(),
                                        ServerUtil.getDirectory.getAbsolutePath()).getOption();
    }
    
    
    public static <E> void setOption(Option<E> option)
    {
        optionsManager.setOption(option);
    }
}
