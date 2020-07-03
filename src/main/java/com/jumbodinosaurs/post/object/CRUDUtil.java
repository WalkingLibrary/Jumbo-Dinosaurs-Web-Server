package com.jumbodinosaurs.post.object;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    public static String tableTablesName = "tables";
    public static String objectsTableIdColumnName = "tableId";


    //TABLE CRUD


    public static Table getTable(int id)
            throws NoSuchTableException, SQLException, WrongStorageFormatException, NoSuchDataBaseException
    {

        String statement = "SELECT * FROM " + tableTablesName;
        statement += " WHERE id =?;";
        Query query = new Query(statement);
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add("" + id);
        query.setParameters(parameters);
        ArrayList<Table> tables;

        tables = DataBaseUtil.getObjectsDataBase(query, getObjectDataBase(), new TypeToken<Table>()
        {
        });


        if (tables.size() > 1)
        {
            throw new IllegalStateException("More than one Table with ID " + id);
        }

        if (tables.size() == 0)
        {
            throw new NoSuchTableException("No Table with the ID " + id);
        }
        Table requestedTable = tables.get(0);

        requestedTable.setId(query.getResultSet().getInt("id"));

        return requestedTable;
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

        /*Add Table Id's To The returned Tables*/
        publicTableQuery.getResultSet().beforeFirst();
        for (Table table : tablesToReturn)
        {
            table.setId(publicTableQuery.getResultSet().getInt("id"));

            if (!publicTableQuery.getResultSet().next())
            {
                throw new IllegalStateException("More Tables than IDs");
            }
        }
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
        /*Add Table Id's To The returned Tables*/
        publicTableQuery.getResultSet().beforeFirst();
        for (Table table : tablesToReturn)
        {
            table.setId(publicTableQuery.getResultSet().getInt("id"));

            if (!publicTableQuery.getResultSet().next())
            {
                throw new IllegalStateException("More Tables than IDs");
            }
        }
        return tablesToReturn;
    }

    public static boolean updateTable(Table oldTable, Table newTable)
    {
        Query updateQuery = DataBaseUtil.getUpdateObjectQuery(tableTablesName, oldTable, newTable);

        try
        {
            DataBaseUtil.manipulateDataBase(updateQuery, getObjectDataBase());
        }
        catch (SQLException | NoSuchDataBaseException e)
        {
            return false;
        }

        int status = updateQuery.getResponseCode();
        if (status > 1)
        {
            throw new IllegalStateException("More than one table effected by update query");
        }
        return status == 1;
    }


    //OBJECT CRUD
    public static ArrayList<PostObject> getObjects(Query query, TypeToken typeToken)
            throws NoSuchDataBaseException, SQLException, WrongStorageFormatException
    {
        return DataBaseUtil.getObjectsDataBase(query, getObjectDataBase(), typeToken);
    }

    public static <E> Query getObjectInsertQuery(String table, E object, int tableID)
    {
        //https://github.com/google/gson/issues/203
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String objectJson = gson.toJson(object);
        String statement = "INSERT INTO " + table + " (" + DataBaseUtil.objectColumnName + ", " +
                objectsTableIdColumnName + ") VALUES(?, ?);";

        Query query = new Query(statement);

        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(objectJson);
        parameters.add(tableID + "");
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
        return objectType.getClass().getCanonicalName();
    }

    public static <E> TypeToken<E> getTypeToken(String objectName)
            throws NoSuchPostObject
    {
        for (Class postObject : ReflectionUtil.getSubClasses(PostObject.class))
        {
            if (postObject.getSimpleName().equals(objectName))
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
        for (int i = 0; i < usernameArray.length; i++)
        {
            if (!AuthUtil.generalWhiteListedCharacters.contains("" + usernameArray[i]))
            {
                return false;
            }
        }
        return true;
    }


}
