package com.jumbodinosaurs.webserver.post.auth;


import com.google.gson.JsonObject;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.server.AuthToken;
import com.jumbodinosaurs.webserver.auth.server.User;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.auth.util.AuthUtil;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.ResponseHeaderUtil;
import com.jumbodinosaurs.webserver.post.PostCommand;
import com.jumbodinosaurs.webserver.util.PasswordStorage;

import java.time.LocalDateTime;

public class GetAuthToken extends PostCommand
{
    
    
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /* Process for Getting an auth Token
         *
         * Get the User From the AuthSession
         * Create a New AuthToken
         * Set the Users AuthToken and Update the User in the DataBase
         * Send the AuthToken in response
         *
         */
        
        
        HTTPResponse response = new HTTPResponse();
    
    
        // Get the User From the AuthSession
        User currentUser = authSession.getUser();
        
        
        //Create a New AuthToken
        String token = AuthUtil.generateRandomString(100);
        LocalDateTime now = LocalDateTime.now();
        AuthToken authToken;
        try
        {
            authToken = new AuthToken(AuthUtil.authUseName, this.ip, token, now.plusDays(30));
        }
        catch(PasswordStorage.CannotPerformOperationException e)
        {
            response.setMessage500();
            return response;
        }
        
        
        //Set the Users AuthToken and Update the User in the DataBase
        currentUser.setToken(authToken);
        
        if(AuthUtil.updateUser(authSession, currentUser))
        {
            String jsonApplicationTypeHeader = ResponseHeaderUtil.contentApplicationHeader + "json";
            JsonObject object = new JsonObject();
            object.addProperty("token", token);
            object.addProperty("tokenUse", AuthUtil.authUseName);
            response.setMessage200();
            response.addHeaders(jsonApplicationTypeHeader);
            response.setBytesOut(object.toString().getBytes());
            return response;
        }
        
        response.setMessage500();
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
        return true;
    }
    
    @Override
    public boolean requiresUser()
    {
        return true;
    }
}
