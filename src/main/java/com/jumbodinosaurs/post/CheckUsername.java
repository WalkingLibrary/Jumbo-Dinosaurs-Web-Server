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
        /* Process for Checking username availability
         *
         * Check to see if the AuthSession's user is null
         *
         * Check the AuthSession's Failure Reason
         *
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
    
        //Check to see if the AuthSession's user is null
        if(authSession.getUser() != null)
        {
            //Return True
            response.setMessage200();
            JsonObject reason = new JsonObject();
            reason.addProperty("isUserNameTaken", true);
            response.addPayload(reason.toString());
            return response;
        }
        
        
        //Check the AuthSession's Failure Reason
        
        if(authSession.getFailureCode().equals(FailureReasons.MISSING_USERNAME) ||
                   authSession.getFailureCode().equals(FailureReasons.INVALID_USERNAME))
        {
            response.setMessage400();
            return response;
        }
        
        if(authSession.getFailureCode().equals(FailureReasons.SERVER_ERROR))
        {
            response.setMessage500();
            return response;
        }
        
        //Return false
        response.setMessage200();
        JsonObject reason = new JsonObject();
        reason.addProperty("isUserNameTaken", false);
        response.addPayload(reason.toString());
        return response;
    }
    
    @Override
    public boolean requiresUser()
    {
        return false;
    }
}
