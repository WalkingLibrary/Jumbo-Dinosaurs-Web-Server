package com.jumbodinosaurs.netty.handler.http.util;

import java.util.ArrayList;

public class ResponseHeaderUtil
{
    //headers
    public static final String keepAlive = "\r\nConnection: keep-alive\r\n\r\n";
    public static final String acceptedLanguageHeader = "\r\nAccept-Language: en-US";
    public static final String originHeader = "\r\nOrigin: http://www.jumbodinosaurs.com/";
    public static final String contentTextHeader = "\r\nContent-Type: text/";
    public static final String contentImageHeader = "\r\nContent-Type: image/";
    public static final String contentApplicationHeader = "\r\nContent-Type: application/";
    public static final String contentLengthHeader = "\r\nContent-Length: "; //[length in bytes of the image]\r\n
    
    
    public static ArrayList<String> getImageFileTypes()
    {
        ArrayList<String> imageFileTypes = new ArrayList<String>();
        
        imageFileTypes.add("png");
        imageFileTypes.add("jpeg");
        imageFileTypes.add("JPG");
        imageFileTypes.add("ico");
        return imageFileTypes;
    }
    
    public static ArrayList<String> getApplicationFileTypes()
    {
        ArrayList<String> applicationFileTypes = new ArrayList<String>();
        applicationFileTypes.add("zip");
        return applicationFileTypes;
    }

}
