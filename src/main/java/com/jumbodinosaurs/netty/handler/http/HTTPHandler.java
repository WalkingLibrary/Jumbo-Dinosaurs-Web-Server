package com.jumbodinosaurs.netty.handler.http;

import com.jumbodinosaurs.log.LogManager;
import com.jumbodinosaurs.log.Session;
import com.jumbodinosaurs.netty.handler.IHandlerHolder;
import com.jumbodinosaurs.netty.handler.http.exceptions.MalformedHTTPMessage;
import com.jumbodinosaurs.netty.handler.http.util.HTTPMessage;
import com.jumbodinosaurs.netty.handler.http.util.HTTPParser;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponseGenerator;
import com.jumbodinosaurs.util.OptionUtil;
import io.netty.channel.ChannelFutureListener;
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
                if(!OptionUtil.getWhiteList().contains(msg.getWho()))
                {
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
                    response = HTTPResponseGenerator.generateResponse(message);
                }
                
                //Would be kinda point less to hash a password if we saved it over in logs.json :P
                msg.setMessage(message.getCensoredMessage());
                msg.setMessageSent(response.getMessageToLog());
                
                //Send Response
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                if(OptionUtil.isInDebugMode())
                {
                    System.out.println(msg.toString());
                }
                LogManager.log(msg);
            }
            catch(MalformedHTTPMessage e)
            {
                LogManager.consoleLogger.error(e.getMessage(), e);
            }
            
        }
        catch(Exception e)
        {
            LogManager.consoleLogger.error("Uncaught Exception in HTTP Handler", e);
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