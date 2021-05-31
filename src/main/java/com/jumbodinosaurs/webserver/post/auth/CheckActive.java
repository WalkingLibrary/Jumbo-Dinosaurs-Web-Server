package com.jumbodinosaurs.webserver.post.auth;

import com.google.gson.JsonObject;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.ResponseHeaderUtil;
import com.jumbodinosaurs.webserver.post.PostCommand;

public class CheckActive extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /*
         * Process for checking if an account is active
         * We get the account from the AuthSession and return if it is active or not
         *
         *  */
    
        HTTPResponse response = new HTTPResponse();
        String jsonApplicationTypeHeader = ResponseHeaderUtil.contentApplicationHeader + "json";
        boolean isActive = authSession.getUser().isActive();
    
        JsonObject object = new JsonObject();
        object.addProperty("isActive", isActive);
        response.setMessage200();
        response.addHeaders(jsonApplicationTypeHeader);
        response.setBytesOut(object.toString().getBytes());
        return response;
    }
    
    @Override
    public boolean requiresSuccessfulAuth()
    {
        return true;
    }
    
    @Override
    public boolean requiresPasswordAuth()
    {
        return false;
    }
    
    @Override
    public boolean requiresUser()
    {
        return true;
    }
}
