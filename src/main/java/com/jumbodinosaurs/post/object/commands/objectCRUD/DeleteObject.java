package com.jumbodinosaurs.post.object.commands.objectCRUD;

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
import com.jumbodinosaurs.post.object.CRUDRequest;
import com.jumbodinosaurs.post.object.CRUDUtil;
import com.jumbodinosaurs.post.object.Permission;
import com.jumbodinosaurs.post.object.Table;
import com.jumbodinosaurs.post.object.exceptions.NoSuchTableException;

import java.sql.SQLException;
import java.util.ArrayList;

public class DeleteObject extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /*
         * Process for Removing an object from the database
         *
         * Check/Verify PostRequest Attributes
         * Ensure it was a AuthToken Auth or Password Auth
         * Check/Verify ContentObject Attributes
         * Get The Table from the DataBase
         * Validate Users Permissions on the Table
         * Prepare Query
         * Remove object by id
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
        if(CRUDRequest.getTableName() == null || CRUDRequest.getId() <= 0)
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
            if(!permissions.canRemove())
            {
                response.setMessage403();
                return response;
            }
        }
        
        //Prepare Query
        //Remove object by id
        String statement = "DELETE FROM " + table.getName() + "WHERE id = ?";
        Query deleteQuery = new Query(statement);
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add("" + CRUDRequest.getId());
        
        deleteQuery.setParameters(parameters);
        try
        {
            CRUDUtil.manipulateTableDataBase(deleteQuery);
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
