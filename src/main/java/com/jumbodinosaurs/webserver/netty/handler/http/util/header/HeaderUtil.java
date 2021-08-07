package com.jumbodinosaurs.webserver.netty.handler.http.util.header;

import com.jumbodinosaurs.webserver.netty.handler.http.exceptions.NoSuchHeaderException;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPMessage;

public class HeaderUtil
{
    //headers
    public static final HTTPHeader connectionHeader = new HTTPHeader("Connection:", "placeHolder");
    public static final HTTPHeader acceptedLanguageHeader = new HTTPHeader("Accept-Language:", "placeHolder");//
    public static final HTTPHeader originHeader = new HTTPHeader("Origin:", "placeHolder");
    
    
    public static final HTTPHeader contentTypeHeader = new HTTPHeader("Content-Type:", "placeHolder");
    public static final HTTPHeader contentLengthHeader = new HTTPHeader("Content-Length:", "placeHolder");
    
    //Websocket Headers
    public static final HTTPHeader upgradeHeader = new HTTPHeader("Upgrade:", "placeHolder");
    
    public static final HTTPHeader secWebSocketAcceptHeader = new HTTPHeader("Sec-WebSocket-Accept:", " placeHolder");
    
    public static final HTTPHeader webSocketProtocolHeader = new HTTPHeader("Sec-WebSocket-Protocol:", "placeHolder");
    
    public static final HTTPHeader locationHeader = new HTTPHeader("Location:", "placeHolder");
    
    
    public static HTTPHeader extractHeader(HTTPMessage message, ClientHeaderPatterns pattern)
            throws NoSuchHeaderException
    {
        String header = message.getHeader(pattern.getPattern());
        
        int colonIndex = header.indexOf(":");
        if(colonIndex < 0 || colonIndex + 1 > header.length())
        {
            throw new NoSuchHeaderException("Malformed Header (Colon Doesn't Meet Index Constraints)");
        }
        
        String key = header.substring(0, colonIndex);
        String value = header.substring(colonIndex + 1).trim();
        return new HTTPHeader(key, value);
        
    }
}
