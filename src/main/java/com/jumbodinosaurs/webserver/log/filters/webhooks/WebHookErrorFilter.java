package com.jumbodinosaurs.webserver.log.filters.webhooks;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;
import com.jumbodinosaurs.devlib.discord.DiscordWebHookAPIMessage;
import com.jumbodinosaurs.devlib.log.filters.webhook.objects.WebHookSubscriber;
import com.jumbodinosaurs.devlib.util.WebUtil;
import com.jumbodinosaurs.webserver.util.OptionUtil;
import com.jumbodinosaurs.webserver.util.ServerUtil;

import java.io.IOException;

public class WebHookErrorFilter extends WebHookSubscriber
{
    @Override
    public FilterReply getFilterReply(ILoggingEvent event)
    {
        if(event.getLevel().toInt() == Level.ERROR_INT)
        {
            String avatarUrl = "https://cdn.discordapp.com/attachments/689414999395139658/833506775604920340/doom-guy-59c95c9a5a880.png";
            String username = "Web Server \"" + ServerUtil.host + "\"";
            String error = event.getMessage();
            
            DiscordWebHookAPIMessage message = new DiscordWebHookAPIMessage(username, avatarUrl, error);
            String webHook = OptionUtil.getWebHook();
            if(!webHook.equals(""))
            {
                try
                {
                    WebUtil.sendMessageToWebHook(webHook, message);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return FilterReply.ACCEPT;
    }
}
