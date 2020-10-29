package com.jumbodinosaurs.netty.handler.http.util;

public enum ClientHeaderPatterns
{
    HOSTHEADER("Host: .*"), CONTENT_LENGTH_HEADER("Content-Length: .*");
    private String pattern;
    
    ClientHeaderPatterns(String pattern)
    {
        this.pattern = pattern;
    }
    
    public String getPattern()
    {
        return pattern;
    }
}
