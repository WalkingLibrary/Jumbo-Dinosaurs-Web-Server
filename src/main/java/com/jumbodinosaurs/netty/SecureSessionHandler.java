package com.jumbodinosaurs.netty;

import com.jumbodinosaurs.objects.Session;
import com.jumbodinosaurs.util.DataController;
import com.jumbodinosaurs.util.OperatorConsole;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SecureSessionHandler extends SimpleChannelInboundHandler<String>
{
    
    
    @Override
    public void channelRead(ChannelHandlerContext context, Object msg)
    {
        String message = (String) msg;
        try
        {
            Channel channel = context.channel();
            Session session = new Session(channel, message);
            boolean allowconection = false;
            if(OperatorConsole.whitelist)
            {
                if(OperatorConsole.whitelistedIps != null)
                {
                    for(String str : OperatorConsole.whitelistedIps)
                    {
                        if(session.getWho().contains(str))
                        {
                            allowconection = true;
                            break;
                        }
                    }
                }
            }
            else
            {
                allowconection = true;
            }
            if(allowconection)
            {
                HTTPSRequest request = new HTTPSRequest(session.getMessage(), session.getWho());
                if(request.isHTTP())
                {
                    request.generateMessage();
                }
                else
                {
                    request.setMessage501();
                }
                
                //Send Message
                
                if(request.hasByteArray())
                {
                    FastResponse response = new FastResponse(request.getMessageToSend(), request.getByteArrayToSend());
                    context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                }
                else
                {
                    FastResponse response = new FastResponse(request.getMessageToSend(), null);
                    context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                }
                
                session.setMessageSent(request.getMessageToSend());
                
                if(!request.logMessageFromClient())
                {
                    session.setMessageSent(request.getCensoredMessageSentToClient());
                    session.setMessage(request.getCensoredMessageFromClient());
                    //Would be kinda point less to hash a password if we saved it over in logs.json :P
                }
                OperatorConsole.printMessageFiltered(session.toString(), true, false);
                DataController.log(session);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Sending Message", false, true);
            
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause)
    {
        
        if(!(cause.getMessage().contains("no cipher suites in common") ||
                   cause.getMessage().contains("not an SSL/TLS") ||
                   cause.getMessage().contains("Client requested protocol SSLv3 not enabled or not supported")))
        {
            System.out.println("Exception");
            OperatorConsole.printMessageFiltered(cause.getMessage(), false, true);
        }
        context.close();
    }
    
    
    @Override
    public void channelRead0(ChannelHandlerContext context, String message)
    {
        context.fireChannelRead(message);
    }
}
