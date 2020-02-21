package com.jumbodinosaurs.netty.handler.http.util;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.log.Session;
import com.jumbodinosaurs.netty.handler.http.exceptions.MalformedHTTPMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class HTTPParser
{
    public static HTTPMessage parseResponse(Session session) throws MalformedHTTPMessage
    {
        
        /*
         public HTTPMessage(boolean encryptedConnection,
                       PostRequest postRequest,
                       String ip,
                       Method method, String path, ArrayList<String> headers, String postData)
    {
        this.encryptedConnection = encryptedConnection;
        this.postRequest = postRequest;
        this.ip = ip;
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.postData = postData;
    }*/
    
        
        
        String messageFromClient = session.getMessage();
        String[] lines = messageFromClient.split("\r\n");
        if(lines.length <= 0)
        {
            throw new MalformedHTTPMessage("No Content");
        }
        
        String lineOne = lines[0];
        
        Method method = null;
        for(Method value: Method.values())
        {
            if(lineOne.startsWith(value.getMethod() + " ") )
            {
                method = value;
            }
        }
        
        if(method == null)
        {
            throw new MalformedHTTPMessage("Method not supported");
        }
        
        
        if(!lineOne.endsWith(" HTTP/1.1"))
        {
            throw new MalformedHTTPMessage("HTTP Version not supported");
        }
    
        String path = lineOne.split( " ")[1];
        try
        {
            path = URLDecoder.decode(path, "UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            throw new MalformedHTTPMessage(e.getMessage());
        }
    
        int lastHeaderLine = lines.length;
        if(Method.POST.equals(method))
        {
            lastHeaderLine = lines.length - 1;
        
        }
        
        ArrayList<String> headers = new ArrayList<String>();
        for(int i = 1; i < lastHeaderLine ; i++)
        {
            headers.add(lines[i]);
        }
    
        boolean secureConnection = session.isSecureConnection();
        String ip = session.getWho();
    
        if(Method.GET.equals(method))
        {
            return new HTTPMessage(secureConnection, ip, method, path, headers);
        }
    
        try
        {
            PostRequest postRequest = new Gson().fromJson(lines[lines.length - 1], PostRequest.class);
            return new HTTPMessage(secureConnection, postRequest, ip, method, path, headers);
        }
        catch(JsonParseException e)
        {
            return new HTTPMessage(secureConnection, ip, method, path, headers);
        }
        
        
    }
}
