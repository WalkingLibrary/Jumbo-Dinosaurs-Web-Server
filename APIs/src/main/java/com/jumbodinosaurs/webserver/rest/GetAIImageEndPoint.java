package com.jumbodinosaurs.webserver.rest;

import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPMessage;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.netty.handler.http.util.Method;

public class GetAIImageEndPoint extends APIEndPoint
{

    @Override
    public HTTPResponse generateResponse(HTTPMessage message)
    {

        HTTPResponse response = new HTTPResponse();
        response.setMessage200();
        return response;
    }

    @Override
    public String getEndPointPath()
    {
        return "/ai/image";
    }

    @Override
    public Method getHttpMethod()
    {
        return Method.GET;
    }
}
