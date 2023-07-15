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
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketUtil
{
    
    public static CopyOnWriteArrayList<Session> openWebSocketConnections = new CopyOnWriteArrayList<Session>();


    public static HTTPResponse generateUpgradeResponse(HTTPMessage message, Session sessionContext)
    {
        /* Process for Parsing Request Info
         * Get Client Key
         * Get Web Socket Version Default is 00
         *
         * */
        //Get Client Key
        String clientKey;
        try
        {
            HTTPHeader clientKeyHeader = HeaderUtil.extractHeader(message,
                    ClientHeaderPatterns.WEB_SOCKET_KEY);
            clientKey = clientKeyHeader.getValue();
        }
        catch(NoSuchHeaderException e)
        {
            HTTPResponse response = new HTTPResponse();
            response.setMessage400();
            return response;
        }

        /* Get Web Socket Version Default is 00
         * Netty Supported Versions
         * 00
         * 07
         * 08
         * 13
         *  */

        int defaultMaxFrameSize = 1000;
        boolean expectMaskedFrames = true;
        boolean allowExtensions = true;
        boolean maskPayload = false;
        ChannelHandler webSocketDecoder, webSocketEncoder;

        webSocketDecoder = new WebSocket00FrameDecoder(defaultMaxFrameSize);
        webSocketEncoder = new WebSocket00FrameEncoder();

        try
        {
            HTTPHeader clientWebSocketVersionHeader = HeaderUtil.extractHeader(message,
                    ClientHeaderPatterns.WEB_SOCKET_VERSION);

            int version = Integer.parseInt(clientWebSocketVersionHeader.getValue());
            switch(version)
            {
                case 7:
                    webSocketDecoder = new WebSocket07FrameDecoder(expectMaskedFrames,
                            allowExtensions,
                            defaultMaxFrameSize);
                    webSocketEncoder = new WebSocket07FrameEncoder(maskPayload);
                case 8:
                    webSocketDecoder = new WebSocket08FrameDecoder(expectMaskedFrames,
                            allowExtensions,
                            defaultMaxFrameSize);
                    webSocketEncoder = new WebSocket08FrameEncoder(maskPayload);
                case 13:
                    webSocketDecoder = new WebSocket13FrameDecoder(expectMaskedFrames,
                            allowExtensions,
                            defaultMaxFrameSize);
                    webSocketEncoder = new WebSocket13FrameEncoder(maskPayload);
            }

        }
        catch(NoSuchHeaderException | NumberFormatException ignored)
        {

        }


        //Create response Key
        String webSocketUUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        byte[] sha1Key = WebSocketUtil.sha1((clientKey.trim() + webSocketUUID).getBytes());
        String serverKey = Base64.getEncoder().encodeToString(sha1Key);


        //Edit Pipeline
        ChannelPipeline pipeline = sessionContext.getChannel().pipeline();
        pipeline.remove("sessionDecoder");
        pipeline.remove("framer");
        pipeline.remove("handler");

        //Note: When changing the order in how pipeline is add be aware of the SSL Handler for secure pipes
        pipeline.addLast("websocketDecoder", webSocketDecoder);
        pipeline.addLast("websocketEncoder", webSocketEncoder);
        pipeline.addLast("webSocketHandler", new WebSocketHandler());
        //Create Handshake Response
        HTTPResponse response = new HTTPResponse();
        response.setMessage101();
        response.setKeepConnectionAlive(true);

        ArrayList<HTTPHeader> headers = new ArrayList<HTTPHeader>();
        headers.add(HeaderUtil.upgradeHeader.setValue("websocket"));
        headers.add(HeaderUtil.secWebSocketAcceptHeader.setValue(serverKey));
        response.addHeaders(headers);
        return response;
    }
    
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
