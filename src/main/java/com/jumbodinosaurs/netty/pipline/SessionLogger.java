package com.jumbodinosaurs.netty.pipline;

import com.jumbodinosaurs.log.LogManager;
import com.jumbodinosaurs.log.Session;
import com.jumbodinosaurs.netty.handler.ISessionLoggable;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.ArrayList;
import java.util.List;

public class SessionLogger extends MessageToMessageEncoder<ISessionLoggable>
{
    
    
    
    @Override
    protected void encode(ChannelHandlerContext ctx, ISessionLoggable msg, List<Object> out) throws Exception
    {
        Session sessionContext = new Session(ctx.channel());
        sessionContext.setMessage(msg.getMessageReceived());
        sessionContext.setMessage(msg.getMessageSent());
        sessionContext.setSecureConnection(isSecureSession(sessionContext));
        LogManager.log(sessionContext);
        ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
    }
    
    public boolean isSecureSession(Session session)
    {
        ArrayList<String> securePipes = new ArrayList<String>();
        securePipes.add("sni");
        securePipes.add("ssl");
        for(String pipeName : securePipes)
        {
            if(session.getChannel().pipeline().names().contains(pipeName))
            {
                return true;
            }
        }
        return false;
    }
    
    
}
