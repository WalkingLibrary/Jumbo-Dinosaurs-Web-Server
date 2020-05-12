package com.jumbodinosaurs.post.object;

public class CRUDRequest
{
    private String tableName;
    private String objectType;
    private String object;
    private String attribute;
    private String limiter;
    private int id;
    
    public String getTableName()
    {
        return tableName;
    }
    
    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }
    
    public String getObjectType()
    {
        return objectType;
    }
    
    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }
    
    public String getObject()
    {
        return object;
    }
    
    public void setObject(String object)
    {
        this.object = object;
    }
    
    public String getLimiter()
    {
        return limiter;
    }
    
    public void setLimiter(String limiter)
    {
        this.limiter = limiter;
    }
    
    public String getAttribute()
    {
        return attribute;
    }
    
    public void setAttribute(String attribute)
    {
        this.attribute = attribute;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
}
