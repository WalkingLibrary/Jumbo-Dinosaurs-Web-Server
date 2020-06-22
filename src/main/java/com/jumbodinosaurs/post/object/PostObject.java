package com.jumbodinosaurs.post.object;

public abstract class PostObject
{
    private String user;
    private String table;
    
    public abstract boolean isValidObject();
    
    public String getUser()
    {
        return user;
    }
    
    public void setUser(String user)
    {
        this.user = user;
    }
    
    public String getTable()
    {
        return table;
    }
    
    public void setTable(String table)
    {
        this.table = table;
    }
}
