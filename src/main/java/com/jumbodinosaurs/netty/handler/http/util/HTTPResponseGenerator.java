package com.jumbodinosaurs.netty.handler.http.util;

import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.domain.util.Domain;
import com.jumbodinosaurs.util.ServerUtil;

import java.io.File;

public class HTTPResponseGenerator
{
    public static HTTPResponse generateResponse(HTTPMessage message)
    {
        if(message.getMethod().equals(Method.GET))
        {
            /*
             *
             *
             *
             */
            String filePath = message.getPath();
            if(filePath.equals("/"))
            {
                filePath = "/home.html";
            }
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
                if(fileToServe == null)
                {
                    HTTPResponse response = new HTTPResponse();
                    response.setMessage404();
                    return response;
                }
            }
            
            
            String headers = "";
            String type = GeneralUtil.getType(fileToServe);
            
            
            if(ResponseHeaderUtil.getImageFileTypes().contains(type))
            {
                byte[] photoBytes = ServerUtil.readPhoto(fileToServe);
                headers += ResponseHeaderUtil.contentImageHeader + type;
                headers += ResponseHeaderUtil.contentLengthHeader + photoBytes.length;
                HTTPResponse response = new HTTPResponse();
                response.setMessage200(headers, photoBytes);
                return response;
            }
            
            
            if(ResponseHeaderUtil.getApplicationFileTypes().contains(type))
            {
                byte[] applicationBytes = ServerUtil.readZip(fileToServe);
                headers += ResponseHeaderUtil.contentApplicationHeader + type;
                headers += ResponseHeaderUtil.contentLengthHeader + applicationBytes.length;
                HTTPResponse response = new HTTPResponse();
                response.setMessage200(headers, applicationBytes);
                return response;
            }
            
            
            headers += ResponseHeaderUtil.contentTextHeader + type;
            HTTPResponse response = new HTTPResponse();
            response.setMessage200(headers, GeneralUtil.scanFileContents(fileToServe));
            return response;
        }
    
    
        HTTPResponse response = new HTTPResponse();
        response.setMessage501();
        return response;
    }
    
  
    
    
    
    
    
    
    
}
