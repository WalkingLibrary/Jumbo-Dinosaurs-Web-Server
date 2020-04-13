package com.jumbodinosaurs.database;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.database.exceptions.WrongStorageFormatException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DataBaseUtil
{
    public static final String objectColumnName = "objectJson";
    
    public static void queryDataBase(Query query, DataBase dataBase) throws SQLException
    {
        Connection dataBaseConnection = dataBase.getConnection();
        Statement statement = dataBaseConnection.createStatement();
        query.setResultSet(statement.executeQuery(query.getQuery()));
    }
    
    public static void manipulateDataBase(Query query, DataBase dataBase) throws SQLException
    {
        Connection dataBaseConnection = dataBase.getConnection();
        Statement statement = dataBaseConnection.createStatement();
        query.setResponseCode(statement.executeUpdate(query.getQuery()));
    }
    
    public static <E> ArrayList<E> getObjectsDataBase(Query query,
                                                      DataBase dataBase,
                                                      TypeToken<E> typeToken) throws SQLException,
                                                                                             WrongStorageFormatException
    {
        queryDataBase(query, dataBase);
        ArrayList<E> objects = new ArrayList<E>();
        ResultSet queryResult = query.getResultSet();
        while(queryResult.next())
        {
            try
            {
                String objectJson = queryResult.getString(objectColumnName);
                objects.add(new Gson().fromJson(objectJson, typeToken.getType()));
            }
            catch(SQLException | JsonParseException e)
            {
                throw new WrongStorageFormatException(e.getMessage());
            }
        }
        return objects;
    }
    
    public static <E> Query getUpdateObjectQuery(String table, E objectOld, E objectNew)
    {
        String oldObjectJson = new Gson().toJson(objectOld);
        String newObjectJson = new Gson().toJson(objectNew);
        String statement = "UPDATE " + table;
        statement += " SET " + objectColumnName + " = \'" + newObjectJson + "\'";
        statement += " WHERE " + objectColumnName + " = CAST(\'" + oldObjectJson + "\' AS JSON);";
        return new Query(statement);
    }
    
    public static <E> Query getInsertQuery(String table, E object)
    {
        String objectJson = new Gson().toJson(object);
        String statement = "INSERT INTO " + table + "(" + objectColumnName + ") VALUES(\'" + objectJson + "\');";
        System.out.println(statement);
        return new Query(statement);
    }
}
