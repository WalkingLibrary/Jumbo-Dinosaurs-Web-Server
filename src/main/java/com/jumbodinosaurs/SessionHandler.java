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
            OperatorConsole.printMessageFiltered("Message Sent to Client: \n" + request.getMessageToSend(),true,false);

            if (request.isPictureRequest())
            {
                context.writeAndFlush(request.getMessageToSend() + request.getPictureContents()).addListener(ChannelFutureListener.CLOSE);
            }
            else
            {
                context.writeAndFlush(request.getMessageToSend()).addListener(ChannelFutureListener.CLOSE);
            }


            session.setMessageSent(request.getMessageToSend());
            OperatorConsole.printMessageFiltered("Adding Session to Logger",true,false);
            this.dataIO.log(session);
            OperatorConsole.printMessageFiltered("Session Complete",true,false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Sending Message",false,true);

        }

    }
}
