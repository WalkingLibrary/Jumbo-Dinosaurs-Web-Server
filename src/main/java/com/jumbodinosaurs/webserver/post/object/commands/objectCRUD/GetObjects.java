package com.jumbodinosaurs.webserver.post.object.commands.objectCRUD;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.HTTPHeader;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.HeaderUtil;
import com.jumbodinosaurs.webserver.post.object.*;

import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;

public class GetObjects extends CRUDCommand
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
        
        if(crudRequest.getObjectType() == null)
        {
            response.setMessage400();
            return response;
        }
        
        //Validate Users Permissions on the Table
        
        Permission permissions = null;
        if(authSession.getUser() != null)
        {
            permissions = table.getPermissions(authSession.getUser().getUsername());
        }
        
        if(!table.isPublic())
        {
            if(permissions == null)
            {
                response.setMessage403();
                return response;
            }
            
            if(!permissions.canSearch())
            {
                response.setMessage403();
                return response;
            }
            
            if(!authSession.isSuccess())
            {
                response.setMessage403();
                return response;
            }
        }
    
        //Generate Prepared Query from Table Name, Limiter, and Attribute
        String tableToSearch = CRUDUtil.getObjectSchemaTableName(crudRequest.getTypeToken());
    
        String statement = "SELECT * FROM " + tableToSearch;
        statement += " WHERE JSON_EXTRACT(" + DataBaseUtil.objectColumnName + ", ?) LIKE ?";
    
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add("$.tableID");
        parameters.add("" + table.getId());
    
        System.out.println(statement);
    
        if(crudRequest.getLimiter() != null && crudRequest.getAttribute() != null)
        {
            statement += " && JSON_EXTRACT(objectJson, ?) = ?";
            parameters.add(crudRequest.getAttribute());
            parameters.add(crudRequest.getLimiter());
        }
    
        statement += ";";
    
        Query objectQuery = new Query(statement);
        objectQuery.setParameters(parameters);
        
        ArrayList<PostObject> foundObjects;
        
        try
        {
            foundObjects = CRUDUtil.getObjects(objectQuery, crudRequest.getTypeToken());
            objectQuery.getResultSet().close();
            System.out.println(objectQuery.getResultSet().toString());
        }
        catch(NoSuchDataBaseException e)
        {
            response.setMessage501();
            return response;
        }
        catch(SQLException | WrongStorageFormatException e)
        {
            e.printStackTrace();
            response.setMessage500();
            return response;
        }
    
        //By default Json escapes HTML
        // static final boolean DEFAULT_ESCAPE_HTML = true;
        // NOTE: the client needs to have the ID of the Objects for manipulation purposes
        // The id Field is Transient and needs to be added to the serialized JSON Objects
        //So we make a special Gson Object
        Gson transientIgnorableGson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.VOLATILE).create();
        HTTPHeader jsonApplicationTypeHeader = HeaderUtil.contentTypeHeader.setValue("json");
        response.setMessage200();
        response.addHeader(jsonApplicationTypeHeader);
        response.setBytesOut(transientIgnorableGson.toJson(foundObjects).getBytes());
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
