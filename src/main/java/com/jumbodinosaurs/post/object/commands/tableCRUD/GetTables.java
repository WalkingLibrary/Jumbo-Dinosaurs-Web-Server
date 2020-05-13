package com.jumbodinosaurs.post.object.commands.tableCRUD;

import com.google.gson.Gson;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.object.CRUDCommand;
import com.jumbodinosaurs.post.object.CRUDRequest;
import com.jumbodinosaurs.post.object.CRUDUtil;
import com.jumbodinosaurs.post.object.Table;

import java.sql.SQLException;
import java.util.ArrayList;

public class GetTables extends CRUDCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest postRequest,
                                    AuthSession authSession,
                                    CRUDRequest crudRequest,
                                    Table table)
    {
        /* Process for Getting a Users Table
         *
         *
         * To Allow for Public Access to Public Tables we Have to check the
         * AuthSession for success and user to determine if it's a "public" query only
         * Prepare query Statement
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        // To Allow for Public Access to Public Tables we Have to check the
        // AuthSession for success to determine if it's a "public" query or not
        
        boolean isPublicQuery = authSession.isSuccess();
        
        ArrayList<Table> tablesToReturn;
        try
        {
            if(isPublicQuery)
            {
                tablesToReturn = CRUDUtil.getPublicTables();
            }
            else
            {
                
                tablesToReturn = CRUDUtil.getTables(authSession.getUser().getUsername());
            }
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
        response.addPayload(new Gson().toJson(tablesToReturn));
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
