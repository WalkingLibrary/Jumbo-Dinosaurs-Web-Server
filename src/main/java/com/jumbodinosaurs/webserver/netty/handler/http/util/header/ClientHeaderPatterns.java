package com.jumbodinosaurs.webserver.netty.handler.http.util.header;

public enum ClientHeaderPatterns
{
    HOSTHEADER("Host: .*"),
    CONTENT_LENGTH_HEADER("Content-Length: .*"),
    UPGRADE_HEADER("Upgrade: .*"),
    WEB_SOCKET_KEY("Sec-WebSocket-Key: .*");
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
