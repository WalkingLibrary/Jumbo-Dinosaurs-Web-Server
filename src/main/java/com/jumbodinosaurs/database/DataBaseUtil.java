package com.jumbodinosaurs.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseUtil
{
    
    public static void queryDataBase(Query query, DataBase dataBase) throws SQLException
    {
        Connection dataBaseConnection = dataBase.getConnection();
        Statement statement = dataBaseConnection.createStatement();
        query.setResultSet(statement.executeQuery(query.getQuery()));
    }
}
