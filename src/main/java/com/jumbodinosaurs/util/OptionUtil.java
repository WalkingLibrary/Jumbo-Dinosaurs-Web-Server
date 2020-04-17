package com.jumbodinosaurs.util;

import com.jumbodinosaurs.auth.server.captcha.CaptchaKey;
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
    
    public static String getUserDataBaseName()
    {
        return optionsManager.getOption(OptionIdentifier.userDataBaseName.getIdentifier(), "userdatabase").getOption();
    }
    
    public static CaptchaKey getCaptchaKey()
    {
        return optionsManager.getOption(OptionIdentifier.captchaKey.getIdentifier(), new CaptchaKey()).getOption();
    }
    
    
    public static <E> void setOption(Option<E> option)
    {
        optionsManager.setOption(option);
    }
}
