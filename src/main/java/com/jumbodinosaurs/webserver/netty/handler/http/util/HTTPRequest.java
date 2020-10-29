package com.jumbodinosaurs.webserver.netty.handler.http.util;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.domain.DomainManager;
import com.jumbodinosaurs.webserver.domain.util.Domain;
import com.jumbodinosaurs.webserver.netty.handler.http.exceptions.NoSuchHeaderException;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import java.util.Map;


public class HTTPRequest extends DefaultFullHttpRequest
{
    private boolean encryptedConnection;
    private PostRequest postRequest;
    private String ip;
    
    public HTTPRequest(HttpVersion httpVersion,
                       HttpMethod method,
                       String uri,
                       ByteBuf content,
                       HttpHeaders headers,
                       HttpHeaders trailingHeader)
    {
        super(httpVersion, method, uri, content, headers, trailingHeader);
        try
        {
            String postContent = content.readCharSequence(content.readableBytes(), CharsetUtil.US_ASCII).toString();
            postRequest = new Gson().fromJson(postContent, PostRequest.class);
        }
        catch(JsonParseException e)
        {
        
        }
    }
    
    
    public boolean isEncryptedConnection()
    {
        return encryptedConnection;
    }
    
    public void setEncryptedConnection(boolean encryptedConnection)
    {
        this.encryptedConnection = encryptedConnection;
    }
    
    public PostRequest getPostRequest()
    {
        return postRequest;
    }
    
    public void setPostRequest(PostRequest postRequest)
    {
        this.postRequest = postRequest;
    }
    
    public String getIp()
    {
        return ip;
    }
    
    public void setIp(String ip)
    {
        this.ip = ip;
    }
  
    public String getCensoredMessage()
    {
        String censoredMessage = "";
        censoredMessage += this.method().toString() + " " + this.uri() + " HTTP/1.1\r\n";
        censoredMessage += this.headers().toString() + "\r\n";
        return censoredMessage;
    }
    
    public Domain getDomain()
    {
        try
        {
            String hostHeader = getHeader(ClientHeaderPatterns.HOSTHEADER.getPattern());
            String[] hostHeaderSplit = hostHeader.split(" ");
            if(hostHeaderSplit.length <= 1)
            {
                return null;
            }
            String host = hostHeaderSplit[1];
            return DomainManager.getDomain(host);
        }
        catch(NoSuchHeaderException e)
        {
            return null;
        }
    }
    
    public String getHeader(String key) throws NoSuchHeaderException
    {
        
        if(this.headers().contains(key))
        {
            return this.headers().get(key);
        }
        
        throw new NoSuchHeaderException("No header matching the key: " + key );
    }
    
    public String getHeadersFormated()
    {
        String formatedHeaders = "";
        for(Map.Entry key: this.headers().entries())
        {
            formatedHeaders += key.getKey() + ": " +key.getValue() + "\n\r";
        }
        return  formatedHeaders;
    }
    
    @Override
    public String toString()
    {
        return this.method() + " " + this.uri() + " " + this.protocolVersion() + "\n\r" +
                this.getHeadersFormated() + postRequest.toString() ;
    }
}
