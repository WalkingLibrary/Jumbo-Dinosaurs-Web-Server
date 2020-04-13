package com.jumbodinosaurs.database;

import java.sql.ResultSet;

public class Query
{
    private String query;
    private ResultSet resultSet;
    private int responseCode;
    
    public Query(String query)
    {
        this.query = query;
    }
    
    public String getQuery()
    {
        return query;
    }
    
    public void setQuery(String query)
    {
        this.query = query;
    }
    
    public ResultSet getResultSet()
    {
        return resultSet;
    }
    
    public void setResultSet(ResultSet resultSet)
    {
        this.resultSet = resultSet;
    }
    
    public int getResponseCode()
    {
        return responseCode;
    }
    
    public void setResponseCode(int responseCode)
    {
        this.responseCode = responseCode;
    }
}
