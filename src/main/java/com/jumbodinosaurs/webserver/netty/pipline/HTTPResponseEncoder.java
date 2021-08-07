package com.jumbodinosaurs.webserver.netty.pipline;

import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class HTTPResponseEncoder extends MessageToMessageEncoder<HTTPResponse>
{
    @Override
    protected void encode(ChannelHandlerContext context, HTTPResponse response, List<Object> out)
            throws Exception
    {
        //Send Response
        //Make a Byte Buffer with the size of the packet
        //Add The bytes to buffer
        //Write the buffer to the pipeline
        
        
        //Make a Byte Buffer with the size of the packet
        int extraBytes = 0;
        if(response.getBytesOut() != null)
        {
            extraBytes = response.getBytesOut().length;
        }
        
        ByteBuf byteBuf = Unpooled.buffer(response.getMessage().getBytes().length + extraBytes);
        
        
        //Add The bytes to buffer
        byteBuf.writeBytes(response.getMessage().getBytes());
        if(response.getBytesOut() != null)
        {
            byteBuf.writeBytes(response.getBytesOut());
        }
    
        //Write the buffer to the pipeline
        ChannelFuture promise = context.writeAndFlush(byteBuf);
        //Add Futures to close the connection when the bytes are sent
        promise.addListener(new CompletionFuture(response.shouldKeepConnectionAlive()));
        promise.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    
        if(response.shouldKeepConnectionAlive())
        {
            context.pipeline().remove(this);
        }
    }
}

