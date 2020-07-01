package com.jumbodinosaurs.post.object.commands.objectCRUD;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.object.*;

import java.sql.SQLException;

public class DeleteObject extends CRUDCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest postRequest,
                                    AuthSession authSession,
                                    CRUDRequest crudRequest,
                                    Table table)
    {
        /*
         * Process for Removing an object from the database
         *
         * Check/Verify CRUDRequest Attributes
         * Validate Users Permissions on the Table
         * Parse Object
         * Validate Object
         * Prepare Query
         * Remove object by id
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        
        //Check/Verify CRUDRequest Attributes
        if(crudRequest.getObject() == null)
        {
            response.setMessage400();
            return response;
        }
        
        //Validate Users Permissions on the Table
        Permission permissions = table.getPermissions(authSession.getUser().getUsername());
        if(!permissions.canRemove())
        {
            response.setMessage403();
            return response;
        }
        
        
        // Parse Object
        PostObject objectToDelete;
        
        try
        {
            objectToDelete = new Gson().fromJson(crudRequest.getObject(), table.getObjectType().getType());
        }
        catch(JsonSyntaxException e)
        {
            response.setMessage400();
            return response;
        }
        
        
        // Validate Object
        if(!objectToDelete.isValidObject())
        {
            response.setMessage400();
            return response;
        }
        
        //Prepare Query
        Query deleteQuery = DataBaseUtil.getDeleteQuery(table.getName(), objectToDelete);
        
        try
        {
            CRUDUtil.manipulateObjectDataBase(deleteQuery);
        }
        catch(NoSuchDataBaseException e)
        {
            response.setMessage501();
            return response;
        }
        catch(SQLException e)
        {
            response.setMessage500();
            return response;
        }
        
        
        response.setMessage200();
        return response;
        
    }
    
    @Override
    public boolean requiresTable()
    {
        return true;
    }
    
    @Override
    public boolean requiresSuccessfulAuth()
    {
        return true;
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
