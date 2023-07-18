package com.jumbodinosaurs.webserver.rest;

import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPMessage;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.netty.handler.http.util.Method;
import com.jumbodinosaurs.webserver.netty.handler.http.util.queryparameters.QueryParameters;

public class GetAIImageEndPoint extends APIEndPoint
{

    @Override
    public HTTPResponse generateResponse(HTTPMessage message)
    {
        QueryParameters parameters = QueryParameters.parse(message);

        String prompt = parameters.getValue("prompt");
        if(prompt == null || prompt.length() <= 0)
        {
            HTTPResponse response = new HTTPResponse();
            response.setMessage400();
            response.setBytesOut("No Prompt was Provided".getBytes());
            return response;
        }




        LogManager.consoleLogger.debug("Prompt: " + prompt);
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
