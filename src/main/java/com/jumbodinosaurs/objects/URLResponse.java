package com.jumbodinosaurs.objects;

public class URLResponse
{
    private int statusCode;
    private String response;
    
    public URLResponse(int statusCode,
                       String response)
    {
        this.statusCode = statusCode;
        this.response = response;
    }
    
    public int getStatusCode()
    {
        return statusCode;
    }
    
    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }
    
    public String getResponse()
    {
        return response;
    }
    
    public void setResponse(String response)
    {
        this.response = response;
    }
}
