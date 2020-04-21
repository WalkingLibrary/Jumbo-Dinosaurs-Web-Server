package com.jumbodinosaurs.netty.handler.http.util;

import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.domain.util.Domain;
import com.jumbodinosaurs.util.ServerUtil;

public class HTTPResponse
{
    //Status Codes
    private final String sC100 = "HTTP/1.1 100 Continue";
    private final String sC200 = "HTTP/1.1 200 OK";
    private final String sC301 = "HTTP/1.1 301  Permanently";
    private final String sC303 = "HTTP/1.1 303 Temporary";
    private final String sC304 = "HTTP/1.1 304 Not Modified";
    private final String sC400 = "HTTP/1.1 400 Bad";
    private final String sC401 = "HTTP/1.1 401 Unauthorized";
    private final String sC403 = "HTTP/1.1 403 Forbidden";
    private final String sC404 = "HTTP/1.1 404 Not Found";
    private final String sC409 = "HTTP/1.1 409 Conflict";
    private final String sC500 = "HTTP/1.1 500 Internal Server Error";
    private final String sC501 = "HTTP/1.1 501 Not Implemented";
    //headers
    private final String locationHeader = "\r\nLocation:";
    private final String closeHeader = " \r\nConnection: Close\r\n\r\n";
    
    private String messageOut;
    private byte[] bytesOut;
    
    public HTTPResponse()
    {
    
    }
    
    public String getMessage()
    {
        return messageOut;
    }
    
    
    public byte[] getBytesOut()
    {
        return bytesOut;
    }
    
    public void setBytesOut(byte[] bytesOut)
    {
        this.bytesOut = bytesOut;
    }
    
    
    public void setMessage200(String headers, byte[] bytesOut)
    {
        this.messageOut = this.sC200;
        this.messageOut += headers;
        this.messageOut += closeHeader;
        this.bytesOut = bytesOut;
    }
    
    public void setMessage200(String headers, String payload)
    {
        this.messageOut = this.sC200;
        this.messageOut += headers;
        this.messageOut += closeHeader;
        this.messageOut += payload;
    }
    
    public void setMessage200(String payload)
    {
        this.messageOut = this.sC200;
        this.messageOut += closeHeader;
        this.messageOut += payload;
    }
    
    public void setMessage200()
    {
        this.messageOut = this.sC200;
        this.messageOut += closeHeader;
    }
    
    public void setMessageToRedirectToHTTPS(HTTPMessage request)
    {
        Domain messageHost = request.getDomain();
        if(messageHost == null)
        {
            setMessage400();
            return;
        }
        this.messageOut = this.sC301;
        this.messageOut += this.locationHeader + " https://" + messageHost + request.getPath();
        this.messageOut += this.closeHeader;
    }
    
    public void setMessage400()
    {
        this.messageOut = this.sC400;
        this.messageOut += this.closeHeader;
    }
    
    public void setMessage400(String payload)
    {
        this.messageOut = this.sC400;
        this.messageOut += this.closeHeader;
        this.messageOut += payload;
    }
    
    public void setMessage403()
    {
        this.messageOut = this.sC403;
        this.messageOut += this.closeHeader;
    }
    
    //Sets the message to send as 404
    public void setMessage404()
    {
        this.messageOut = this.sC404;
        this.messageOut += this.closeHeader;
        this.messageOut += GeneralUtil.scanFileContents(ServerUtil.safeSearchDir(ServerUtil.getDirectory,
                                                                                 "/404.html",
                                                                                 true));
    }
    
    public void setMessage409()
    {
        this.messageOut = this.sC409;
        this.messageOut += this.closeHeader;
    }
    
    public void setMessage500()
    {
        this.messageOut = this.sC500;
        this.messageOut += this.closeHeader;
    }
    
    public void setMessage501()
    {
        this.messageOut = this.sC501;
        this.messageOut += this.closeHeader;
    }
    
    public void addPayload(String payload)
    {
        messageOut += payload;
    }
    
    public String getMessageToLog()
    {
        return this.messageOut.substring(0, this.messageOut.indexOf(this.closeHeader));
    }
    
    
    @Override
    public String toString()
    {
        return "HTTPResponse{" +
                       "messageOut='" + messageOut + '\'' +
                       '}';
    }
}