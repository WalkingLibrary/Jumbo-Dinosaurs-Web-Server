package com.jumbodinosaurs.webserver.rest;

import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPMessage;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.netty.handler.http.util.Method;

public abstract class APIEndPoint
{
    public abstract HTTPResponse generateResponse(HTTPMessage message);

    public abstract String getEndPointPath();



    public abstract Method getHttpMethod();
}
