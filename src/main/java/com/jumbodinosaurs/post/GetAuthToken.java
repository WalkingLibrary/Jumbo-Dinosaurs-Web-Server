package com.jumbodinosaurs.post;


import com.google.gson.JsonObject;
import com.jumbodinosaurs.auth.server.AuthToken;
import com.jumbodinosaurs.auth.server.User;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.util.PasswordStorage;

import java.time.LocalDateTime;

public class GetAuthToken extends PostCommand
{
    
    
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        HTTPResponse response = new HTTPResponse();
        
        if(!authSession.isSuccess())
        {
            response.setMessage403();
            return response;
        }
        
        try
        {
            User currentUser = authSession.getUser();
            
            String token = AuthUtil.generateRandomString(100);
            LocalDateTime now = LocalDateTime.now();
            AuthToken authToken = new AuthToken(AuthUtil.authUseName, this.session.getWho(), token, now.plusDays(30));
            currentUser.setToken(authToken);
            if(AuthUtil.updateUser(authSession, currentUser))
            {
                JsonObject object = new JsonObject();
                object.addProperty("token", token);
                response.setMessage200(object.toString());
                return response;
            }
            response.setMessage500();
            return response;
        }
        catch(PasswordStorage.CannotPerformOperationException e)
        {
            response.setMessage500();
            return response;
        }
    }
}
