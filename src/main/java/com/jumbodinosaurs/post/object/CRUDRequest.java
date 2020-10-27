package com.jumbodinosaurs.post.object;

import com.google.gson.reflect.TypeToken;

public class CRUDRequest<E>
{
    private transient TypeToken<E> typeToken;
    private String tableName;
    private String objectType;
    private String object;
    private String attribute;
    private String limiter;
    private int tableID;
    private int objectID;
    
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
    
    public int getTableID()
    {
        return tableID;
    }
    
    public void setTableID(int tableID)
    {
        this.tableID = tableID;
    }
    
    public int getObjectID()
    {
        return objectID;
    }
    
    public void setObjectID(int objectID)
    {
        this.objectID = objectID;
    }
    
    public TypeToken<E> getTypeToken()
    {
        return typeToken;
    }
    
    public void setTypeToken(TypeToken<E> typeToken)
    {
        this.typeToken = typeToken;
    }
}
