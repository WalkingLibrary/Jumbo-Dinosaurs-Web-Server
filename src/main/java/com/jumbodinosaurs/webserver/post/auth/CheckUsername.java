package com.jumbodinosaurs.webserver.post.auth;

import com.google.gson.JsonObject;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.auth.util.AuthUtil;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.HTTPHeader;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.HeaderUtil;
import com.jumbodinosaurs.webserver.post.PostCommand;

public class CheckUsername extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        HTTPResponse response = new HTTPResponse();
        HTTPHeader jsonApplicationTypeHeader = HeaderUtil.contentTypeHeader.setValue("json");
        JsonObject reason = new JsonObject();
        reason.addProperty("isUserNameTaken", AuthUtil.isUserNameTaken(request.getUsername()));
        response.setMessage200();
        response.addHeader(jsonApplicationTypeHeader);
        response.setBytesOut(reason.toString().getBytes());
        return response;
    
    }
    
    @Override
    public boolean requiresSuccessfulAuth()
    {
        return false;
    }
    
    @Override
    public boolean requiresPasswordAuth()
    {
        return false;
    }
    
    @Override
    public boolean requiresUser()
    {
        return false;
    }
}
