package com.jumbodinosaurs.webserver.netty.pipline;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public class CompletionFuture implements GenericFutureListener<ChannelFuture>
{
    private boolean keepConnectionAlive;
    
    public CompletionFuture(boolean keepConnectionAlive)
    {
        this.keepConnectionAlive = keepConnectionAlive;
    }
    
    @Override
    public void operationComplete(ChannelFuture future)
            throws Exception
    {
        if(future.isDone())
        {
            if(!keepConnectionAlive())
            {
                future.channel().close();
            }
        }
    }
    
    public boolean keepConnectionAlive()
    {
        return keepConnectionAlive;
    }
    
    public void keepConnectionAlive(boolean keepConnectionAlive)
    {
        this.keepConnectionAlive = keepConnectionAlive;
    }
}
