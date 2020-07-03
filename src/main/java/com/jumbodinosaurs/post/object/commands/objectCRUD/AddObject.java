package com.jumbodinosaurs.post.object.commands.objectCRUD;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.object.*;

import java.sql.SQLException;

public class AddObject extends CRUDCommand
{
    
    @Override
    public HTTPResponse getResponse(PostRequest postRequest,
                                    AuthSession authSession,
                                    CRUDRequest crudRequest,
                                    Table table)
    {
        /* Process for Adding an object to a Table
         *
         *
         *
         * Check/Verify CRUDRequest Attributes
         * Validate Users Permissions on the Table
         * Parse Object
         * Validate the Object given
         * Add the object to DataBase
         *
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
        if(!permissions.canAdd())
        {
            response.setMessage403();
            return response;
        }
        
        
        //Parse Object
        String objectJson = crudRequest.getObject();
        
        PostObject postObject;
        try
        {
            postObject = new Gson().fromJson(objectJson, table.getObjectType().getType());
        }
        catch(JsonSyntaxException e)
        {
            response.setMessage400();
            return response;
        }

        //Validate the Object given
        if (!postObject.isValidObject())
        {
            response.setMessage400();
            return response;
        }


        //Add the object to the DataBase
        String tableToEdit = CRUDUtil.getObjectSchemaTableName(table.getObjectType());
        Query insertQuery = CRUDUtil.getObjectInsertQuery(tableToEdit, postObject, table.getId());

        try
        {
            CRUDUtil.manipulateObjectDataBase(insertQuery);
        }
        catch (NoSuchDataBaseException e)
        {
            response.setMessage501();
            return response;
        }
        catch(SQLException e)
        {
            response.setMessage500();
            return response;
        }
        
        if(insertQuery.getResponseCode() > 1)
        {
            throw new IllegalStateException("Query Manipulated more than one row " + insertQuery.toString());
        }
        
        if(insertQuery.getResponseCode() == 0)
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
        return false;
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
