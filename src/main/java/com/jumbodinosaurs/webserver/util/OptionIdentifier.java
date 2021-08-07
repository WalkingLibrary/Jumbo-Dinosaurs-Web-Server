package com.jumbodinosaurs.webserver.util;

public enum OptionIdentifier
{
    debugMode("debugMode"),
    allowPost("allowPost"),
    isWhiteListOn("isWhiteListOn"),
    whiteList("whiteList"),
    email("email"),
    shouldUpgradeInsecureConnections("shouldUpgradeInsecureConnections"),
    userDataBaseName("userDataBaseName"),
    getDirPath("getDirPath"),
    hiddenDirs("hiddenDirs"),
    webhook("webhook"),
    maxAmountOfConnections("maxAmountOfConnections"),
    allowWebSocketConnections("allowWebSocketConnections");
    private String identifier;
    
    OptionIdentifier(String identifier)
    {
        this.identifier = identifier;
    }
    
    public String getIdentifier()
    {
        return identifier;
    }
    
    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }
}
