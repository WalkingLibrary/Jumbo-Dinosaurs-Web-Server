package com.jumbodinosaurs.webserver.netty.handler.websocket;

import com.jumbodinosaurs.webserver.log.Session;
import com.jumbodinosaurs.webserver.netty.handler.http.exceptions.NoSuchHeaderException;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPMessage;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.ClientHeaderPatterns;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.HTTPHeader;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.HeaderUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.concurrent.FastThreadLocal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketUtil
{
    
    public static CopyOnWriteArrayList<Session> openWebSocketConnections = new CopyOnWriteArrayList<Session>();
    
    private static final FastThreadLocal<MessageDigest> SHA1 = new FastThreadLocal<MessageDigest>()
    {
        @Override
        protected MessageDigest initialValue()
                throws Exception
        {
            try
            {
                //Try to get a MessageDigest that uses SHA1
                return MessageDigest.getInstance("SHA1");
            }
            catch(NoSuchAlgorithmException e)
            {
                //This shouldn't happen! How old is the computer?
                throw new IllegalStateException("SHA-1 not supported on this platform - Outdated?");
            }
        }
    };
    
    public static byte[] sha1(byte[] data)
    {
        return digest(SHA1, data);
    }
    
    private static byte[] digest(FastThreadLocal<MessageDigest> digestFastThreadLocal, byte[] data)
    {
        MessageDigest digest = digestFastThreadLocal.get();
        digest.reset();
        return digest.digest(data);
    }
}
