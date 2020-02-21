package com.jumbodinosaurs.netty.handler.http.util;

public enum ClientHeaderPatterns
{
    HOSTHEADER("Host: .*");
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
