package com.jumbodinosaurs.post.object;

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class Table<E>
{
    private String name;
    private boolean isPublic;
    private String creator;
    private TypeToken<E> objectType;
    private HashMap<String, Permission> permissions;
    
    public Table(String name, boolean isPublic, String creator, TypeToken<E> objectType)
    {
        this.name = name;
        this.isPublic = isPublic;
        this.creator = creator;
        this.objectType = objectType;
        this.permissions = new HashMap<String, Permission>();
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public TypeToken<E> getObjectType()
    {
        return objectType;
    }
    
    public void setObjectType(TypeToken<E> objectType)
    {
        this.objectType = objectType;
    }
    
    public HashMap<String, Permission> getPermissions()
    {
        return permissions;
    }
    
    public void setPermissions(HashMap<String, Permission> permissions)
    {
        this.permissions = permissions;
    }
    
    public String getCreator()
    {
        return creator;
    }
    
    public void setCreator(String creator)
    {
        this.creator = creator;
    }
    
    public boolean isPublic()
    {
        return isPublic;
    }
    
    public void setPublic(boolean aPublic)
    {
        isPublic = aPublic;
    }
}
