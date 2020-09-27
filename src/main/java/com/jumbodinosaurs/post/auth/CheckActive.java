package com.jumbodinosaurs.post.auth;

import com.google.gson.JsonObject;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.netty.handler.http.util.ResponseHeaderUtil;
import com.jumbodinosaurs.post.PostCommand;

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
        response.setMessage200(jsonApplicationTypeHeader, object.toString());
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
