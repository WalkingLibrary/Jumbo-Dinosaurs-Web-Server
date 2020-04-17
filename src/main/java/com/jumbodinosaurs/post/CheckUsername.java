package com.jumbodinosaurs.post;

import com.google.gson.JsonObject;
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
         * Check/Verify PostRequest Attributes
         * Verify DataBase Config
         * Check to see if a user exists with the given username
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        //Check/Verify PostRequest Attributes
        if(request.getUsername() == null)
        {
            response.setMessage400();
            return response;
        }
        
        //Verify DataBase Config (Make/Get DataBase)
        DataBase userDataBase = null;
        try
        {
            userDataBase = AuthUtil.getUserDataBase();
        }
        catch(NoSuchDataBaseException e)
        {
            response.setMessage501();
            return response;
        }
        
        
        //Check to see if a user exists with the given username
        try
        {
            User userCheck = AuthUtil.getUser(userDataBase, request.getUsername());
            boolean isUserNameTaken = userCheck != null;
            
            response.setMessage200();
            JsonObject reason = new JsonObject();
            reason.addProperty("isUserNameTaken", isUserNameTaken);
            response.addPayload(reason.toString());
            return response;
            
        }
        catch(SQLException | WrongStorageFormatException e)
        {
            response.setMessage500();
            return response;
        }
    }
}
