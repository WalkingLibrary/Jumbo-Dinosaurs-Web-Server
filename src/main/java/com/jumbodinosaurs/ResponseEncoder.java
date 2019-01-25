package com.jumbodinosaurs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ResponseEncoder extends MessageToMessageEncoder<String>
{
    @Override
    protected void encode(ChannelHandlerContext context, String msg, List<Object> out) throws Exception
    {
        ByteBuf buffer = context.alloc().buffer();
        for (char character: msg.toCharArray())
        {
            buffer.writeByte((byte)character);
        }

        out.add(buffer);
    }
}
