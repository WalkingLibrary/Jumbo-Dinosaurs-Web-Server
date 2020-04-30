package com.jumbodinosaurs.post;

import com.google.gson.JsonObject;
import com.jumbodinosaurs.auth.exceptions.NoSuchUserException;
import com.jumbodinosaurs.auth.server.User;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
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
        /* Process for Checking username availability
         * Check the given Auth Sessions user variable
         * return 200 okay with boolean
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        //Check the given Auth Sessions user variable
        boolean isUserNameTaken = authSession.getUser() != null;
        
        response.setMessage200();
        JsonObject reason = new JsonObject();
        reason.addProperty("isUserNameTaken", isUserNameTaken);
        response.addPayload(reason.toString());
        return response;
    }
    
    @Override
    public boolean requiresUser()
    {
        return false;
    }
}
