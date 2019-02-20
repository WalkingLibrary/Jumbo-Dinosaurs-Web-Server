package com.jumbodinosaurs;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SessionHandler extends SimpleChannelInboundHandler<String>
{


    @Override
    public void channelRead(ChannelHandlerContext context, Object msg)
    {
        String message = (String) msg;
        try
        {
            Channel channel = context.channel();
            Session session = new Session(channel, message);
            HTTPRequest request = new HTTPRequest(session.getMessage());
            if (request.isHTTP())
            {

                if(SecureSessionHandlerInitializer.running)
                {
                    request.setMessage301RedirectHTTPS();
                }
                else
                {
                    request.generateMessage();
                }
            }
            else
            {
                request.setMessage501();
            }

            //Send Message
            OperatorConsole.printMessageFiltered("Message Sent to Client: \n" + request.getMessageToSend(), true, false);

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
            OperatorConsole.printMessageFiltered("Adding Session to Logger", true, false);
            DataController.log(session);
            OperatorConsole.printMessageFiltered("Session Complete", true, false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Sending Message", false, true);

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause)
    {
        System.out.println("Netty Exception Caught will Dispatch Flying Monkeys To Fix that Dave");
        context.close();
    }


    @Override
    public void channelRead0(ChannelHandlerContext context, String message)
    {
        context.fireChannelRead(message);
    }
}
