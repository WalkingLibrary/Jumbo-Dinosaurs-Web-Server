package com.jumbodinosaurs.post.object.commands.tableCRUD;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.object.*;

import java.util.HashMap;

public class UpdateTable extends CRUDCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest postRequest,
                                    AuthSession authSession,
                                    CRUDRequest crudRequest,
                                    Table table)
    {
        /* Process for updating a Table
         *
         * Check/Verify CRUDRequest Attributes
         * Verify Users Permissions on the Table
         * Parse New Table
         * Validate The New Table
         * Update the table in the Database
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        //Check/Verify CRUDRequest Attributes
        //Note: the new Table should be in the object attribute of the CrudRequest
        if(crudRequest.getObject() == null)
        {
            response.setMessage400();
            return response;
        }
        
        
        //Verify Users Permissions on the Table
        Permission usersPermissions = table.getPermissions(authSession.getUser().getUsername());
        
        if(!usersPermissions.hasAdminPerms())
        {
            response.setMessage403();
            return response;
        }
        
        //Parse New Table
        String tableJson = crudRequest.getObject();
        Table newTable;
        try
        {
            newTable = new Gson().fromJson(tableJson, Table.class);
        }
        catch(JsonSyntaxException e)
        {
            response.setMessage400();
            return response;
        }
        
        
        /* Validate The New Table
         *
         * Check the New Name of the Table
         * Validate The Creator
         * Change Type to current Tables Type
         * Make Sure Some one still has Admin Perms
         *
         *
         */
        
        
        //Check the New Name of the Table
        if(!CRUDUtil.isValidTableDisplayName(newTable.getName()))
        {
            response.setMessage400();
            return response;
        }
        
        
        //Validate The Creator
        if(!table.getCreator().equals(newTable.getCreator()))
        {
            response.setMessage400();
            return response;
        }
        
        
        boolean doesSomeoneHaveAdminPerms = false;
        
        HashMap<String, Permission> permissionHashMap = newTable.getPermissions();
        //Make Sure Some one still has Admin Perms
        
        //Limit Permissions List Size
        if(permissionHashMap.keySet().size() > 50)
        {
            response.setMessage400();
            return response;
        }
        
        for(String userName : permissionHashMap.keySet())
        {
            Permission usersPerms = permissionHashMap.get(userName);
            if(!AuthUtil.isValidUsername(userName))
            {
                response.setMessage400();
                return response;
            }
            
            if(usersPerms.hasAdminPerms())
            {
                doesSomeoneHaveAdminPerms = true;
            }
        }
        
        if(!doesSomeoneHaveAdminPerms)
        {
            response.setMessage400();
            return response;
        }
        
        
        //Update the table in the Database
        if(!CRUDUtil.updateTable(table, newTable))
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
