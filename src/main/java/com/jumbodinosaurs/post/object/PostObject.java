package com.jumbodinosaurs.post.object;

import com.jumbodinosaurs.devlib.database.Identifiable;

public abstract class PostObject implements Identifiable
{
    private transient int id;
    private String user;
    private int tableID;
    
    
    public abstract boolean isValidObject();
    
    public String getUser()
    {
        return user;
    }
    
    public void setUser(String user)
    {
        this.user = user;
    }
    
    @Override
    public int getId()
    {
        return id;
    }
    
    @Override
    public void setId(int id)
    {
        this.id = id;
    }
    
    public int getTableID()
    {
        return tableID;
    }
    
    public void setTableID(int tableID)
    {
        this.tableID = tableID;
    }
}
