package com.jumbodinosaurs.netty.initializer;

import com.jumbodinosaurs.netty.handler.IHandlerHolder;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponseEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class DefaultConnectListenerInitializer extends ConnectionListenerInitializer
{
    public DefaultConnectListenerInitializer(int port, IHandlerHolder iHandlerHolder)
    {
        super(port, iHandlerHolder);
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
        pipeline.addLast("encoder", new HTTPResponseEncoder());
        pipeline.addLast("streamer", new ChunkedWriteHandler());
        pipeline.addLast("handlerHolder", this.handlerHolder.getInstance());
        
    }
}
