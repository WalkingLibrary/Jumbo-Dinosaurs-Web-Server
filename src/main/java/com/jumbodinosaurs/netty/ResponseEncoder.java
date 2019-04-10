package com.jumbodinosaurs.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ResponseEncoder extends MessageToMessageEncoder<PipelineResponse>
{
    @Override
    protected void encode(ChannelHandlerContext context,
                          PipelineResponse response,
                          List<Object> out) throws Exception
    {
        ByteBuf buffer = context.alloc().buffer();
        buffer.writeBytes(response.getMessage().getBytes());
        if(response.getBytesOut() != null)
        {
            buffer.writeBytes(response.getBytesOut());
        }
        out.add(buffer);
    }
}
