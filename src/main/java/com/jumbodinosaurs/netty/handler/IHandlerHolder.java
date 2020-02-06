package com.jumbodinosaurs.netty.handler;

import io.netty.channel.SimpleChannelInboundHandler;

public interface IHandlerHolder
{
    SimpleChannelInboundHandler<String> getInstance();
}
