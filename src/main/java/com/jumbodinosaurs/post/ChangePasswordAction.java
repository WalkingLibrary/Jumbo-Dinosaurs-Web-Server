package com.jumbodinosaurs.post;

import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;

public class ChangePasswordAction extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /*Process for changing password
         * Check to see if they authenticated properly
         * if the auth was from any token other then ChangePasswordSendCode.changePasswordUseName fail it
         * get the user from the database
         * get the new password from content
         * change the users password
         * update the user in the database
         * send 200 okay
         */
        HTTPResponse response = new HTTPResponse();
        
        
        //if(authSession)
        
        return null;
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
