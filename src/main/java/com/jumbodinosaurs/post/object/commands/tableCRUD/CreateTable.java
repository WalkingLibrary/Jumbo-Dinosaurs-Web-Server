package com.jumbodinosaurs.post.object.commands.tableCRUD;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.auth.server.captcha.CaptchaResponse;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.log.LogManager;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.object.*;
import com.jumbodinosaurs.post.object.exceptions.NoSuchPostObject;

import java.io.IOException;

public class CreateTable extends CRUDCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest postRequest,
                                    AuthSession authSession,
                                    CRUDRequest crudRequest,
                                    Table table)
    {
    
        /* Process for creating a new table
         *
         * Check/Verify PostRequest Attributes
         * Verify Captcha Code
         * Check/Verify crudRequest Attributes
         * Check/Validate Display Name
         * Get The Type of object to Store
         * Create the new table
         * Set the users permissions on the table
         * add the table to the database
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        
        //Check/Verify PostRequest Attributes
        if(postRequest.getCaptchaCode() == null)
        {
            response.setMessage400();
            return response;
        }
        
        //Verify Captcha code
        if(!AuthUtil.testMode)
        {
            try
            {
                CaptchaResponse captchaResponse = AuthUtil.getCaptchaResponse(postRequest.getCaptchaCode());
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
        }
    
    
        //Check/Verify crudRequest Attributes
        if(crudRequest.getObjectType() == null)
        {
            response.setMessage400();
            return response;
        }
    
    
        //Check/Validate Display Name
        String displayName = crudRequest.getTableName();
    
        if(CRUDUtil.isValidTableDisplayName(displayName))
        {
            response.setMessage409();
            JsonObject reason = new JsonObject();
            reason.addProperty("failureReason", "Table Name Not Valid");
            response.addPayload(reason.toString());
            return response;
        }
        
        
        //Get The Type of object to Store
        TypeToken typeToken;
        try
        {
            typeToken = CRUDUtil.getTypeToken(crudRequest.getObjectType());
        }
        catch(NoSuchPostObject noSuchPostObject)
        {
            response.setMessage400();
            return response;
        }
        
        //Create the new table
        Table newTable = new Table(displayName, false, authSession.getUser().getUsername(), typeToken);
        
        //Set the users permissions on the table
        Permission newPermissions = new Permission(true, true, true, true);
        newTable.addUser(authSession.getUser(), newPermissions);
        
        // add the table to the database
        if(!CRUDUtil.addTable(newTable))
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
    
    @Override
    public boolean requiresTable()
    {
        return false;
    }
}
