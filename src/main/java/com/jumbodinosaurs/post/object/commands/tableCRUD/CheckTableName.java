package com.jumbodinosaurs.post.object.commands.tableCRUD;

import com.google.gson.JsonObject;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.object.CRUDCommand;
import com.jumbodinosaurs.post.object.CRUDRequest;
import com.jumbodinosaurs.post.object.CRUDUtil;
import com.jumbodinosaurs.post.object.Table;

public class CheckTableName extends CRUDCommand
{
    
    @Override
    public HTTPResponse getResponse(PostRequest postRequest,
                                    AuthSession authSession,
                                    CRUDRequest crudRequest,
                                    Table table)
    {
        /* Process for creating a new table
         *
         *
         * Validate tableName
         * Check Table Name's availability
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        //Validate tableName
        String tableName = crudRequest.getTableName();
        
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
    public boolean requiresTable()
    {
        return false;
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
