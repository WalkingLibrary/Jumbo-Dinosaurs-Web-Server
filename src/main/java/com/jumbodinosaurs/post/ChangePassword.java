package com.jumbodinosaurs.post;

import com.jumbodinosaurs.auth.server.User;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.util.PasswordStorage;

import java.util.Base64;

public class ChangePassword extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /* Process for Changing a Users Password
         *
         * Check/Verify PostRequest Attributes
         * Note: The New Password Should be in the Content Attribute of the PostRequest
         * Validate New Password
         * Hash The New Password
         * Encode The New Password (Base64)
         * Update the Users Password
         * Update the User in the DataBase
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        //Check/Verify PostRequest Attributes
        //Note: The New Password Should be in the Content Attribute of the PostRequest
        if(request.getContent() == null)
        {
            response.setMessage400();
            return response;
        }
        
        
        //Validate New Password
        String newPassword = request.getContent();
        //Note: The New Password Should be in the Content Attribute of the PostRequest
        if(!AuthUtil.isValidPassword(newPassword))
        {
            response.setMessage400();
            return response;
        }
        
        
        //Hash The New Password
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
        
        //Encode The New Password (Base64)
        String base64HashedPassword = Base64.getEncoder().encodeToString(hashedPassword.getBytes());
        
        
        //Update the Users Password
        User updatedUser = authSession.getUser();
        updatedUser.setBase64HashedPassword(base64HashedPassword);
        
        //Update the User in the DataBase
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
        return true;
    }
    
    @Override
    public boolean requiresUser()
    {
        return true;
    }
}
