package com.jumbodinosaurs.commands.general;

import com.jumbodinosaurs.auth.server.captcha.CaptchaKey;
import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.util.OptionIdentifier;
import com.jumbodinosaurs.util.OptionUtil;

public class SetCaptchaKey extends Command
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter Site Key:");
        String siteKey = OperatorConsole.getEnsuredAnswer();
        System.out.println("Enter Secret Key:");
        String secretKey = OperatorConsole.getEnsuredAnswer();
        CaptchaKey newCaptchaKey = new CaptchaKey(siteKey, secretKey);
        Option<CaptchaKey> captchaKeyOption = new Option<CaptchaKey>(newCaptchaKey,
                                                                     OptionIdentifier.captchaKey.getIdentifier());
        OptionUtil.setOption(captchaKeyOption);
        return new MessageResponse("Captcha Key has Been Updated");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Allows the user to enter a captcha key.";
    }
}
