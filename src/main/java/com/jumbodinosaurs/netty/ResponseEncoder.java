package com.jumbodinosaurs.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ResponseEncoder extends MessageToMessageEncoder<FastResponse>
{
    @Override
    protected void encode(ChannelHandlerContext context, FastResponse response, List<Object> out) throws Exception
    {
        ByteBuf buffer = context.alloc().buffer();
        for (char charToSend: response.getMessage().toCharArray())
        {
            buffer.writeByte((byte)charToSend);
        }

        if(response.getPhotobytes() != null)
        {
            for (byte byteToSend : response.getPhotobytes())
            {
                buffer.writeByte(byteToSend);
            }
        }
        out.add(buffer);
    }
}
