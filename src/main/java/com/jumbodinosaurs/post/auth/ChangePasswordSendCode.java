package com.jumbodinosaurs.post.auth;

import com.jumbodinosaurs.auth.server.AuthToken;
import com.jumbodinosaurs.auth.server.User;
import com.jumbodinosaurs.auth.server.captcha.CaptchaResponse;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.log.LogManager;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.PostCommand;
import com.jumbodinosaurs.util.PasswordStorage;

import java.io.IOException;
import java.time.LocalDateTime;

public class ChangePasswordSendCode extends PostCommand
{
    
    
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /* Process for sending a change password code
         *
         *
         * Check/Verify PostRequest Attributes
         *
         * Check the Captcha Code Given
         * Get the User From the AuthSession
         * Check the given email with the email on record to reduce spam possibilities
         * Check if we need to make a new changePassword AuthToken
         *
         * no ->
         * return 200 okay
         *
         * yes ->
         * Make changePassword AuthToken
         * Set changePassword Token on the user
         * Update the user in the DataBase
         * Prepare Email with Instructions
         * Send Change Password Email
         * Send 200 okay
         *
         *  */
    
        //Check/Verify PostRequest Attributes
        HTTPResponse response = new HTTPResponse();
        if(request.getEmail() == null || request.getCaptchaCode() == null)
        {
            response.setMessage400();
            return response;
        }
    
        //Verify Captcha code
    
        try
        {
            CaptchaResponse captchaResponse = AuthUtil.getCaptchaResponse(request.getCaptchaCode(),
                                                                          authSession.getDomain());
            double captchaScore = captchaResponse.getScore();
            boolean captchaSuccess = captchaResponse.isSuccess();
            if(!(captchaSuccess && captchaScore > .7))
            {
                response.setMessage409();
                return response;
            }
        }
        catch(IOException e)
        {
            LogManager.consoleLogger.error(e.getMessage());
            response.setMessage500();
            return response;
        }
    
    
        //Get the User From the AuthSession
        User user = authSession.getUser();
    
    
        //Check the given email with the email on record to reduce spam possibilities
        if(!request.getEmail().equals(user.getEmail()))
        {
            //Return 200 okay to help avoid email scanning
            response.setMessage200();
            return response;
        }
        
        //Check if we need to make a new changePassword AuthToken
        AuthToken authTokenToCheck = user.getToken(AuthUtil.changePasswordUseName);
        
        //no -> return 200 okay
        if(authTokenToCheck != null && !authTokenToCheck.hasExpired())
        {
            response.setMessage200();
            return response;
        }
        
        /*
         *
         * Make changePassword AuthToken
         * Set changePassword Token on the user
         * Update the user in the DataBase
         * Prepare Email with Instructions
         * Send Change Password Email
         * Send 200 okay
         */
    
    
        // Make changePassword AuthToken
        String token = AuthUtil.generateRandomString(100);
        LocalDateTime expirationDate = LocalDateTime.now();
        expirationDate = expirationDate.plusHours(2);
        AuthToken changePasswordToken;
        try
        {
            changePasswordToken = new AuthToken(AuthUtil.changePasswordUseName, this.ip, token, expirationDate);
        }
        catch(PasswordStorage.CannotPerformOperationException e)
        {
            response.setMessage500();
            return response;
        }
    
        //Set changePassword Token on the user
        User updatedUser = user.clone();
        updatedUser.setToken(changePasswordToken);
        
        //Update the user in the DataBase
        if(!AuthUtil.updateUserNoAuthCheck(authSession, updatedUser))
        {
            response.setMessage500();
            return response;
        }
    
    
        //Prepare Email with Instructions
    
        //This code is specific for jumbodinosaurs.com
        String topic = "Password Change";
        String message = "Here is the token needed to change your password on your Jumbo Dinosaurs Account\n";
        message += "Code: \n\n    " + token + "\n";
    
    
        //Send Change Password Email
        try
        {
            getServersEmail().sendEmail(user.getEmail(), topic, message);
        }
        catch(Exception e)
        {
            response.setMessage500();
            return response;
        }
    
        //Send 200 okay
        response.setMessage200();
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
        return true;
    }
}
