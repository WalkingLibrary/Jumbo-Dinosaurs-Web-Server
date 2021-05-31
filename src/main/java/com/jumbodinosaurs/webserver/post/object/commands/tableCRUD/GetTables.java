package com.jumbodinosaurs.webserver.post.object.commands.tableCRUD;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.ResponseHeaderUtil;
import com.jumbodinosaurs.webserver.post.object.CRUDCommand;
import com.jumbodinosaurs.webserver.post.object.CRUDRequest;
import com.jumbodinosaurs.webserver.post.object.CRUDUtil;
import com.jumbodinosaurs.webserver.post.object.Table;

import java.lang.reflect.Modifier;
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
         * AuthSession for success and user to determine if it's a "public" query or not
         * Get The Tables
         * Add Tables returned to Response
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        // To Allow for Public Access to Public Tables we Have to check the
        // AuthSession for success to determine if it's a "public" query or not
    
        boolean isPublicQuery = !authSession.isSuccess();
        //Get The Tables
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
    
        //Add Tables returned to Response
        // NOTE: the client needs to have the ID of the Table for manipulation purposes
        // The id Field is Transient and needs to be added to the serialized JSON Objects
        //So we make a special Gson Object
        String jsonApplicationTypeHeader = ResponseHeaderUtil.contentApplicationHeader + "json";
        Gson transientIgnorableGson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.VOLATILE).create();
        response.setMessage200();
        response.addHeaders(jsonApplicationTypeHeader);
        response.setBytesOut(transientIgnorableGson.toJson(tablesToReturn).getBytes());
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
