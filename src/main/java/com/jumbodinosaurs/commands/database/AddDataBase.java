package com.jumbodinosaurs.commands.database;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.database.DataBase;
import com.jumbodinosaurs.devlib.database.DataBaseManager;
import com.jumbodinosaurs.devlib.database.DataBaseUser;

public class AddDataBase extends DataBaseCommand
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter the database's Name:");
        String dataBaseName = OperatorConsole.getEnsuredAnswer();
        System.out.println("Enter the database's IP:");
        String ip = OperatorConsole.getEnsuredAnswer();
        System.out.println("Enter the database's PORT:");
        String port = OperatorConsole.getEnsuredAnswer();
        System.out.println("Enter the database user's USERNAME:");
        String username = OperatorConsole.getEnsuredAnswer();
        System.out.println("Enter the database user's PASSWORD:");
        String password = OperatorConsole.getEnsuredAnswer();
        
        DataBaseUser user = new DataBaseUser(username, password);
        DataBase dataBase = new DataBase(dataBaseName, ip, port, user);
        boolean success = DataBaseManager.addDataBase(dataBase);
        if(!success)
        {
            return new MessageResponse(dataBaseName + " already exists in the DataBaseManager");
        }
        return new MessageResponse(dataBaseName + " has been added to the DataBaseManager");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Allows The user to add a DataBase to the DataBase Manager";
    }
}
