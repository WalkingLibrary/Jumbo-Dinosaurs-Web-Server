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

import java.time.LocalDateTime;
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
                    String startTime = LocalDateTime.now().toString();
        
                    HTTPResponseGenerator httpResponseGenerator = new HTTPResponseGenerator(msg);
                    response = httpResponseGenerator.generateResponse(message);
        
        
                    if(message.getMethod() == Method.POST)
                    {
                        System.out.println("Start Time: " + startTime);
                        System.out.println("End Time: " + LocalDateTime.now());
                    }
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
            LogManager.consoleLogger.error("Uncaught Exception in HTTP Handler: " + e.getMessage(), e);
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