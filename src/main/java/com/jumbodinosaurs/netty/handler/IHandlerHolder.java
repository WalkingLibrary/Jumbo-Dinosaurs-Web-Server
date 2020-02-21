package com.jumbodinosaurs.netty.handler;

import com.jumbodinosaurs.log.Session;
import io.netty.handler.codec.MessageToMessageDecoder;

public interface IHandlerHolder
{
    MessageToMessageDecoder<Session> getInstance();
}
