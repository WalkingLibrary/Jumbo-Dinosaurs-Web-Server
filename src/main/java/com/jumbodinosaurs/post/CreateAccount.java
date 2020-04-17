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
            response.setMessage500();
            return response;
        }
        
        //sanitize username
        if(!AuthUtil.verifyUsername(request.getUsername()))
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
            response.setMessage500();
            return response;
        }
        
        //Send Activation Email
        try
        {
            Email defaultEmail = EmailManager.getEmail(OptionUtil.getDefaultEmail());
            String emailCode = AuthUtil.generateRandomString(100);
            LocalDateTime now = LocalDateTime.now();
            int accountGracePeriod = 30;
            AuthToken emailToken = new AuthToken(AuthUtil.emailUseName,
                                                 this.session.getWho(),
                                                 emailCode,
                                                 now.plusDays(accountGracePeriod));
            String topic = "Account Activation";
            String message = "Here is your code to activate you account \n\n";
            message += emailCode;
            message += "\n\n after ";
            message += accountGracePeriod + " days if your account is not activated it will be deleted.";
            
            WebUtil.sendEmail(defaultEmail, request.getEmail(), topic, message);
        }
        catch(PasswordStorage.CannotPerformOperationException | MessagingException e)
        {
            response.setMessage500();
            return response;
        }
        catch(NoSuchEmailException e)
        {
            response.setMessage501();
            return response;
        }
        
        
        //Add the new User to the User DataBase
        try
        {
            String hashedPassword = PasswordStorage.createHash(request.getPassword());
            
            //TODO store email and hashed password in base 64
            Query insertQuery = DataBaseUtil.getInsertQuery(AuthUtil.userTableName,
                                                            new User(request.getUsername(),
                                                                     hashedPassword,
                                                                     request.getEmail()));
            DataBaseUtil.queryDataBase(insertQuery, userDataBase);
            if(insertQuery.getResponseCode() == 1)
            {
                //Send 200 okay
                response.setMessage200();
                return response;
            }
        }
        catch(PasswordStorage.CannotPerformOperationException | SQLException e)
        {
            response.setMessage500();
            return response;
        }
        
        response.setMessage500();
        return response;
    }
}
