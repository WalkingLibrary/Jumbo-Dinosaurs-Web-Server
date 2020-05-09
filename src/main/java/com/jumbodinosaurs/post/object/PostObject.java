package com.jumbodinosaurs.post.object;

public abstract class PostObject
{
    private String user;
    
    public abstract boolean isValidObject();
    
    public String getUser()
    {
        return user;
    }
    
    public void setUser(String user)
    {
        this.user = user;
    }
}
