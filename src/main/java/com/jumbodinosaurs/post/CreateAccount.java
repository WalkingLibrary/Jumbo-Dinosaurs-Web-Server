package com.jumbodinosaurs.post;

import com.google.gson.JsonObject;
import com.jumbodinosaurs.auth.server.AuthToken;
import com.jumbodinosaurs.auth.server.User;
import com.jumbodinosaurs.auth.server.captcha.CaptchaResponse;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
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
         * Check/Verify PostRequest Attributes
         *
         * Verify DataBase Config
         *
         * Verify safe account creation
         *  - Make sure there are no duplicate usernames
         *  - sanitize username
         *  - Verify Captcha code
         *
         * Send Activation Email
         *
         * Add the new User to the User DataBase
         *
         * Send 200 okay
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        
        //Check/Verify PostRequest Attributes
        if(request.getUsername() == null ||
                   request.getPassword() == null ||
                   request.getEmail() == null ||
                   request.getCaptchaCode() == null)
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
            System.out.println(e.getMessage());
            response.setMessage501();
            return response;
        }
        
        
        /* Verify safe account creation
         *  - Make sure there are no duplicate usernames
         *  - Verify Captcha code
         */
        
        //Make sure there are no duplicate usernames
        try
        {
            User userCheck = AuthUtil.getUser(userDataBase, request.getUsername());
            if(userCheck != null)
            {
                response.setMessage409();
                JsonObject reason = new JsonObject();
                reason.addProperty("failureReason", "Username Taken");
                response.addPayload(reason.toString());
                return response;
            }
        }
        catch(SQLException | WrongStorageFormatException e)
        {
            System.out.println(e.getMessage());
            response.setMessage500();
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
        
        //Send Activation Email
        try
        {
            AuthToken emailToken = null;
            Email defaultEmail = EmailManager.getEmail(OptionUtil.getDefaultEmail());
            String emailCode = AuthUtil.generateRandomString(100);
            LocalDateTime now = LocalDateTime.now();
            int accountGracePeriod = 30;
            emailToken = new AuthToken(AuthUtil.emailUseName,
                                       this.session.getWho(),
                                       emailCode,
                                       now.plusDays(accountGracePeriod));
    
            //TODO Make this a link??
            String topic = "Account Activation";
            String message = "Here is your code to activate you account \n\n";
            message += emailCode;
            message += "\n\n after ";
            message += accountGracePeriod + " days if your account is not activated it will be deleted.";
    
            //Wait to send the email until the account is in the data base
    
            //Add the new User to the User DataBase
            String base64HashedPassword = Base64.getEncoder()
                                                .encodeToString(PasswordStorage.createHash(request.getPassword())
                                                                               .getBytes());
            String base64Email = Base64.getEncoder().encodeToString(request.getEmail().getBytes());
    
            User newUser = new User(request.getUsername(),
                                    base64HashedPassword,
                                    base64Email);
            newUser.setToken(emailToken);
            Query insertQuery = DataBaseUtil.getInsertQuery(AuthUtil.userTableName, newUser);
            DataBaseUtil.manipulateDataBase(insertQuery, userDataBase);
    
            if(insertQuery.getResponseCode() != 1)
            {
                response.setMessage500();
                return response;
            }
    
            if(!AuthUtil.testMode)
            {
                WebUtil.sendEmail(defaultEmail, request.getEmail(), topic, message);
            }
    
            //Send 200 okay
            response.setMessage200();
            return response;
    
        }
        catch(MessagingException e)
        {
            //Send 200 okay
            response.setMessage200();
            return response;
        }
        catch(PasswordStorage.CannotPerformOperationException | SQLException e)
        {
            System.out.println(e.getMessage());
            response.setMessage500();
            return response;
        }
        catch(NoSuchEmailException e)
        {
            System.out.println(e.getMessage());
            response.setMessage501();
            return response;
        }
        
    }
}
