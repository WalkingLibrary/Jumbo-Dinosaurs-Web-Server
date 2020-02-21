package com.jumbodinosaurs.netty.handler.http.util;

import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.exceptions.NoSuchHeaderException;

import java.util.ArrayList;


public class HTTPMessage
{
    private boolean encryptedConnection;
    private PostRequest postRequest;
    private String ip;
    private Method method;
    private String path;
    private ArrayList<String> headers;
    
    
    public HTTPMessage(boolean encryptedConnection,
                       String ip,
                       Method method,
                       String path,
                       ArrayList<String> headers)
    {
        this.encryptedConnection = encryptedConnection;
        this.ip = ip;
        this.method = method;
        this.path = path;
        this.headers = headers;
    }
    
    public HTTPMessage(boolean encryptedConnection,
                       PostRequest postRequest,
                       String ip,
                       Method method, String path, ArrayList<String> headers)
    {
        this.encryptedConnection = encryptedConnection;
        this.postRequest = postRequest;
        this.ip = ip;
        this.method = method;
        this.path = path;
        this.headers = headers;
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
    
    public Method getMethod()
    {
        return method;
    }
    
    public void setMethod(Method method)
    {
        this.method = method;
    }
    
    public String getPath()
    {
        return path;
    }
    
    public void setPath(String path)
    {
        this.path = path;
    }
    
    public ArrayList<String> getHeaders()
    {
        return headers;
    }
    
    public void setHeaders(ArrayList<String> headers)
    {
        this.headers = headers;
    }
    
    public String getCensoredMessage()
    {
        String censoredMessage = "";
        censoredMessage += this.method + " " + path + " HTTP/1.1\r\n";
        for(String header: headers)
        {
            censoredMessage += header + "\r\n";
        }
        return censoredMessage;
    }
    
    public String getHeader(String pattern) throws NoSuchHeaderException
    {
        
        for(String header: headers)
        {
            if(header.matches(pattern))
            {
                return header;
            }
        }
        
        throw new NoSuchHeaderException("No header matching the pattern: " + pattern );
    }
}
