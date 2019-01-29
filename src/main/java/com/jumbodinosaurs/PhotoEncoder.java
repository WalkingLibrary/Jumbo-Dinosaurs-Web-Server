package com.jumbodinosaurs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class PhotoEncoder extends MessageToMessageEncoder<byte[]>
{
    @Override
    protected void encode(ChannelHandlerContext context, byte[] msg, List<Object> out) throws Exception
    {
        ByteBuf buffer = context.alloc().buffer();
        for (byte byteToSend: msg)
        {
            buffer.writeByte(byteToSend);
        }

        out.add(buffer);
    }
}
