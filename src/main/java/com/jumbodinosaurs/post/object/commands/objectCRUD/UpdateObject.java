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

public class UpdateObject extends CRUDCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest postRequest,
                                    AuthSession authSession,
                                    CRUDRequest crudRequest,
                                    Table table)
    {
        
        /*
         * Process for Getting Objects from a Table
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
        if(crudRequest.getObject() == null || crudRequest.getLimiter() == null)
        {
            response.setMessage400();
            return response;
        }
        
        
        //Validate Users Permissions on the Table
        Permission usersPermissions = table.getPermissions(authSession.getUser().getUsername());
        if(!usersPermissions.canAdd() || !usersPermissions.canRemove())
        {
            response.setMessage403();
            return response;
        }
        
        
        //Parse new and Old Object from Limiter and Object
        //Note: the new Object should be in the Object Attribute and the Old Object in the Limiter Attribute
        
        PostObject oldObject, newObject;
        
        try
        {
            oldObject = new Gson().fromJson(crudRequest.getLimiter(), table.getObjectType().getType());
            newObject = new Gson().fromJson(crudRequest.getObject(), table.getObjectType().getType());
        }
        catch(JsonSyntaxException e)
        {
            response.setMessage500();
            return response;
        }
        
        
        //Validate new and old Objects
        if(!oldObject.isValidObject() || !newObject.isValidObject())
        {
            response.setMessage400();
            return response;
        }
        
        
        //Create Update Query
        Query updateQuery = DataBaseUtil.getUpdateObjectQuery(table.getName(), oldObject, newObject);
        
        
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
