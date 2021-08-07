package com.jumbodinosaurs.webserver.netty.handler.http.util.header;

public class HTTPHeader
{
    //Key should have : in it
    private String key;
    private String value;
    
    public HTTPHeader(String key, String value)
    {
        this.key = key;
        this.value = value;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public void setKey(String key)
    {
        this.key = key;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public HTTPHeader setValue(String value)
    {
        this.value = value;
        return copy();
    }
    
    public HTTPHeader copy()
    {
        return new HTTPHeader(key, value);
    }
    
    @Override
    public String toString()
    {
        return key + " " + value;
    }
}
