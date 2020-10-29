package com.jumbodinosaurs.webserver.netty.initializer;

import com.jumbodinosaurs.devlib.netty.ConnectionListenerInitializer;
import com.jumbodinosaurs.devlib.netty.handler.IHandlerHolder;
import com.jumbodinosaurs.webserver.netty.pipline.HTTPMessageFramer;
import com.jumbodinosaurs.webserver.netty.pipline.HTTPResponseEncoder;
import com.jumbodinosaurs.webserver.netty.pipline.SessionDecoder;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class DefaultHTTPConnectListenerInitializer extends ConnectionListenerInitializer
{
    public DefaultHTTPConnectListenerInitializer(int port, IHandlerHolder iHandlerHolder)
    {
        super(port, iHandlerHolder);
    }
    
    @Override
    protected void initChannel(SocketChannel channel) throws Exception
    {
        //Line for setting the initial size of ByteBuf
        channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(1000000));
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("readTimeoutHandler", new ReadTimeoutHandler(15));
        pipeline.addLast("framer", new HTTPMessageFramer());
        pipeline.addLast("sessionDecoder", new SessionDecoder());
        pipeline.addLast("encoder", new HTTPResponseEncoder());
        pipeline.addLast("handler", this.handlerHolder.getInstance());
    
    }
}
