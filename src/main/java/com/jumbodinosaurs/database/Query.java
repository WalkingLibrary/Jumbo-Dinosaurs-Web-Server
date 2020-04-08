package com.jumbodinosaurs.database;

import java.sql.ResultSet;

public class Query
{
    private String query;
    private ResultSet resultSet;
    
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
}
