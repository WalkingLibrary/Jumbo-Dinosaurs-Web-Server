package com.jumbodinosaurs.webserver.netty.handler.http;

import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.netty.handler.IHandlerHolder;
import com.jumbodinosaurs.webserver.log.Session;
import com.jumbodinosaurs.webserver.log.SessionLogManager;
import com.jumbodinosaurs.webserver.netty.handler.http.exceptions.MalformedHTTPMessage;
import com.jumbodinosaurs.webserver.netty.handler.http.exceptions.NoSuchHeaderException;
import com.jumbodinosaurs.webserver.netty.handler.http.util.*;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.ClientHeaderPatterns;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.ResponseHeaderUtil;
import com.jumbodinosaurs.webserver.netty.handler.websocket.WebSocketHandler;
import com.jumbodinosaurs.webserver.util.OptionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.WebSocket13FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocket13FrameEncoder;
import io.netty.util.concurrent.FastThreadLocal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

public class HTTPHandler extends MessageToMessageDecoder<Session> implements IHandlerHolder
{
    
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
    
    public static byte[] digest(FastThreadLocal<MessageDigest> digestFastThreadLocal, byte[] data)
    {
        MessageDigest digest = digestFastThreadLocal.get();
        digest.reset();
        return digest.digest(data);
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, Session msg, List<Object> out)
    {
        try
        {
            //TODO add Server Specific White List Decoder
            if(OptionUtil.isWhiteListOn())
            {
                LogManager.consoleLogger.debug("Whitelist IP: " + msg.getWho());
                String compareWho = msg.getWho().replace("/", "");
                if(!OptionUtil.getWhiteList().contains(compareWho))
                {
                    LogManager.consoleLogger.debug("Blocked: " + msg.getWho());
                    return;
                }
            }
            
            try
            {
                HTTPMessage message = HTTPParser.parseResponse(msg);
                HTTPResponse response = new HTTPResponse();
    
                //Parsing and Upgrading Web Socket requests
                try
                {
                    String upgradeHeader = message.getHeader(ClientHeaderPatterns.UPGRADE_HEADER.getPattern());
                    if(upgradeHeader.contains("websocket"))
                    {
                        // Upgrade to Websocket Protocool
                        System.out.println("Read Request To Upgrade to Websocket");
                        ctx.pipeline().remove("sessionDecoder");
                        ctx.pipeline().remove(this);
                        //ctx.pipeline().addFirst("webSocketHandler", new WebSocketHandler());
                        //ctx.pipeline().addFirst("webSocketProtocolHandler", new WebSocketServerProtocolHandler("/chat"));
                        ctx.pipeline().addFirst("webSocketHandler", new WebSocketHandler());
                        ctx.pipeline().addFirst("wsDecoder", new WebSocket13FrameDecoder(true, true, 1000));
                        ctx.pipeline().addFirst("wsEncoder", new WebSocket13FrameEncoder(false));
                        //ctx.pipeline().addFirst("httpCodex", new HttpServerCodec());
            
            
                        //Make accept Header
                        //Read in client random
                        String base64ClientRandom;
                        try
                        {
                            String clientRandom = message.getHeader(ClientHeaderPatterns.WEB_SOCKET_KEY.getPattern());
                            base64ClientRandom = clientRandom.substring(clientRandom.indexOf(":") + 1);
                        }
                        catch(NoSuchHeaderException e)
                        {
                            response.setMessage400();
                            ctx.writeAndFlush(response);
                            return;
                        }
                        //258EAFA5-E914-47DA-95CA-C5AB0DC85B11
                        String fixedUUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
                        String shaOneOfClientRandom = base64ClientRandom.trim() + fixedUUID;
                        byte[] sha1 = sha1(shaOneOfClientRandom.getBytes());
                        String base64ShaOneClientRandom = Base64.getEncoder().encodeToString(sha1);
                        String webSocketHeaders = ResponseHeaderUtil.upgradeHeader + "websocket";
                        webSocketHeaders += ResponseHeaderUtil.connectionUpgradeHeader;
                        webSocketHeaders += ResponseHeaderUtil.secWebSocketAcceptHeader +
                                            base64ShaOneClientRandom +
                                            "\r\n\r\n";
                        //webSocketHeaders += ResponseHeaderUtil.webSocketProtocolHeader + " chat" + "\r\n\r\n";
                        response.setMessage101();
                        response.addHeaders(webSocketHeaders);
                        response.setKeepConnectionAlive(true);
                        ctx.writeAndFlush(response);
                        ctx.pipeline().remove("encoder");
                        System.out.println("Pipeline: " + ctx.pipeline().toString());
                        return;
                    }
        
                }
                catch(NoSuchHeaderException ignored)
                {
        
                }
    
                if(!msg.isSecureConnection() && OptionUtil.shouldUpgradeInsecureConnections())
                {
                    response.setMessageToRedirectToHTTPS(message);
                }
                else
                {
                    HTTPResponseGenerator httpResponseGenerator = new HTTPResponseGenerator(msg);
                    response = httpResponseGenerator.generateResponse(message);
        
                }
                response.setClosingHeaders();
    
                //Would be kinda point less to hash a password if we saved it over in logs.json :P
                msg.setMessage(message.getCensoredMessage());
                msg.setMessageSent(response.getMessageToLog());
    
                //Send Response
                ctx.writeAndFlush(response);
    
                /*DEBUG MESSAGES*/
                if(OptionUtil.isInDebugMode())
                {
                    if(message.getMethod().equals(Method.POST))
                    {
    
                        if(message.getPostRequest() != null)
                        {
                            //System.out.println(message.getPostRequest().toString());
                        }
                    }
                    System.out.println(msg.toString());
                    System.out.println(response.toString());
                }
    
                SessionLogManager.log(msg);
            }
            catch(MalformedHTTPMessage e)
            {
                LogManager.consoleLogger.debug(e.getMessage(), e);
            }
            
        }
        catch(Exception e)
        {
            LogManager.consoleLogger.error("Uncaught Exception in HTTP Handler: " + e.getMessage(), e);
        }
        
    }
    
    protected String getWebSocketURL(HTTPMessage req)
    {
        System.out.println("Req URI : " + req.getPath());
        String url = "ws://localhost/";
        System.out.println("Constructed URL : " + url);
        return url;
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause)
    {
        context.close();
    }
    
    
    @Override
    public MessageToMessageDecoder<Session> getInstance()
    {
        return new HTTPHandler();
    }
    
    
}