package com.jumbodinosaurs.netty.initializer;

import com.jumbodinosaurs.netty.handler.IHandlerHolder;
import com.jumbodinosaurs.netty.pipline.HTTPRequestConverter;
import com.jumbodinosaurs.netty.pipline.HTTPResponseEncoder;
import com.jumbodinosaurs.netty.pipline.SessionLogger;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class DefaultHTTPConnectListenerInitializer extends ConnectionListenerInitializer
{
    public DefaultHTTPConnectListenerInitializer(int port, IHandlerHolder iHandlerHolder)
    {
        super(port, iHandlerHolder);
    }
    
    @Override
    protected void initChannel(SocketChannel channel) throws Exception
    {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
        pipeline.addLast("encoder", new HTTPResponseEncoder());
        pipeline.addLast("streamer", new ChunkedWriteHandler());
        pipeline.addLast("sessionlogger", new SessionLogger());
        pipeline.addLast("converter", new HTTPRequestConverter());
        pipeline.addLast("handler", this.handlerHolder.getInstance());
        
    }
}
