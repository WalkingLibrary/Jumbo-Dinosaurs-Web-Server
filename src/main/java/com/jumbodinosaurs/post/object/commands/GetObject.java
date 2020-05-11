package com.jumbodinosaurs.post.object.commands;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.PostCommand;
import com.jumbodinosaurs.post.object.*;
import com.jumbodinosaurs.post.object.exceptions.NoSuchTableException;

import java.sql.SQLException;
import java.util.ArrayList;

public class GetObject extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /*
         * Process for Getting Objects from a Table
         *
         * Check/Verify PostRequest Attributes
         * Ensure it was a AuthToken Auth or Password Auth
         * Check/Verify ContentObject Attributes
         * Get The Table from the DataBase
         * Validate Users Permissions on the Table
         * Generate Prepared Query from Limiter
         * Return Requested Objects
         *  */
        
        
        HTTPResponse response = new HTTPResponse();
        
        //Check/Verify PostRequest Attributes
        if(request.getContent() == null)
        {
            response.setMessage400();
            return response;
        }
        
        //Ensure it was a AuthToken Auth or Password Auth
        if(!authSession.isPasswordAuth() && !authSession.getTokenUsed().getUse().equals(AuthUtil.authUseName))
        {
            response.setMessage403();
            return response;
        }
        
        
        //ContentObject
        String content = request.getContent();
        
        ContentObject contentObject;
        try
        {
            contentObject = new Gson().fromJson(content, ContentObject.class);
        }
        catch(JsonParseException e)
        {
            response.setMessage400();
            return response;
        }
        
        //Check/Verify ContentObject Attributes
        if(contentObject.getTableName() == null ||
           contentObject.getLimiter() == null ||
           contentObject.getAttribute() == null)
        {
            response.setMessage400();
            return response;
        }
        
        
        //Get The Table from the DataBase
        Table table;
        try
        {
            table = TableManager.getTable(contentObject.getTableName());
        }
        catch(NoSuchTableException e)
        {
            response.setMessage400();
            return response;
        }
        catch(WrongStorageFormatException | SQLException | NoSuchDataBaseException e)
        {
            response.setMessage500();
            return response;
        }
        
        
        //Validate Users Permissions on the Table
        if(!table.isPublic())
        {
            Permission permissions = table.getPermissions(authSession.getUser().getUsername());
            if(!permissions.canSearch())
            {
                response.setMessage403();
                return response;
            }
        }
        
        
        // Generate Prepared Query from Limiter
        String statement = "SELECT * FROM " + table.getName() + " WHERE JSON_EXTRACT(objectJson, ?) = ?;";
        Query objectQuery = new Query(statement);
        
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(contentObject.getAttribute());
        parameters.add(contentObject.getLimiter());
        
        ArrayList<PostObject> foundObjects;
        try
        {
            foundObjects = TableManager.queryTable(objectQuery, table.getObjectType());
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
        response.addPayload(new Gson().toJson(foundObjects));
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
}
