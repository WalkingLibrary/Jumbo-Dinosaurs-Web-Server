package com.jumbodinosaurs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;


public class SessionHandlerInitializer extends ChannelInitializer<SocketChannel>
{
    private DataController dataIO;

    public SessionHandlerInitializer(DataController dataIO)
    {
        this.dataIO = dataIO;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception
        {
            ChannelPipeline pipeline = channel.pipeline();

            String delimiter = "\r\n\r\n";
            ByteBuf buffer = Unpooled.buffer(delimiter.getBytes().length);
            buffer.writeBytes(delimiter.getBytes());
            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(10000000, buffer));
            pipeline.addLast("decoder", new StringDecoder());
            pipeline.addLast("encoder",new ResponseEncoder());
            pipeline.addLast(new ChunkedWriteHandler());
            pipeline.addLast("handler", new SessionHandler());


        }
}
