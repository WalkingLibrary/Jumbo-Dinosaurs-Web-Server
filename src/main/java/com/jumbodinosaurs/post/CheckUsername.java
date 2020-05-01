package com.jumbodinosaurs.post;

import com.google.gson.JsonObject;
import com.jumbodinosaurs.auth.exceptions.NoSuchUserException;
import com.jumbodinosaurs.auth.server.User;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.auth.util.FailureReasons;
import com.jumbodinosaurs.devlib.database.DataBase;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;

import java.sql.SQLException;

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
