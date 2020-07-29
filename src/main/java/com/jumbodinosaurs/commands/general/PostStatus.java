package com.jumbodinosaurs.commands.general;

import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.devlib.commands.Command;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.database.DataBase;
import com.jumbodinosaurs.devlib.database.DataBaseManager;
import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.email.EmailManager;
import com.jumbodinosaurs.devlib.email.NoSuchEmailException;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.Domain;
import com.jumbodinosaurs.post.object.CRUDUtil;
import com.jumbodinosaurs.post.object.PostObject;
import com.jumbodinosaurs.util.OptionUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PostStatus extends Command
{
    @Override
    public MessageResponse getExecutedMessage()
            throws WaveringParametersException
    {
        /*
         * Process for displaying Post abilities on the Server
         * Display Domain and Captcha Key Status
         * Display Default Database Status
         * Display Available tables vs needed tables
         * Display Default Email Status
         * Display Allow Post Status
         *  */
        
        String returnMessage = "\n";
        
        //Display Domain and Captcha Key Status
        if(DomainManager.getDomains().size() <= 0)
        {
            returnMessage += "No Domains in Service.\n\n";
        }
        else
        {
            String domainsWithFunctioningCaptchaKeys = "";
            for(Domain domain : DomainManager.getDomains())
            {
                if(domain.getCaptchaKey() != null)
                {
                    domainsWithFunctioningCaptchaKeys += domain.getDomain() + "\n";
                }
            }
            if(domainsWithFunctioningCaptchaKeys.equals(""))
            {
                returnMessage += "No Domains with Functioning Captcha Keys";
            }
            else
            {
                returnMessage += "Domains with Functioning Captcha Keys:\n" + domainsWithFunctioningCaptchaKeys;
            }
            returnMessage += "\n\n";
        }
        
        //Display Default Database Status
        DataBase dataBase = null;
        try
        {
            dataBase = DataBaseManager.getDataBase(OptionUtil.getServersDataBaseName());
            returnMessage += OptionUtil.getServersDataBaseName() + " was found in the DatabaseManager\n\n";
        }
        catch(NoSuchDataBaseException e)
        {
            returnMessage += "Servers Database Name Option (" +
                             OptionUtil.getServersDataBaseName() +
                             ") could not be" +
                             " found in the DatabaseManager\n\n";
        }
        
        
        //Display Available tables vs needed tables
        if(dataBase != null)
        {
            String getTablesStatement = "show tables;";
            Query query = new Query(getTablesStatement);
            try
            {
                DataBaseUtil.queryDataBase(query, dataBase);
                ResultSet resultSet = query.getResultSet();
                ArrayList<String> availableTables = new ArrayList<String>();
                while(resultSet.next())
                {
                    availableTables.add(resultSet.getString("Tables_in_" + dataBase.getDataBaseName()));
                }
                returnMessage += "Available Tables:\n";
                for(String tableName : availableTables)
                {
                    returnMessage += tableName + "\n";
                }
                
                returnMessage += "\n";
                
                //NEEDED TABLES
                ArrayList<String> neededTables = new ArrayList<String>();
                neededTables.add(AuthUtil.userTableName);
                neededTables.add(CRUDUtil.tableTablesName);
                for(Class postObject : ReflectionUtil.getSubClasses(PostObject.class))
                {
                    neededTables.add(postObject.getSimpleName());
                }
                
                
                //Missing Tables
                ArrayList<String> missingTables = new ArrayList<String>();
                for(int i = 0; i < neededTables.size(); i++)
                {
                    if(!availableTables.contains(neededTables.get(i)))
                    {
                        missingTables.add(neededTables.get(i));
                    }
                }
                
                returnMessage += "Missing Tables:\n";
                for(String missingTable : missingTables)
                {
                    returnMessage += missingTable + "\n";
                }
                returnMessage += "\n";
            }
            catch(SQLException e)
            {
                returnMessage += "Error Querying " + dataBase.getDataBaseName() + ":\n" + e.getMessage();
            }
        }
        
        
        //Display Default Email Status
        try
        {
            EmailManager.getEmail(OptionUtil.getDefaultEmail());
            returnMessage += OptionUtil.getDefaultEmail() + " was found in EmailManager\n\n";
        }
        catch(NoSuchEmailException e)
        {
            returnMessage += "Servers Email Name Option (" +
                             OptionUtil.getDefaultEmail() +
                             ") could not be found in " +
                             "the EmailManager\n\n";
        }
        
        //Display Allow Post Status
        returnMessage += "Post is Enabled: " + OptionUtil.allowPost();
        return new MessageResponse(returnMessage);
        
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Gives a report of all the variables that are needed for post";
    }
}
