package com.jumbodinosaurs.webserver.netty.pipline;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public class CompletionFuture implements GenericFutureListener<ChannelFuture>
{
    private boolean closeChannel;
    
    public CompletionFuture(boolean closeChannel)
    {
        this.closeChannel = closeChannel;
    }
    
    @Override
    public void operationComplete(ChannelFuture future)
            throws Exception
    {
        if(future.isDone())
        {
            if(shouldCloseChannel())
            {
                future.channel().close();
            }
        }
    }
    
    public boolean shouldCloseChannel()
    {
        return closeChannel;
    }
    
    public void setCloseChannel(boolean closeChannel)
    {
        this.closeChannel = closeChannel;
    }
}
