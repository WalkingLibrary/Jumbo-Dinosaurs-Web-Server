package com.jumbodinosaurs.post.object;

import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.PostCommand;

public abstract class CRUDCommand extends PostCommand
{
    
    public abstract HTTPResponse getResponse(PostRequest postRequest, AuthSession authSession, CRUDRequest crudRequest);
    
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        return null;
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
