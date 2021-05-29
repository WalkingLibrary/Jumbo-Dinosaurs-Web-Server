package com.jumbodinosaurs.webserver.commands.general;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.options.Option;
import com.jumbodinosaurs.webserver.util.OptionIdentifier;
import com.jumbodinosaurs.webserver.util.OptionUtil;
import org.slf4j.LoggerFactory;

public class ToggleDebugMode extends Command
{
    public static void toggleConsoleAppenderFilter(boolean debugMode)
    {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger("ConsoleLogger");
        Filter<ILoggingEvent> filter = new Filter<ILoggingEvent>()
        {
            @Override
            public FilterReply decide(ILoggingEvent event)
            {
                if(!debugMode)
                {
                    if((event).getLevel().toInt() < ch.qos.logback.classic.Level.INFO_INT)
                    {
                        return FilterReply.DENY;
                    }
                }
                
                return FilterReply.ACCEPT;
            }
        };
        Appender systemOut = logger.getAppender("STDOUT");
        systemOut.clearAllFilters();
        systemOut.addFilter(filter);
        
    }
    
    @Override
    public MessageResponse getExecutedMessage()
            throws WaveringParametersException
    {
        
        Option<Boolean> debugMode = new Option<Boolean>(!OptionUtil.isInDebugMode(),
                                                        OptionIdentifier.debugMode.getIdentifier());
        OptionUtil.setOption(debugMode);
        String outputMessage = "";
        
        toggleConsoleAppenderFilter(OptionUtil.isInDebugMode());
        
        outputMessage = OptionUtil.isInDebugMode() ?
                        "The server is now in debugMode" + "\n" :
                        "The server is no longer in debugMode" + "\n";
        
        return new MessageResponse(outputMessage);
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Toggles the servers ability to send debug messages";
    }
}
