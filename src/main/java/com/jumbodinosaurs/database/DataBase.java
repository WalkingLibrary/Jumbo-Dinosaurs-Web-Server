package com.jumbodinosaurs.database;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
}
