package com.jumbodinosaurs.netty.pipline;

import com.jumbodinosaurs.log.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.ArrayList;
import java.util.List;

public class SessionDecoder extends MessageToMessageDecoder<String>
{
    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception
    {
        Session sessionContext = new Session(ctx.channel(), msg);
        sessionContext.setSecureConnection(isSecureSession(sessionContext));
        ctx.fireChannelRead(sessionContext);
    }
    
    
    public boolean isSecureSession(Session session)
    {
        ArrayList<String> securePipes = new ArrayList<String>();
        securePipes.add("sni");
        securePipes.add("ssl");
        for(String pipeName: securePipes)
        {
            if(session.getChannel().pipeline().names().contains(pipeName))
            {
                return true;
            }
        }
        return false;
    }
    
    
}
