package com.jumbodinosaurs.database;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class DataBase
{
    private String dataBaseName;
    private String ip;
    private String port;
    private String baseInfo;
    private DataBaseUser user;
    
    
    public DataBase(String dataBaseName, String ip, String port, DataBaseUser user)
    {
        this.dataBaseName = dataBaseName;
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.baseInfo = "jdbc:mysql://";
    }
    
    public Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(getURL(), user.getUsername(), user.getPassword());
    }
    
    public String getURL()
    {
        return baseInfo + ip + ":" + port + "/" + dataBaseName;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(o == null || getClass() != o.getClass())
        {
            return false;
        }
        DataBase dataBase = (DataBase) o;
        return Objects.equals(dataBaseName, dataBase.dataBaseName) &&
                       Objects.equals(ip, dataBase.ip) &&
                       Objects.equals(port, dataBase.port) &&
                       Objects.equals(baseInfo, dataBase.baseInfo) &&
                       Objects.equals(user, dataBase.user);
    }
    
    
    public String getDataBaseName()
    {
        return dataBaseName;
    }
}
