package com.jumbodinosaurs.post.object.commands.objectCRUD;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.object.*;

import java.sql.SQLException;
import java.util.ArrayList;

public class GetObject extends CRUDCommand
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
         * Generate Prepared Query from Table Name, Limiter, and Attribute
         * Return Requested Objects
         *  */
        
        
        HTTPResponse response = new HTTPResponse();
        
        
        //Check/Verify CRUDRequest Attributes
        if(crudRequest.getLimiter() == null || crudRequest.getAttribute() == null)
        {
            response.setMessage400();
            return response;
        }
        
        
        //Validate Users Permissions on the Table

        Permission permissions = table.getPermissions(authSession.getUser().getUsername());
        if (!permissions.canSearch())
        {
            response.setMessage403();
            return response;
        }


        //Generate Prepared Query from Table Name, Limiter, and Attribute
        String tableToEdit = CRUDUtil.getObjectSchemaTableName(table.getObjectType());
        String statement = "SELECT * FROM " + tableToEdit;
        statement += " WHERE JSON_EXTRACT(objectJson, ?) = ?;";
        Query objectQuery = new Query(statement);

        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(table.getName());
        parameters.add(crudRequest.getAttribute());
        parameters.add(crudRequest.getLimiter());

        ArrayList<PostObject> foundObjects;
        try
        {
            foundObjects = CRUDUtil.getObjects(objectQuery, table.getObjectType());
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
        
        response.setMessage200();
        
        //By default Json escapes HTML
        //static final boolean DEFAULT_ESCAPE_HTML = true;
        
        Gson sanitizer = new GsonBuilder().create();
        response.addPayload(sanitizer.toJson(foundObjects));
        
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
