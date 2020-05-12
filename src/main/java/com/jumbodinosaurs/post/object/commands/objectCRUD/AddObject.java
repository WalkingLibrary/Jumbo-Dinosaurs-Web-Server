package com.jumbodinosaurs.post.object.commands.objectCRUD;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.database.DataBase;
import com.jumbodinosaurs.devlib.database.DataBaseManager;
import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.PostCommand;
import com.jumbodinosaurs.post.object.*;
import com.jumbodinosaurs.post.object.exceptions.NoSuchTableException;
import com.jumbodinosaurs.util.OptionUtil;

import java.sql.SQLException;

public class AddObject extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /* Process for Adding an object to a Table
         *
         *
         * Check/Verify PostRequest Attributes
         * Ensure it was a AuthToken Auth or Password Auth
         * Check/Verify ContentObject Attributes
         * Get The Table from the DataBase
         * Validate Users Permissions on the Table
         * Validate the Object given
         * Add the object to DataBase
         *
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
    
        CRUDRequest CRUDRequest;
        try
        {
            CRUDRequest = new Gson().fromJson(content, CRUDRequest.class);
        }
        catch(JsonParseException e)
        {
            response.setMessage400();
            return response;
        }
        
        //Check/Verify ContentObject Attributes
        if(CRUDRequest.getTableName() == null || CRUDRequest.getObject() == null)
        {
            response.setMessage400();
            return response;
        }
        
        //Get The Table from the DataBase
        Table table;
        try
        {
            table = CRUDUtil.getTable(CRUDRequest.getTableName());
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
            if(!permissions.canAdd())
            {
                response.setMessage403();
                return response;
            }
        }
        
        //Validate the Object given
        String objectJson = CRUDRequest.getObject();
        
        PostObject postObject;
        try
        {
            postObject = new Gson().fromJson(objectJson, table.getObjectType().getType());
        }
        catch(JsonParseException e)
        {
            response.setMessage400();
            return response;
        }
        
        if(!postObject.isValidObject())
        {
            response.setMessage400();
            return response;
        }
        
        
        //Add the object to the DataBase
        Query insertQuery = DataBaseUtil.getInsertQuery(table.getName(), postObject);
        DataBase objectDataBase;
        try
        {
            objectDataBase = DataBaseManager.getDataBase(OptionUtil.getServersDataBaseName());
        }
        catch(NoSuchDataBaseException e)
        {
            response.setMessage501();
            return response;
        }
        
        try
        {
            DataBaseUtil.manipulateDataBase(insertQuery, objectDataBase);
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
}
