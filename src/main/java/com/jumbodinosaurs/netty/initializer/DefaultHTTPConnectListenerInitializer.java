package com.jumbodinosaurs.netty.initializer;

import com.jumbodinosaurs.netty.handler.IHandlerHolder;
import com.jumbodinosaurs.netty.pipline.FullMessageFramer;
import com.jumbodinosaurs.netty.pipline.HTTPResponseEncoder;
import com.jumbodinosaurs.netty.pipline.SessionDecoder;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

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
        //channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(500000));
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("framer", new FullMessageFramer());
        pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast("sessionDecoder", new SessionDecoder());
        pipeline.addLast("encoder", new HTTPResponseEncoder());
        pipeline.addLast("streamer", new ChunkedWriteHandler());
        pipeline.addLast("handler", this.handlerHolder.getInstance());
    
    }
}
