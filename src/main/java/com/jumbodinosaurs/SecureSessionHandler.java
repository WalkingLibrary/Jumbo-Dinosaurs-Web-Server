package com.jumbodinosaurs;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.net.ssl.SSLHandshakeException;

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
            HTTPSRequest request = new HTTPSRequest(session.getMessage(), session.getWho());
            if (request.isHTTP())
            {
                request.generateMessage();
            }
            else
            {
                request.setMessage501();
            }

            //Send Message
            OperatorConsole.printMessageFiltered("Message Sent to Client: \n" + request.getMessageToSend(),true,false);

            if (request.hasByteArray())
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
                session.setMessageSent("POST, Had 200 Code:" + request.messageSentContained200Code());
                session.setMessage(request.getCensoredMessageFromClient());
                //Would be kinda point less to hash a password if we saved it over in logs.json :P
            }

            OperatorConsole.printMessageFiltered("Adding Session to Logger",true,false);
            DataController.log(session);
            OperatorConsole.printMessageFiltered("Session Complete",true,false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Sending Message",false,true);

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause)
    {

        if(!cause.getMessage().contains("no cipher suites in common") ||
                !cause.getMessage().contains("not an SSL/TLS record:") ||
                !cause.getMessage().contains("Client requested protocol SSLv3 not enabled or not supported"))
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
