package com.jumbodinosaurs.webserver.post.object.commands.objectCRUD;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.post.object.*;
import com.jumbodinosaurs.webserver.post.object.exceptions.NoSuchObjectException;

import java.sql.SQLException;

public class UpdateObject extends CRUDCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest postRequest,
                                    AuthSession authSession,
                                    CRUDRequest crudRequest,
                                    Table table)
    {
    
        /*
         * Process for Updating Objects from a Table
         *
         * Check/Verify CRUDRequest Attributes
         * Validate Users Permissions on the Table
         * Parse new and Old Object from Limiter and Object
         * Validate new and old Objects
         * Create Update Query
         * Update the DataBase
         *  */
    
        HTTPResponse response = new HTTPResponse();
    
        //Check/Verify CRUDRequest Attributes
        if(crudRequest.getObjectType() == null || crudRequest.getObject() == null)
        {
            response.setMessage400();
            return response;
        }
    
        PostObject oldObject;
    
        try
        {
            oldObject = CRUDUtil.getObject(crudRequest.getObjectID(), crudRequest.getTypeToken());
        }
        catch(NoSuchObjectException e)
        {
            response.setMessage400();
            return response;
        }
        catch(NoSuchDataBaseException | SQLException | WrongStorageFormatException e)
        {
            response.setMessage500();
            return response;
        }
    
        Table tableToCheck;
    
        try
        {
            tableToCheck = CRUDUtil.getTable(oldObject.getTableID());
        }
        catch(NoSuchObjectException e)
        {
            response.setMessage400();
            return response;
        }
        catch(NoSuchDataBaseException | SQLException | WrongStorageFormatException e)
        {
            response.setMessage500();
            return response;
        }
        //Validate Users Permissions on the Table
        Permission usersPermissions = tableToCheck.getPermissions(authSession.getUser().getUsername());
        if(!usersPermissions.canAdd() || !usersPermissions.canRemove())
        {
            response.setMessage403();
            return response;
        }
    
    
        //Parse the new Object
    
        PostObject newObject;
        
        try
        {
            newObject = new Gson().fromJson(crudRequest.getObject(), crudRequest.getTypeToken().getType());
        }
        catch(JsonSyntaxException e)
        {
            response.setMessage500();
            return response;
        }
    
    
        //Validate new and old Objects
        if(!newObject.isValidObject())
        {
            response.setMessage400();
            return response;
        }
    
    
        //Create Update Query
        String tableToEdit = CRUDUtil.getObjectSchemaTableName(crudRequest.getTypeToken());
        Query updateQuery = DataBaseUtil.getUpdateObjectQuery(tableToEdit, newObject, crudRequest.getObjectID());
    
    
        //Update the DataBase
        try
        {
            CRUDUtil.manipulateObjectDataBase(updateQuery);
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
        return false;
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
