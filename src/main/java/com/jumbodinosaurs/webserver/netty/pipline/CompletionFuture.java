package com.jumbodinosaurs.webserver.netty.pipline;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public class CompletionFuture implements GenericFutureListener<ChannelFuture>
{
    @Override
    public void operationComplete(ChannelFuture future)
            throws Exception
    {
        if(future.isDone())
        {
            future.channel().close();
        }
    }
}
