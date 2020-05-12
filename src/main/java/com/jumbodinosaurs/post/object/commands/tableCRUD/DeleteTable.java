package com.jumbodinosaurs.post.object.commands.tableCRUD;

import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.object.*;

import java.sql.SQLException;
import java.util.ArrayList;

public class DeleteTable extends CRUDCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest postRequest,
                                    AuthSession authSession,
                                    CRUDRequest crudRequest,
                                    Table table)
    {
        /* Process for Deleting a Table
         *
         * Check/Verify CRUDRequest Attributes
         * Check Users Permissions
         * Create Drop Query for DataBase
         * Create Delete Query for Table Tables
         * Manipulate Database
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        //Check Users Permissions
        Permission usersPermissions = table.getPermissions(authSession.getUser().getUsername());
        
        if(!usersPermissions.hasAdminPerms())
        {
            response.setMessage403();
            return response;
        }
        
        
        //Create Drop Query for DataBase
        String dropStatement = "DROP ?;";
        Query dropQuery = new Query(dropStatement);
        
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(table.getName());
        dropQuery.setParameters(parameters);
        
        //Create Delete Query for Table Tables
        Query removeStatement = DataBaseUtil.getDeleteQuery(table.getName(), table);
        
        //Manipulate Database
        try
        {
            CRUDUtil.manipulateTableDataBase(dropQuery);
            CRUDUtil.manipulateTableDataBase(removeStatement);
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
        return true;
    }
    
    @Override
    public boolean requiresUser()
    {
        return true;
    }
}
