package com.jumbodinosaurs.webserver.post.object.commands.tableCRUD;

import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.post.object.*;

import java.sql.SQLException;

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
         * Create and Execute Delete Queries
         *
         *  */
    
        //Check/Verify CRUDRequest Attributes
        HTTPResponse response = new HTTPResponse();
    
        //Check Users Permissions
        Permission usersPermissions = table.getPermissions(authSession.getUser().getUsername());
    
        if(!usersPermissions.hasAdminPerms())
        {
            response.setMessage403();
            return response;
        }
    
    
        /* Creating and Executing Delete Queries
         *
         * Create Delete Table from Tables table Query
         * Manipulate Database
         *
         *  */
    
        //Create Delete Table from Tables table Query
        Query deleteTableQuery = DataBaseUtil.getDeleteQuery(CRUDUtil.tableTablesName, table.getId());
    
    
        //Manipulate Database
        try
        {
            CRUDUtil.manipulateObjectDataBase(deleteTableQuery);
            deleteTableQuery.getStatementObject().getConnection().close();
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
    
    
        /* TODO Rather than remove objects based on each delete request we could schedule task
            that would send a query that would delete all objects missing a table
        
        
        
        
        
         * Delete all Objects from the Object Table if they belong to the table being deleted
         *
        String deleteObjectsStatement = "DELETE FROM " + objectsTableName;
        deleteObjectsStatement += " WHERE " + CRUDUtil.objectsTableIdColumnName;
        deleteObjectsStatement += " = " + table.getId();
        Query removeObjectsStatement = new Query(deleteObjectsStatement);
        
        try
        {
            CRUDUtil.manipulateObjectDataBase(removeObjectsStatement);
        }
        catch(NoSuchDataBaseException e)
        {
            response.setMessage501();
            return response;
        }
        catch(SQLException e)
        {
            response.setMessage500();
            LogManager.consoleLogger.warn("A table was deleted but the objects may not have been");
            LogManager.consoleLogger.error(e.getMessage(), e);
            return response;
        }
       
         */
    
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
