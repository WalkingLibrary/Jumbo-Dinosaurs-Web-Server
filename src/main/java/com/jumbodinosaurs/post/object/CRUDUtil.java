package com.jumbodinosaurs.post.object;

import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.database.DataBase;
import com.jumbodinosaurs.devlib.database.DataBaseManager;
import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.post.object.exceptions.NoSuchPostObject;
import com.jumbodinosaurs.post.object.exceptions.NoSuchTableException;
import com.jumbodinosaurs.util.OptionUtil;

import java.sql.SQLException;
import java.util.ArrayList;

public class CRUDUtil
{
    //The name for the table that holds table information
    private static String tableTablesName = "tables";
    
    
    //TABLE CRUD
    
    
    public static boolean addTable(Table tableToAdd)
    {
        Query insertQuery = DataBaseUtil.getInsertQuery(tableTablesName, tableToAdd);
        try
        {
            DataBaseUtil.manipulateDataBase(insertQuery, getTableDataBase());
            return true;
        }
        catch(SQLException | NoSuchDataBaseException e)
        {
            return false;
        }
    }
    
    public static Table getTable(String tableName)
            throws NoSuchDataBaseException, WrongStorageFormatException, NoSuchTableException, SQLException
    {
        return getTable(getTableDataBase(), tableName);
    }
    
    public static Table getTable(DataBase dataBase, String tableName)
            throws NoSuchTableException, SQLException, WrongStorageFormatException
    {
        if(!isValidTableName(tableName))
        {
            throw new NoSuchTableException("Table Name Given was not valid");
        }
        String statement = "SELECT * FROM " + tableTablesName;
        statement += " WHERE JSON_EXTRACT(" + DataBaseUtil.objectColumnName + ", \"$.name\") =?;";
        Query query = new Query(statement);
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(tableName);
        query.setParameters(parameters);
        
        ArrayList<Table> tables;
        
        tables = DataBaseUtil.getObjectsDataBase(query, dataBase, new TypeToken<Table>() {});
        
        
        if(tables.size() > 1)
        {
            throw new IllegalStateException("More than one Table Named " + tableName);
        }
        
        if(tables.size() == 0)
        {
            throw new NoSuchTableException("No Table Found with the name " + tableName);
        }
        
        return tables.get(0);
    }
    
    public static boolean updateTable(Table oldTable, Table newTable)
    {
        Query updateQuery = DataBaseUtil.getUpdateObjectQuery(tableTablesName, oldTable, newTable);
        
        try
        {
            DataBaseUtil.manipulateDataBase(updateQuery, getTableDataBase());
        }
        catch(SQLException | NoSuchDataBaseException e)
        {
            return false;
        }
        
        int status = updateQuery.getResponseCode();
        if(status > 1)
        {
            throw new IllegalStateException("More than one table effected by update query");
        }
        return status == 1;
    }
    
    
    //OBJECT CRUD
    public static ArrayList<PostObject> getObjects(Query query, TypeToken typeToken)
            throws NoSuchDataBaseException, SQLException, WrongStorageFormatException
    {
        return DataBaseUtil.getObjectsDataBase(query, getTableDataBase(), typeToken);
    }
    
    
    //ETC
    private static DataBase getTableDataBase()
            throws NoSuchDataBaseException
    {
        return DataBaseManager.getDataBase(OptionUtil.getServersDataBaseName());
    }
    
    
    public static void manipulateTableDataBase(Query query)
            throws NoSuchDataBaseException, SQLException
    {
        DataBaseUtil.manipulateDataBase(query, getTableDataBase());
    }
    
    
    public static <E> TypeToken<E> getTypeToken(String objectName)
            throws NoSuchPostObject
    {
        for(Class postObject : ReflectionUtil.getSubClasses(PostObject.class))
        {
            if(postObject.getSimpleName().equals(objectName))
            {
                return TypeToken.get(postObject);
            }
        }
        throw new NoSuchPostObject("No Post Object found with the name " + objectName);
    }
    
    
    public static boolean isValidTableName(String name)
    {
        if(name.equals(tableTablesName) || name.equals(AuthUtil.userTableName))
        {
            return false;
        }
        
        if(name.length() > 15)
        {
            return false;
        }
        
        char[] usernameArray = name.toCharArray();
        for(int i = 0; i < usernameArray.length; i++)
        {
            if(!AuthUtil.generalWhiteListedCharacters.contains("" + usernameArray[i]))
            {
                return false;
            }
        }
        
        return true;
    }
    
    
    public static boolean isTableNameTaken(String name)
    {
        try
        {
            getTable(getTableDataBase(), name);
            return true;
        }
        catch(NoSuchTableException e)
        {
            return false;
        }
        catch(SQLException | WrongStorageFormatException | NoSuchDataBaseException e)
        {
            return true;
        }
    }
    
    
}
