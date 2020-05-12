package com.jumbodinosaurs.post.object.commands.tableCRUD;

import com.google.gson.JsonObject;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.PostCommand;
import com.jumbodinosaurs.post.object.CRUDUtil;

public class CheckTableName extends PostCommand
{
    
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /* Process for creating a new table
         *
         *
         * Check/Verify PostRequest Attributes
         * Validate tableName
         * Check Table Name's availability
         *
         *  */
        HTTPResponse response = new HTTPResponse();
        
        
        //Check/Verify PostRequest Attributes
        if(request.getContent() == null)
        {
            response.setMessage400();
            return response;
        }
        
        //Validate tableName
        String tableName = request.getContent();
        if(!CRUDUtil.isValidTableName(tableName))
        {
            response.setMessage400();
            JsonObject reason = new JsonObject();
            reason.addProperty("failureReason", "Table Name Given was not Valid");
            response.addPayload(reason.toString());
            return response;
        }
        
        
        response.setMessage200();
        JsonObject reason = new JsonObject();
        reason.addProperty("isTableNmeTaken", CRUDUtil.isTableNameTaken(tableName));
        response.addPayload(reason.toString());
        
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
        return false;
    }
}
