package com.jumbodinosaurs.webserver.netty.handler.http.util.header;

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
