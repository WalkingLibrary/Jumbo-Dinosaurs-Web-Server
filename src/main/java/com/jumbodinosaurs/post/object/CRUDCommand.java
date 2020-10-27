package com.jumbodinosaurs.post.object;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.PostCommand;
import com.jumbodinosaurs.post.object.exceptions.NoSuchObjectException;
import com.jumbodinosaurs.post.object.exceptions.NoSuchPostObject;

import java.sql.SQLException;

public abstract class CRUDCommand extends PostCommand
{
    
    public abstract HTTPResponse getResponse(PostRequest postRequest,
                                             AuthSession authSession,
                                             CRUDRequest crudRequest,
                                             Table table);
    
    public abstract boolean requiresTable();
    
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /* Process for Short Cutting a CRUD Request
         *
         * Check/Verify PostRequest Attributes
         * Check/Verify CRUDRequest Attributes
         * Filter By requiresTable
         * Get Table for getResponse()
         *
         * Check Table Permissions with AuthSession
         * Check to make sure the account has been activated
         * Ensure password or AuthToken auth
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        //Check/Verify PostRequest Attributes
        if(request.getContent() == null)
        {
            response.setMessage400();
            return response;
        }
        
        
        //Check/Verify CRUDRequest Attributes
        String content = request.getContent();
    
        CRUDRequest crudRequest;
        try
        {
            crudRequest = new Gson().fromJson(content, CRUDRequest.class);
        }
        catch(Exception e)
        {
            response.setMessage400();
            return response;
        }
    
        //TypeToken Checking
        /* Checking/Verifying the Object Type Given here allows following crud commands
         * to only check for if the object type given is null and then assume type tokens
         * from the crud request
         */
        if(crudRequest.getObjectType() != null)
        {
            try
            {
                TypeToken typeToken = CRUDUtil.getTypeToken(crudRequest.getObjectType());
                crudRequest.setTypeToken(typeToken);
            }
            catch(NoSuchPostObject e)
            {
                response.setMessage400();
                return response;
            }
        }
    
    
        //Filter By requiresTable
        // All Commands require a table except for Creating a Table and Retrieving Tables Commands
        if(!requiresTable())
        {
            return getResponse(request, authSession, crudRequest, null);
        }
    
        //Get Table for getResponse()
        //Check to make sure Table ID given is a valid table id
        if(crudRequest.getTableID() < 0)
        {
            response.setMessage400();
            return response;
        }
    
        Table table;
    
        try
        {
            table = CRUDUtil.getTable(crudRequest.getTableID());
        }
        catch(NoSuchDataBaseException e)
        {
            response.setMessage501();
            return response;
        }
        catch(SQLException | WrongStorageFormatException e)
        {
            response.setMessage500();
            return response;
        }
        catch(NoSuchObjectException e)
        {
            response.setMessage400();
            JsonObject object = new JsonObject();
            object.addProperty("failureReason", "Table with ID: " + crudRequest.getTableID() + " does not exist");
            response.addPayload(object.toString());
            return response;
        }
        
        
        //Check Table Permissions with AuthSession
        //Note that the Permission Check here is only for if they have any permissions at all on a given table
        if(!table.isPublic() && authSession.getUser() != null)
        {
            //Check to make sure the account has been activated
            if(!authSession.getUser().isActive() && !AuthUtil.testMode)
            {
                response.setMessage400();
                return response;
            }
    
            if(table.getPermissions(authSession.getUser().getUsername()) == null)
            {
                response.setMessage403();
                return response;
            }
        }
        
        //Ensure password or AuthToken auth
        //Note that in GenerateHTTPResponse 'password auth needed' checks should be preformed
        if(requiresSuccessfulAuth())
        {
            if(authSession.getTokenUsed() != null && !authSession.getTokenUsed().getUse().equals(AuthUtil.authUseName))
            {
                response.setMessage403();
                return response;
            }
        }
        
        return getResponse(request, authSession, crudRequest, table);
        
    }
}
