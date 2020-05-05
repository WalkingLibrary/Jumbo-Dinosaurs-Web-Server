package com.jumbodinosaurs.post.auth;

import com.jumbodinosaurs.auth.server.User;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.PostCommand;
import com.jumbodinosaurs.util.PasswordStorage;

import java.util.Base64;

public class ChangePasswordForgot extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /*Process for changing password
         * Check/Verify PostRequest Attributes
         * Ensure it was a password token authentication
         * Validate New Password
         * Hash Password
         * Encode Password (Base64)
         * Update Users Password
         * Revoke/Remove Password Token from User
         * Update user in the database
         *
         * */
        HTTPResponse response = new HTTPResponse();
        
        //Check/Verify PostRequest Attributes
        //The new password should be stored in the Content Attribute
        if(request.getContent() == null)
        {
            response.setMessage400();
            return response;
        }
        
        //Ensure it was a password token authentication
        if(!request.getTokenUse().equals(AuthUtil.changePasswordUseName))
        {
            response.setMessage403();
            return response;
        }
        
        //Update Users Password
        //Note: The new password should be stored in the Content Attribute
        String newPassword = request.getContent();
        
        
        //Validate New Password
        if(!AuthUtil.isValidPassword(newPassword))
        {
            response.setMessage400();
            return response;
        }
    
        //Hash Password
        String hashedPassword;
        try
        {
            hashedPassword = PasswordStorage.createHash(newPassword);
        }
        catch(PasswordStorage.CannotPerformOperationException e)
        {
            response.setMessage500();
            return response;
        }
        
        //Encode Password
        String base64HashedPassword = Base64.getEncoder().encodeToString(hashedPassword.getBytes());
    
        //Update users password
        User updatedUser = authSession.getUser();
        updatedUser.setBase64HashedPassword(base64HashedPassword);
    
    
        //Revoke/Remove Password Token from User
        if(!updatedUser.removeToken(AuthUtil.changePasswordUseName))
        {
            response.setMessage500();
            return response;
        }
        
        
        
        //Update user in the database
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
