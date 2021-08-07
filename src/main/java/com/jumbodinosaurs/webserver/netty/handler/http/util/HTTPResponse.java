package com.jumbodinosaurs.webserver.netty.handler.http.util;

import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.webserver.domain.util.Domain;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.HTTPHeader;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.HeaderUtil;
import com.jumbodinosaurs.webserver.util.ServerUtil;

import java.util.ArrayList;
import java.util.Objects;

public class HTTPResponse
{
    //Status Codes
    private final String sC100 = "HTTP/1.1 100 Continue";
    private final String sC101 = "HTTP/1.1 101 Switching Protocols";
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
    
    
    private String messageOut;
    private byte[] bytesOut;
    private boolean keepConnectionAlive = false;
    public static final String headerSeparator = "\r\n";
    
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
    
    public void setBytesOut(byte[] payload)
    {
        this.bytesOut = payload;
    }
    
    public void setClosingHeaders()
    {
        if(bytesOut != null)
        {
            addHeader(HeaderUtil.contentLengthHeader.setValue("" + this.bytesOut.length));
        }
        addHeader(getCloseHeader());
        this.messageOut += headerSeparator + headerSeparator;
    }
    
    public HTTPHeader getCloseHeader()
    {
        return HeaderUtil.connectionHeader.setValue(this.keepConnectionAlive ? "keep-alive, upgrade" : "close");
    }
    
    public void addHeader(HTTPHeader header)
    {
        this.messageOut += headerSeparator + header.toString();
    }
    
    public void addHeaders(ArrayList<HTTPHeader> headers)
    {
        for(HTTPHeader header : headers)
        {
            this.messageOut += headerSeparator + header.toString();
        }
    }
    
    public String getMessageToLog()
    {
        return this.messageOut.substring(0, this.messageOut.indexOf(getCloseHeader().toString()));
    }
    
    public void setDebug()
    {
        /*
         * This function is here to help debug why certain code was thrown
         *
         *  */
        StackTraceElement[] element = Thread.currentThread().getStackTrace();
        for(int i = 0; i < element.length && i < 8; i++)
        {
            //this.messageOut += element[i] + "\n";
            System.out.println(element[i]);
        }
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
        addHeader(HeaderUtil.locationHeader.setValue("https://" + messageHost + request.getPath()));
    }
    
    public void setMessage100()
    {
        this.messageOut = this.sC100;
    }
    
    public void setMessage101()
    {
        this.messageOut = this.sC101;
    }
    
    public void setMessage200()
    {
        this.messageOut = this.sC200;
    }
    
    public void setMessage400()
    {
        this.messageOut = this.sC400;
    }
    
    public void setMessage403()
    {
        this.messageOut = this.sC403;
    }
    
    //Sets the message to send as 404
    public void setMessage404()
    {
        this.messageOut = this.sC404;
        try
        {
            String fourOFourFileContents = GeneralUtil.scanFileContents(ServerUtil.safeSearchDir(ServerUtil.getDirectory,
                                                                                                 "/404.html",
                                                                                                 true));
            this.bytesOut = fourOFourFileContents.getBytes();
        }
        catch(NullPointerException ignored)
        {
        
        }
    }
    
    public void setMessage409()
    {
        this.messageOut = this.sC409;
    }
    
    public void setMessage500()
    {
        this.messageOut = this.sC500;
    }
    
    public void setMessage501()
    {
        this.messageOut = this.sC501;
    }
    
    
    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        HTTPResponse response = (HTTPResponse) o;
        return Objects.equals(messageOut, response.messageOut);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(messageOut);
    }
    
    
    @Override
    public String toString()
    {
        return "HTTPResponse{" + "messageOut='" + messageOut + '\'' + '}';
    }
    
    public boolean shouldKeepConnectionAlive()
    {
        return keepConnectionAlive;
    }
    
    public void setKeepConnectionAlive(boolean keepConnectionAlive)
    {
        this.keepConnectionAlive = keepConnectionAlive;
    }
}