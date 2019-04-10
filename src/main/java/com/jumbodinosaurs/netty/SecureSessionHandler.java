package com.jumbodinosaurs.netty;

import com.jumbodinosaurs.ServerControl;
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
    public void channelRead(ChannelHandlerContext context,
                            Object msg)
    {
        String message = (String) msg;
        try
        {
            Channel channel = context.channel();
            Session session = new Session(channel, message);
            boolean allowConnection = false;
            if(OperatorConsole.whitelist)
            {
                if(OperatorConsole.whitelistedIps != null)
                {
                    for(String str : OperatorConsole.whitelistedIps)
                    {
                        if(session.getWho().contains(str))
                        {
                            allowConnection = true;
                            break;
                        }
                    }
                }
            }
            else
            {
                allowConnection = true;
            }
            if(allowConnection)
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
                PipelineResponse response = new PipelineResponse(request.getMessageToSend(),
                                                                 request.getByteArrayToSend());
                context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                //end message send
                
                session.setMessageSent(request.getMessageToSend());
                
                if(!request.leaveMessageTheSame() && !(ServerControl.getArguments() != null && ServerControl.getArguments().isInTestMode()))
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
    public void exceptionCaught(ChannelHandlerContext context,
                                Throwable cause)
    {
        
        if(!(cause.getMessage() != null || cause.getMessage().contains("no cipher suites in common") || cause.getMessage().contains(
                "not an SSL/TLS") || cause.getMessage().contains(
                "Client requested protocol SSLv3 not enabled or not supported") || cause.getMessage().contains(
                "Connection reset by peer")))
        {
            OperatorConsole.printMessageFiltered(cause.getMessage(), false, true);
        }
        context.close();
    }
    
    
    @Override
    public void channelRead0(ChannelHandlerContext context,
                             String message)
    {
        context.fireChannelRead(message);
    }
}
