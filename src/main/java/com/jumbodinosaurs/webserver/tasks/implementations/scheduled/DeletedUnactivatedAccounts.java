package com.jumbodinosaurs.webserver.tasks.implementations.scheduled;

import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.devlib.database.DataBase;
import com.jumbodinosaurs.devlib.database.DataBaseManager;
import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.task.ScheduledTask;
import com.jumbodinosaurs.webserver.auth.server.AuthToken;
import com.jumbodinosaurs.webserver.auth.server.User;
import com.jumbodinosaurs.webserver.auth.util.AuthUtil;
import com.jumbodinosaurs.webserver.util.OptionUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DeletedUnactivatedAccounts extends ScheduledTask
{
    public DeletedUnactivatedAccounts(ScheduledThreadPoolExecutor executor)
    {
        super(executor);
    }
    
    @Override
    public int getPeriod()
    {
        return 1;
    }
    
    @Override
    public TimeUnit getTimeUnit()
    {
        return TimeUnit.DAYS;
    }
    
    @Override
    public void run()
    {
        /* Process for deleting unactivated accounts
         * Check that there is a database of users and post is enabled
         * Get users from database
         * Deleted accounts that have been around for more than thirty days and have not been activated
         *
         *
         *  */
        
        //Check that there is a database of users and post is enabled
        if(!OptionUtil.allowPost())
        {
            return;
        }
        
        DataBase userDataBase;
        try
        {
            userDataBase = DataBaseManager.getDataBase(OptionUtil.getServersDataBaseName());
        }
        catch(NoSuchDataBaseException e)
        {
            return;
        }
        
        
        //Get users from database
        String statement = "SELECT * FROM " + AuthUtil.userTableName;
        Query getObjectsQuery = new Query(statement);
        try
        {
            ArrayList<User> users = DataBaseUtil.getObjectsDataBase(getObjectsQuery,
                                                                    userDataBase,
                                                                    new TypeToken<User>() {});
            for(User user : users)
            {
                if(!user.isActive())
                {
                    AuthToken emailToken = user.getToken(AuthUtil.emailActivationUseName);
                    if(emailToken != null && emailToken.hasExpired())
                    {
                        LogManager.consoleLogger.debug("Deleting " + user.getUsername());
                        if(!AuthUtil.deleteUser(user.getId()))
                        {
                            LogManager.consoleLogger.error("Error Deleting " + user.getUsername());
                        }
                    }
                }
            }
            
        }
        catch(SQLException e)
        {
            LogManager.consoleLogger.warn(e.getMessage());
        }
        catch(WrongStorageFormatException e)
        {
            LogManager.consoleLogger.error(e.getMessage());
        }
    }
}
