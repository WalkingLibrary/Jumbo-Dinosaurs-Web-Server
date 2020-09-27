package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.database.DataBase;
import com.jumbodinosaurs.devlib.database.DataBaseManager;
import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.log.LogManager;
import com.jumbodinosaurs.post.object.CRUDUtil;
import com.jumbodinosaurs.post.object.PostObject;
import com.jumbodinosaurs.util.OptionUtil;

import java.sql.SQLException;
import java.util.ArrayList;

public class CreateObjectTables extends StartUpTask
{
    public CreateObjectTables()
    {
        super(Phase.PostInitialization);
    }
    
    @Override
    public void run()
    {
        /*
         * Process for creating each needed table for the Post System
         * Get/Check for Server DataBase
         * Create a table for the tables
         * Create a table for each PostObject
         *
         *
         *  */
        ArrayList<Class> postObjects = ReflectionUtil.getSubClasses(PostObject.class);
        try
        {
            //Get/Check for Server DataBase
            DataBase serversDataBase = DataBaseManager.getDataBase(OptionUtil.getServersDataBaseName());
            if(serversDataBase == null)
            {
                LogManager.consoleLogger.warn("No Data Base Set");
                return;
            }
            
            
            //Create a table for the tables
            String statement = "create table IF NOT EXISTS %s (" +
                               "id int primary key auto_increment," +
                               "objectJson json not null" +
                               ");";
            
            String createTableTablesStatement = String.format(statement, CRUDUtil.tableTablesName);
            Query createTableTablesQuery = new Query(createTableTablesStatement);
            try
            {
                DataBaseUtil.manipulateDataBase(createTableTablesQuery, serversDataBase);
            }
            catch(SQLException e)
            {
                LogManager.consoleLogger.error(e.getMessage());
            }
            
            
            //Create a table for each PostObject
            for(Class clazz : postObjects)
            {
                
                String createTableStatement = String.format(statement, clazz.getSimpleName());
                Query query = new Query(createTableStatement);
                try
                {
                    DataBaseUtil.manipulateDataBase(query, serversDataBase);
                }
                catch(SQLException e)
                {
                    LogManager.consoleLogger.error(e.getMessage());
                }
            }
        }
        catch(NoSuchDataBaseException e)
        {
            LogManager.consoleLogger.error("Unable To Confirm Object Tables Exists: " + e.getMessage());
        }
        
    }
}
