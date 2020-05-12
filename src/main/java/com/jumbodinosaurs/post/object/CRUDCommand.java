package com.jumbodinosaurs.post.object;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.PostCommand;
import com.jumbodinosaurs.post.object.commands.tableCRUD.CreateTable;
import com.jumbodinosaurs.post.object.exceptions.NoSuchTableException;

import java.sql.SQLException;

public abstract class CRUDCommand extends PostCommand
{
    
    public abstract HTTPResponse getResponse(PostRequest postRequest,
                                             AuthSession authSession,
                                             CRUDRequest crudRequest,
                                             Table table);
    
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /* Process for Short Cutting a CRUD Request
         *
         * Check/Verify PostRequest Attributes
         * Check/Verify CRUDRequest Attributes
         * Validate Table Name
         * Filter By Create Command
         * Check Table Permissions with AuthSession
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
        
        //Check/Verify ContentObject Attributes
        //All CRUD request require a tableName
        if(crudRequest.getTableName() == null)
        {
            response.setMessage400();
            return response;
        }
        
        //Validate Table Name
        String tableName = crudRequest.getTableName();
        
        if(CRUDUtil.isValidTableName(tableName))
        {
            response.setMessage409();
            JsonObject reason = new JsonObject();
            reason.addProperty("failureReason", "Table Name given was not valid");
            response.addPayload(reason.toString());
        }
        
        //Filter By Create Command
        // All Commands require a table except for creating a table
        if(request.getCommand().equals(CreateTable.class.getSimpleName()))
        {
            return getResponse(request, authSession, crudRequest, null);
        }
        
        
        Table table;
        
        try
        {
            table = CRUDUtil.getTable(tableName);
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
        catch(NoSuchTableException e)
        {
            response.setMessage400();
            JsonObject object = new JsonObject();
            object.addProperty("failureReason", "Table Name Taken");
            response.addPayload(object.toString());
            return response;
        }
        
        
        //Check Table Permissions with AuthSession
        if(!table.isPublic() && authSession.getUser() != null)
        {
            if(table.getPermissions(authSession.getUser().getUsername()) == null)
            {
                response.setMessage403();
                return response;
            }
        }
        
        return getResponse(request, authSession, crudRequest, table);
        
    }
}
