package com.jumbodinosaurs.webserver.commands.database;

import com.jumbodinosaurs.devlib.commands.MessageResponse;
import com.jumbodinosaurs.devlib.commands.exceptions.WaveringParametersException;
import com.jumbodinosaurs.devlib.database.DataBaseManager;
import com.jumbodinosaurs.webserver.commands.OperatorConsole;

public class DeleteDataBase extends DataBaseCommand
{
    @Override
    public MessageResponse getExecutedMessage() throws WaveringParametersException
    {
        System.out.println("Enter the database's name:");
        String dataBaseName = OperatorConsole.getEnsuredAnswer();
        boolean success = DataBaseManager.removeDataBase(dataBaseName);
        if(!success)
        {
            return new MessageResponse("No DataBase named " + dataBaseName + " in the DataBaseManager");
        }
        return new MessageResponse("Successfully removed " + dataBaseName + "from the DataBase Manager");
    }
    
    @Override
    public String getHelpMessage()
    {
        return "Allows the user to delete a DataBase from the DataBaseManager";
    }
}
