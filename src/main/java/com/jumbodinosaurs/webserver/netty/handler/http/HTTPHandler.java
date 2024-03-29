package com.jumbodinosaurs.webserver.netty.handler.http;

import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.netty.handler.IHandlerHolder;
import com.jumbodinosaurs.webserver.log.Session;
import com.jumbodinosaurs.webserver.log.SessionLogManager;
import com.jumbodinosaurs.webserver.netty.handler.http.exceptions.MalformedHTTPMessage;
import com.jumbodinosaurs.webserver.netty.handler.http.util.*;
import com.jumbodinosaurs.webserver.util.OptionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class HTTPHandler extends MessageToMessageDecoder<Session> implements IHandlerHolder
{
    
    
    
    @Override
    protected void decode(ChannelHandlerContext ctx, Session msg, List<Object> out)
    {
        try
        {
            //TODO add Server Specific White List Decoder
            if(OptionUtil.isWhiteListOn())
            {
                LogManager.consoleLogger.debug("Whitelist IP: " + msg.getWho());
                String compareWho = msg.getWho().replace("/", "");
                if(!OptionUtil.getWhiteList().contains(compareWho))
                {
                    LogManager.consoleLogger.debug("Blocked: " + msg.getWho());
                    return;
                }
            }
            
            try
            {
                HTTPMessage message = HTTPParser.parseResponse(msg);
    
                HTTPResponse response = new HTTPResponse();
    
    
                if(!msg.isSecureConnection() && OptionUtil.shouldUpgradeInsecureConnections())
                {
                    response.setMessageToRedirectToHTTPS(message);
                }
                else
                {
                    HTTPResponseGenerator httpResponseGenerator = new HTTPResponseGenerator(msg);
                    response = httpResponseGenerator.generateResponse(message);
        
                }
                response.setClosingHeaders();
    
                //Would be kinda point less to hash a password if we saved it over in logs.json :P
                msg.setMessage(message.getCensoredMessage());
                msg.setMessageSent(response.getMessageToLog());
    
                //Send Response
                ctx.writeAndFlush(response);
    
                /*DEBUG MESSAGES*/
                if(OptionUtil.isInDebugMode())
                {
                    if(message.getMethod().equals(Method.POST))
                    {
    
                        if(message.getPostRequest() != null)
                        {
                            //System.out.println(message.getPostRequest().toString());
                        }
                    }
                    System.out.println(msg.toString());
                    System.out.println(response.toString());
                }
    
                SessionLogManager.log(msg);
            }
            catch(MalformedHTTPMessage e)
            {
                LogManager.consoleLogger.debug(e.getMessage(), e);
            }
            
        }
        catch(Exception e)
        {
            String stackTrace = "";
            StackTraceElement[] elements = e.getStackTrace();
            int linesToInclude = 6;
            for(int i = 0; i < linesToInclude && i < elements.length; i++)
            {
                StackTraceElement element = elements[i];
                stackTrace += element.toString() + "\n";
            }
            LogManager.consoleLogger.error("Uncaught Exception in HTTP Handler: ```" + stackTrace + "```");
            e.printStackTrace();
        }
        
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause)
    {
        context.close();
    }
    
    
    @Override
    public MessageToMessageDecoder<Session> getInstance()
    {
        return new HTTPHandler();
    }
    
    
}