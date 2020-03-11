package com.jumbodinosaurs.netty.handler.http.util;


public enum Method
{
    GET("GET"), POST("POST");
    private String method;
    
    Method(String method)
    {
        this.method = method;
    }
    
    public String getMethod()
    {
        return method;
    }
    
    public boolean equals(Method method)
    {
        return this.method.equals(method.getMethod());
    }
}
