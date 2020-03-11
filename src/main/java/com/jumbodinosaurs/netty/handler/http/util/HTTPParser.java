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
        /* Parsing Process
         * Split the HTTP Content and the HTTP Message
         *  Parsing the HTTP Message
         *   Get the method
         *   Get the Path/uri
         *   Get the headers
         *  Add Connection Info To Request
         *
         *
         *
         *
         * Validate HTTP Message(Through out the parsing process)
         */
    
    
    
    
        /* Splitting the HTTP Content and the HTTP Message
         * Split the message and content
         * get the message
         * get the content
         */
        
        
        //Split the message and content
        String httpMessage, httpContent;
    
        String messageFromClient = session.getMessage();
        
        String[] splitHTTPMessage = messageFromClient.split("\r\n\r\n");
    
        //Validate HTTP Message(Through out the parsing process)
        //Checking for valid formatting
        if(splitHTTPMessage.length <= 0)
        {
            throw new MalformedHTTPMessage("No Content");
        }
        //get the message
        httpMessage = splitHTTPMessage[0];
        
        
        //get the content
        if(splitHTTPMessage.length >= 2)
        {
            httpContent = splitHTTPMessage[1];
            //Because split removed the split char sequence we add it back to the content
            for(int i = 2; i < splitHTTPMessage.length; i++)
            {
                httpContent += "\r\n" + splitHTTPMessage[i];
            }
        }
        else
        {
            httpContent = null;
        }
        
        System.out.println(httpContent);
    
        //Parsing the HTTP Message
        
        //Validate HTTP Message(Through out the parsing process)
        //Checking for valid formatting
        String[] httpContentLines = httpMessage.split("\r\n");
        if(httpContentLines.length <= 0)
        {
            throw new MalformedHTTPMessage("No Content");
        }
        
        
        //Get The Method
        String lineOne = httpContentLines[0];
        
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
        
        
        
        //Get the Path/Uri
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
        
        //get The headers
        ArrayList<String> headers = new ArrayList<String>();
        for(int i = 1; i < httpContentLines.length ; i++)
        {
            headers.add(httpContentLines[i]);
        }
        
        //Add Connection Info To Request
        boolean secureConnection = session.isSecureConnection();
        String ip = session.getWho();
    
        if(Method.GET.equals(method))
        {
            return new HTTPMessage(secureConnection, ip, method, path, headers);
        }
    
        try
        {
            PostRequest postRequest = new Gson().fromJson(httpContent, PostRequest.class);
            return new HTTPMessage(secureConnection, postRequest, ip, method, path, headers);
        }
        catch(JsonParseException e)
        {
            return new HTTPMessage(secureConnection, ip, method, path, headers);
        }
    }
}
