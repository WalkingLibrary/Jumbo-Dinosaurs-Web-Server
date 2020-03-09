package com.jumbodinosaurs.netty.pipline;

import com.jumbodinosaurs.netty.handler.http.util.HTTPRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

public class HTTPRequestConverter extends MessageToMessageDecoder<FullHttpRequest>
{
    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception
    {
        HTTPRequest convertedRequest = new HTTPRequest(msg.getProtocolVersion(), msg.method(), msg.uri(),
                                                       msg.content(), msg.headers(), msg.trailingHeaders());
        ctx.fireChannelRead(convertedRequest);
    }
}
