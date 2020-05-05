package com.jumbodinosaurs.post.auth;

import com.google.gson.JsonObject;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.PostCommand;

public class CheckUsername extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        HTTPResponse response = new HTTPResponse();
        response.setMessage200();
        JsonObject reason = new JsonObject();
        reason.addProperty("isUserNameTaken", AuthUtil.isUserNameTaken(request.getUsername()));
        response.addPayload(reason.toString());
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
