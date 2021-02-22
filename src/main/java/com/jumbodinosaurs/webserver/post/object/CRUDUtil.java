package com.jumbodinosaurs.webserver.post.object;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.devlib.database.DataBase;
import com.jumbodinosaurs.devlib.database.DataBaseManager;
import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.webserver.auth.util.AuthUtil;
import com.jumbodinosaurs.webserver.post.object.exceptions.NoSuchObjectException;
import com.jumbodinosaurs.webserver.post.object.exceptions.NoSuchPostObject;
import com.jumbodinosaurs.webserver.util.OptionUtil;

import java.sql.SQLException;
import java.util.ArrayList;

public class CRUDUtil
{
    //The name for the table that holds table information
    public static String tableTablesName = "tables";
    public static String objectsTableIdColumnName = "tableId";
    private static ArrayList<Class> postObjectClasses = ReflectionUtil.getSubClasses(PostObject.class);
    
    
    public static PostObject getObject(int id, TypeToken<PostObject> typeToken)
            throws NoSuchObjectException, NoSuchDataBaseException, SQLException, WrongStorageFormatException
    {
        String tableName = getObjectSchemaTableName(typeToken);
        String statement = "SELECT * FROM " + tableName;
        statement += " WHERE id=?;";
        Query query = new Query(statement);
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add("" + id);
        query.setParameters(parameters);
        
        ArrayList<PostObject> objectList = DataBaseUtil.getObjectsDataBase(query, getObjectDataBase(), typeToken);
        query.getStatementObject().getConnection().close();
        if(objectList.size() > 1)
        {
            throw new IllegalStateException("More than one Object in " + tableName + " with ID " + id);
        }
        
        if(objectList.size() <= 0)
        {
            throw new NoSuchObjectException("No PostObject with the ID " + id + " in " + tableName);
        }
        
        return objectList.get(0);
    }
    
    //TABLE CRUD
    public static Table getTable(int id)
            throws NoSuchObjectException, SQLException, WrongStorageFormatException, NoSuchDataBaseException
    {
        
        String statement = "SELECT * FROM " + tableTablesName;
        statement += " WHERE id =?;";
        Query query = new Query(statement);
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add("" + id);
        query.setParameters(parameters);
        ArrayList<Table> tablesList;
        
        tablesList = DataBaseUtil.getObjectsDataBase(query, getObjectDataBase(), new TypeToken<Table>()
        {});
        //Don't need to call a close here as getObjectsDataBase Closes the connection for us
        if(tablesList.size() > 1)
        {
            throw new IllegalStateException("More than one Table with ID " + id);
        }
        
        if(tablesList.size() <= 0)
        {
            throw new NoSuchObjectException("No Table with the ID " + id);
        }
        
        return tablesList.get(0);
    }

    public static ArrayList<Table> getTables(String userName)
            throws NoSuchDataBaseException, SQLException, WrongStorageFormatException
    {
        /*
         * Getting Tables from a Username
         *  */
        String statement = "SELECT * FROM " +
                tableTablesName +
                " WHERE JSON_EXTRACT(" +
                DataBaseUtil.objectColumnName +
                ", \"$.permissions\") LIKE \"%\"?\"%\";";
        Query publicTableQuery = new Query(statement);
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(userName);
        publicTableQuery.setParameters(parameters);

        ArrayList<Table> tablesToReturn = DataBaseUtil.getObjectsDataBase(publicTableQuery,
                getObjectDataBase(),
                new TypeToken<Table>()
                {
                });
        return tablesToReturn;
    }

    public static ArrayList<Table> getPublicTables()
            throws NoSuchDataBaseException, SQLException, WrongStorageFormatException
    {

        String statement = "SELECT * FROM " +
                tableTablesName +
                " WHERE JSON_EXTRACT(" +
                DataBaseUtil.objectColumnName +
                ", \"$.isPublic\") = true;";
        Query publicTableQuery = new Query(statement);

        ArrayList<Table> tablesToReturn = DataBaseUtil.getObjectsDataBase(publicTableQuery,
                getObjectDataBase(),
                new TypeToken<Table>()
                {
                });
        return tablesToReturn;
    }

    public static boolean updateTable(Table oldTable, Table newTable)
    {
        Query updateQuery = DataBaseUtil.getUpdateObjectQuery(tableTablesName, oldTable, newTable);
    
        try
        {
            DataBaseUtil.manipulateDataBase(updateQuery, getObjectDataBase());
        }
        catch(SQLException | NoSuchDataBaseException e)
        {
            return false;
        }
    
        int status = updateQuery.getResponseCode();
    
        try
        {
            updateQuery.getStatementObject().getConnection().close();
        }
        catch(SQLException e)
        {
            return status == 1;
        }
    
        if(status > 1)
        {
            throw new IllegalStateException("More than one table effected by update query");
        }
        return status == 1;
    }


    //OBJECT CRUD
    public static <E> ArrayList<E> getObjects(Query query, TypeToken<E> typeToken)
            throws NoSuchDataBaseException, SQLException, WrongStorageFormatException
    {
        return DataBaseUtil.getObjectsDataBase(query, getObjectDataBase(), typeToken);
    }
    
    public static <E> Query getObjectInsertQuery(String table, E object)
    {
        //https://github.com/google/gson/issues/203
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String objectJson = gson.toJson(object);
        String statement = "INSERT INTO " + table + " (" + DataBaseUtil.objectColumnName + ") VALUES( ?);";
        
        Query query = new Query(statement);
        
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(objectJson);
        query.setParameters(parameters);

        return query;
    }


    //ETC
    private static DataBase getObjectDataBase()
            throws NoSuchDataBaseException
    {
        return DataBaseManager.getDataBase(OptionUtil.getServersDataBaseName());
    }
    
    
    public static void manipulateObjectDataBase(Query query)
            throws NoSuchDataBaseException, SQLException
    {
        DataBaseUtil.manipulateDataBase(query, getObjectDataBase());
    }
    
    public static <E> String getObjectSchemaTableName(TypeToken<E> objectType)
    {
        return getObjectSchemaTableName(objectType.getRawType());
    }
    
    public static <E> String getObjectSchemaTableName(Class clazz)
    {
        return clazz.getSimpleName();
    }
    
    public static <E> TypeToken<E> getTypeToken(String objectName)
            throws NoSuchPostObject
    {
        for(Class<E> postObject : postObjectClasses)
        {
            if(postObject.getSimpleName().equals(objectName))
            {
                return TypeToken.get(postObject);
            }
        }
        throw new NoSuchPostObject("No Post Object found with the name " + objectName);
    }
    
    
    public static boolean isValidTableDisplayName(String tableName)
    {
        if (tableName.length() > 15)
        {
            return false;
        }

        if (tableName.length() == 0)
        {
            return false;
        }
    
        char[] usernameArray = tableName.toCharArray();
        for(int i = 0; i < usernameArray.length; i++)
        {
            if(!AuthUtil.generalWhiteListedCharacters.contains("" + usernameArray[i]))
            {
                return false;
            }
        }
        return true;
    }
    
    
    public static ArrayList<Class> getPostObjectClasses()
    {
        return postObjectClasses;
    }
}
