package com.jumbodinosaurs.netty.handler.http.util;

import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.domain.util.Domain;
import com.jumbodinosaurs.util.ServerUtil;
import io.netty.handler.codec.http.HttpMethod;

import java.io.File;

public class HTTPResponseGenerator
{
    public static HTTPResponse generateResponse(HTTPRequest message)
    {
        HTTPResponse response = new HTTPResponse();
        if(message.getMethod().equals(HttpMethod.GET))
        {
            /* Dealing with GET Requests
             * We first need to analyze the file they are requesting and change it if need be
             * We then need to search our GET dir for the specified file
             * If we Don't have the file they are looking for we return a 404 message with the 404 page
             * Next we need to form our headers for the message which depends on the type of file we are sending
             */
            
            
            
            //We first need to analyze the file they are requesting and change it if need be
            String filePath = message.getUri();
            if(filePath.equals("/"))
            {
                filePath = "/home.html";
            }
            
            
            
            
            
            // We then need to search our GET dir for the specified file
            Domain messageHost = message.getDomain();
            File dirToSearch = ServerUtil.getDirectory;
            
            if(messageHost != null)
            {
                dirToSearch = messageHost.getGetDir();
            }
            
            File fileToServe = ServerUtil.safeSearchDir(dirToSearch, filePath, false);
            
            if(fileToServe == null)
            {
                fileToServe = ServerUtil.safeSearchDir(ServerUtil.getDirectory, filePath, false);
                
                //If we Don't have the file they are looking for we return a 404 message with the 404 page
                if(fileToServe == null)
                {
                    response.setMessage404();
                    return response;
                }
            }
            
            //Next we need to form our headers for the message which depends on the type of file we are sending
            String headers = "";
            String type = GeneralUtil.getType(fileToServe);
            
            
            if(ResponseHeaderUtil.getImageFileTypes().contains(type))
            {
                byte[] photoBytes = ServerUtil.readPhoto(fileToServe);
                headers += ResponseHeaderUtil.contentImageHeader + type;
                headers += ResponseHeaderUtil.contentLengthHeader + photoBytes.length;
                response.setMessage200(headers, photoBytes);
                return response;
            }
            
            
            if(ResponseHeaderUtil.getApplicationFileTypes().contains(type))
            {
                byte[] applicationBytes = ServerUtil.readZip(fileToServe);
                headers += ResponseHeaderUtil.contentApplicationHeader + type;
                headers += ResponseHeaderUtil.contentLengthHeader + applicationBytes.length;
                response.setMessage200(headers, applicationBytes);
                return response;
            }
            
            
            headers += ResponseHeaderUtil.contentTextHeader + type;
            response.setMessage200(headers, GeneralUtil.scanFileContents(fileToServe));
            return response;
        }
        
        
        
        
        if(message.getMethod().equals(HttpMethod.POST))
        {
            if(message.getPostRequest() == null)
            {
               response.setMessage400();
               return response;
            }
        }
        
        response.setMessage501();
        return response;
    }
    
  
    
    
    
    
    
    
    
}
