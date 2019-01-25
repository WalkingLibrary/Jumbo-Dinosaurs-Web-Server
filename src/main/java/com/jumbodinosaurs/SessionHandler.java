package com.jumbodinosaurs;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SessionHandler extends SimpleChannelInboundHandler<String>
{
    private static DataController dataIO;

    public SessionHandler(DataController dataIO)
    {
        this.dataIO = dataIO;
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
                request.generateMessage(this.dataIO);
            }
            else
            {
                request.setMessage501(this.dataIO);
            }

        //Send Message
            System.out.println("Message Sent to Client: \n" + request.getMessageToSend());

            if (request.isPictureRequest())
            {
                context.writeAndFlush(request.getMessageToSend() + request.getPictureContents()).addListener(ChannelFutureListener.CLOSE);
            }
            else
            {
                context.writeAndFlush(request.getMessageToSend()).addListener(ChannelFutureListener.CLOSE);
            }
            session.setMessageSent(request.getMessageToSend());
            System.out.println("Adding Session to Logger");
            this.dataIO.log(session);
            System.out.println("Session Complete");
        }
        catch (Exception e)
        {
            System.out.println("Error Sending Message");
            e.printStackTrace();
            System.out.println(e.getCause());
        }

    }
}
