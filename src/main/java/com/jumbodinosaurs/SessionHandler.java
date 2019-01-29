package com.jumbodinosaurs;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SessionHandler extends SimpleChannelInboundHandler<String>
{

    public SessionHandler()
    {
    }


    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext)
    {

    }


    @Override
    public void channelRead0(ChannelHandlerContext context, String message)
    {

        try
        {
            Channel channel = context.channel();
            Session session = new Session(channel, message);
            HTTPRequest request = new HTTPRequest(session.getMessage());
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

            if (request.isPictureRequest())
            {
                FastResponse response = new FastResponse(request.getMessageToSend(), request.getPictureContents());
                context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
            else
            {
                FastResponse response = new FastResponse(request.getMessageToSend(), null);
                context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }


            session.setMessageSent(request.getMessageToSend());
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
}
