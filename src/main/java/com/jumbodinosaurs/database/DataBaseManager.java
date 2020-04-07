package com.jumbodinosaurs.database;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.json.GsonUtil;
import com.jumbodinosaurs.devlib.util.GeneralUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataBaseManager
{
    private static File databaseDir;
    private static File dataBaseMemory;
    private static ArrayList<DataBase> dataBases;
    
    public static void initializeDataBases(File parentFile)
    {
        databaseDir = GeneralUtil.checkFor(parentFile, "DataBases");
        dataBaseMemory = GeneralUtil.checkFor(databaseDir, "databases.json");
        loadDataBases();
        if(dataBases == null)
        {
            dataBases = new ArrayList<DataBase>();
        }
        saveDataBases();
    }
    
    private static void loadDataBases()
    {
        try
        {
            dataBases = GsonUtil.readList(dataBaseMemory,
                                          DataBase.class,
                                          new TypeToken<ArrayList<DataBase>>() {},
                                          false);
        }
        catch(JsonParseException e)
        {
            e.printStackTrace();
            throw new IllegalStateException("Data Base Data is not Loadable");
        }
    }
    
    public static void saveDataBases()
    {
        saveDataBases(dataBases);
    }
    
    public static void saveDataBases(ArrayList<DataBase> dataBases)
    {
        Type typeToken = new TypeToken<DataBase>() {}.getType();
        String dataBasesJsonized = new Gson().toJson(dataBases, typeToken);
        GeneralUtil.writeContents(dataBaseMemory, dataBasesJsonized, false);
    }
    
    /*returns True if the DataBase Was added*/
    public static boolean addDataBase(DataBase dataBase)
    {
        for(DataBase dataBaseStored : dataBases)
        {
            if(dataBaseStored.getDataBaseName().equals(dataBase.getDataBaseName()))
            {
                return false;
            }
        }
        dataBases.add(dataBase);
        saveDataBases();
        return true;
    }
    
    public static DataBase getDataBase(String name) throws NoSuchDataBaseException
    {
        for(DataBase dataBase : dataBases)
        {
            if(dataBase.getDataBaseName().equals(name))
            {
                return dataBase;
            }
        }
        throw new NoSuchDataBaseException("Could not find a DataBase Named " + name);
    }
    
    public static boolean updateDataBase(DataBase dataBase)
    {
        /*Returns true if the database was updated*/
        for(DataBase storedDataBase : dataBases)
        {
            if(dataBase.equals(storedDataBase))
            {
                dataBases.remove(storedDataBase);
                dataBases.add(dataBase);
                saveDataBases();
                return true;
            }
        }
        return false;
    }
}
