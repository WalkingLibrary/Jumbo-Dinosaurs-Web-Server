package com.jumbodinosaurs.webserver.netty.handler.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;

public class WebSocketHandler extends ChannelInboundHandlerAdapter
{
    
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        System.out.println(msg.toString());
        if(msg instanceof WebSocketFrame)
        {
            System.out.println("This is a WebSocket frame");
            System.out.println("Client Channel : " + ctx.channel());
            if(msg instanceof BinaryWebSocketFrame)
            {
                System.out.println("BinaryWebSocketFrame Received : ");
                System.out.println(((BinaryWebSocketFrame) msg).content());
            }
            else if(msg instanceof TextWebSocketFrame)
            {
                System.out.println("TextWebSocketFrame Received : ");
                for(int i = 0; i < 10; i++)
                {
                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ctx.channel()
                       .writeAndFlush(new TextWebSocketFrame("Message recieved : " +
                                                             ((TextWebSocketFrame) msg).text()));
                }
                System.out.println(((TextWebSocketFrame) msg).text());
            }
            else if(msg instanceof PingWebSocketFrame)
            {
                System.out.println("PingWebSocketFrame Received : ");
                System.out.println(((PingWebSocketFrame) msg).content());
            }
            else if(msg instanceof PongWebSocketFrame)
            {
                System.out.println("PongWebSocketFrame Received : ");
                System.out.println(((PongWebSocketFrame) msg).content());
            }
            else if(msg instanceof CloseWebSocketFrame)
            {
                System.out.println("CloseWebSocketFrame Received : ");
                System.out.println("ReasonText :" + ((CloseWebSocketFrame) msg).reasonText());
                System.out.println("StatusCode : " + ((CloseWebSocketFrame) msg).statusCode());
            }
            else
            {
                System.out.println("Unsupported WebSocketFrame");
            }
        }
    }
}
