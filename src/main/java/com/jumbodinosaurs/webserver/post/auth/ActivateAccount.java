package com.jumbodinosaurs.webserver.post.auth;

import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.server.User;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.auth.util.AuthUtil;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.post.PostCommand;

public class ActivateAccount extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /* Process for Activating an Account
         *
         * Check/Verify PostRequest Attributes
         * Make sure account is not already active
         * Ensure it was a Activate Account token authentication
         * Update users Activation Status
         * Revoke/Remove Activation Token
         * Update the User in the Data Base
         *
         */
    
        HTTPResponse response = new HTTPResponse();
    
        //Check/Verify PostRequest Attributes
    
        //Make sure account is not already active
        if(authSession.getUser().isActive())
        {
            response.setMessage200();
            return response;
        }
    
        //Ensure it was a Activate Account token authentication
        if(!request.getTokenUse().equals(AuthUtil.emailActivationUseName))
        {
            response.setMessage403();
            return response;
        }
    
        //Update users Activation Status
        User updatedUser = authSession.getUser();
        updatedUser.setActive(true);
        
        //Revoke/Remove Activation Token
        updatedUser.removeToken(AuthUtil.emailActivationUseName);
        
        if(!AuthUtil.updateUser(authSession, updatedUser))
        {
            response.setMessage500();
            return response;
        }
        
        response.setMessage200();
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
