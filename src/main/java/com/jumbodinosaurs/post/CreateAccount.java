package com.jumbodinosaurs.post;

import com.google.gson.JsonObject;
import com.jumbodinosaurs.auth.exceptions.NoSuchUserException;
import com.jumbodinosaurs.auth.server.AuthToken;
import com.jumbodinosaurs.auth.server.User;
import com.jumbodinosaurs.auth.server.captcha.CaptchaResponse;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.auth.util.FailureReasons;
import com.jumbodinosaurs.devlib.database.DataBase;
import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.email.Email;
import com.jumbodinosaurs.devlib.email.EmailManager;
import com.jumbodinosaurs.devlib.email.NoSuchEmailException;
import com.jumbodinosaurs.devlib.util.WebUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.util.OptionUtil;
import com.jumbodinosaurs.util.PasswordStorage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;

public class CreateAccount extends PostCommand
{
    
    
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /* Process for creating a new account
         *
         * Check/Verify PostRequest Attributes
         * Verify safe account creation
         *  - Make sure the username is not taken
         *  - sanitize username
         *  - Verify Captcha code
         *
         * Prepare Activation Email
         *
         * Create new User from PostRequest Attributes
         *
         * Add the new User to the User DataBase
         *
         * Send Activation Email
         *
         * Send 200 okay
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        
        //Check/Verify PostRequest Attributes
        if(request.getPassword() == null || request.getEmail() == null || request.getCaptchaCode() == null)
        {
            response.setMessage400();
            return response;
        }
        
        
        
        
        /* Verify safe account creation
         *  - Make sure the username is not taken
         *  - Verify Captcha code
         */
        
        //Make sure the username is not taken
        if(!authSession.getFailureCode().equals(FailureReasons.MISSING_USER))
        {
            response.setMessage409();
            JsonObject reason = new JsonObject();
            reason.addProperty("failureReason", "Username Taken");
            response.addPayload(reason.toString());
            return response;
        }
        
        
        //sanitize username
        if(!AuthUtil.isValidUsername(request.getUsername()))
        {
            response.setMessage409();
            JsonObject reason = new JsonObject();
            reason.addProperty("failureReason", "Username given was not valid");
            return response;
        }
        
        
        //Verify Captcha code
        try
        {
            CaptchaResponse captchaResponse = AuthUtil.getCaptchaResponse(request.getCaptchaCode());
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
            System.out.println(e.getMessage());
            response.setMessage500();
            return response;
        }
        
        /* Send Activation Email
         *
         *
         * Form email with activation code
         *
         * Create Auth Token
         *
         *
         */
        
        
        
        //Form email with activation code
        int accountGracePeriod = 30;
        
        String emailActivationCode = AuthUtil.generateRandomString(100);
        
        
        //TODO Make this a link??
        String topic = "Account Activation";
        String message = "Here is your code to activate you account \n\n";
        message += emailActivationCode;
        message += "\n\n after ";
        message += accountGracePeriod + " days if your account is not activated it will be deleted.";
        
        
        //Create Auth Token
        LocalDateTime now = LocalDateTime.now();
        AuthToken emailToken;
        try
        {
            emailToken = new AuthToken(AuthUtil.emailUseName,
                                       this.ip,
                                       emailActivationCode,
                                       now.plusDays(accountGracePeriod));
        }
        catch(PasswordStorage.CannotPerformOperationException e)
        {
            response.setMessage500();
            return response;
        }
        
        
        
        /* Create new User from PostRequest Attributes
         *
         * Hash Password
         * Convert Hashed Password to Base64
         *
         * Convert Email to base64
         *
         * Create User Object
         *
         * Set users Email activation AuthToken
         */
        
        //Hash Password
        String hashedPassword = null;
        try
        {
            hashedPassword = PasswordStorage.createHash(request.getPassword());
        }
        catch(PasswordStorage.CannotPerformOperationException e)
        {
            response.setMessage500();
            return response;
        }
    
        //Convert hashed Password to Base64
        String base64HashedPassword = Base64.getEncoder().encodeToString(hashedPassword.getBytes());
        
        //Convert Email to base64
        String base64Email = Base64.getEncoder().encodeToString(request.getEmail().getBytes());
        
        
        //Create User Object
        User newUser = new User(request.getUsername(), base64HashedPassword, base64Email);
        
        
        //Set users Email activation AuthToken
        newUser.setToken(emailToken);
        
        
        //Add the new User to the User DataBase
        //Note: We check to make sure the was added
        if(!AuthUtil.addUser(newUser))
        {
            response.setMessage500();
            return response;
        }
        
        
        //Send Activation Email
        //Note: For testing purposes to avoid spamming my own email we check to see if the server is in test mode
        if(!AuthUtil.testMode)
        {
            
            try
            {
                WebUtil.sendEmail(getServersEmail(), request.getEmail(), topic, message);
            }
            catch(MessagingException e)
            {
                /*
                 * If we fail to send the code to the user's email then they can request it again
                 * so we will send 200 okay.
                 */
            }
        }
        
        //Send 200 okay
        response.setMessage200();
        return response;
        
        
    }
    
    @Override
    public boolean requiresUser()
    {
        return false;
    }
}
