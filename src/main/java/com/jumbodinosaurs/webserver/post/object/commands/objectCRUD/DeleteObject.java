package com.jumbodinosaurs.webserver.post.object.commands.objectCRUD;

import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.post.object.*;

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
         * Prepare Query
         * Remove object by id
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        
        //Check/Verify CRUDRequest Attributes
        if(crudRequest.getObjectType() == null)
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
    
    
        //Prepare Query
        String tableToEdit = CRUDUtil.getObjectSchemaTableName(crudRequest.getTypeToken());
        Query deleteQuery = DataBaseUtil.getDeleteQuery(tableToEdit, crudRequest.getObjectID());
    
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
