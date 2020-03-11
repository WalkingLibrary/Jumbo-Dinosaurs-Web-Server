package com.jumbodinosaurs.netty.handler.http;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.netty.handler.IHandlerHolder;
import com.jumbodinosaurs.netty.handler.http.util.HTTPRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponseGenerator;
import com.jumbodinosaurs.util.OptionUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class HTTPHandler<E> extends MessageToMessageDecoder<HTTPRequest> implements IHandlerHolder
{
    
    @Override
    protected void decode(ChannelHandlerContext ctx, HTTPRequest msg, List<Object> out)
    {
        try
        {
            //TODO add Server Specific White List Decoder
            if(OptionUtil.isWhiteListOn())
            {
                if(!OptionUtil.getWhiteList().contains(msg.getIp()))
                {
                    return;
                }
            }
            
            
            HTTPResponse response = new HTTPResponse();
            
            
            if(!msg.isEncryptedConnection() && OptionUtil.shouldUpgradeInsecureConnections())
            {
                response.setMessageToRedirectToHTTPS(msg);
            }
            else
            {
                response = HTTPResponseGenerator.generateResponse(msg);
            }
            
            //Would be kinda point less to hash a password if we saved it over in logs.json :P
            response.setMessageReceived(msg.getCensoredMessage());
            
            //Send Response
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            OperatorConsole.printMessageFiltered(msg.toString(), true, false);
            
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Uncaught Exception in HTTP Handler", false, true);
        }
        
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause)
    {
        context.close();
    }
    
    
    @Override
    public MessageToMessageDecoder<HTTPHandler> getInstance()
    {
        return new HTTPHandler();
    }
    
    
}
