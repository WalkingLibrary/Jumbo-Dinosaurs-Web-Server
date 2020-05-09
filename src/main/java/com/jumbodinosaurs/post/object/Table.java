package com.jumbodinosaurs.post.object;

import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.auth.server.User;

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
    
    
    public Permission getPermissions(String username)
    {
        return permissions.get(username);
    }
    
    public void addUser(User user, Permission permission)
    {
        permissions.put(user.getUsername(), permission);
    }
    
    public void updateUser(User user, Permission updatedPermissions)
    {
        permissions.replace(user.getUsername(), updatedPermissions);
    }
    
    public void removeUser(User user)
    {
        permissions.remove(user.getUsername());
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
    
    public void setPublic(boolean isPublic)
    {
        this.isPublic = isPublic;
    }
}
