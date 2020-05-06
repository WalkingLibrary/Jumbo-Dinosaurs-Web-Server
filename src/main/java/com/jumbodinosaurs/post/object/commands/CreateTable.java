package com.jumbodinosaurs.post.object.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.jumbodinosaurs.auth.server.captcha.CaptchaResponse;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.PostCommand;
import com.jumbodinosaurs.post.object.ContentObject;
import com.jumbodinosaurs.post.object.Table;
import com.jumbodinosaurs.post.object.TableManager;

import java.io.IOException;

public class CreateTable extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /* Process for creating a new table
         *
         *
         * Check/Verify PostRequest Attributes
         * Verify Captcha Code
         * Get ContentObject
         * Check/Verify ContentObject Attributes
         * Validate Table Name
         * Check to make sure that the table name is not already taken
         * validate objectName
         * Create the new table
         * Set the users permissions on the table
         * add the table to the database
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        
        //Check/Verify PostRequest Attributes
        if(request.getContent() == null || request.getCaptchaCode() == null)
        {
            response.setMessage400();
            return response;
        }
        
        //Verify Captcha code
        if(!AuthUtil.testMode)
        {
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
        }
        
        
        //ContentObject
        String content = request.getContent();
        
        ContentObject contentObject;
        try
        {
            contentObject = new Gson().fromJson(content, ContentObject.class);
        }
        catch(JsonParseException e)
        {
            response.setMessage400();
            return response;
        }
        
        //Check/Verify ContentObject Attributes
        if(contentObject.getTableName() == null || contentObject.getObjectName() == null)
        {
            response.setMessage400();
            return response;
        }
        
        
        //Validate Table Name
        String tableName = contentObject.getTableName();
        if(!TableManager.isValidTableName(tableName))
        {
            response.setMessage409();
            JsonObject reason = new JsonObject();
            reason.addProperty("failureReason", "Table Name given was not valid");
            response.addPayload(reason.toString());
        }
        
        
        //Check to make sure that the table name is not already taken
        if(TableManager.isTableNameTaken(tableName))
        {
            response.setMessage409();
            JsonObject reason = new JsonObject();
            reason.addProperty("failureReason", "Table Name Taken");
            response.addPayload(reason.toString());
            return response;
        }
        
        
        //validate objectName
        
        
        //Create the new table
        Table newTable = new Table(tableName, false, authSession.getUser().getUsername(), );
        
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
        return false;
    }
}
