package com.jumbodinosaurs.webserver.post.object;

import com.jumbodinosaurs.devlib.database.Identifiable;
import com.jumbodinosaurs.webserver.auth.server.User;

import java.util.HashMap;

public class Table<E> implements Identifiable
{
    //Warning Don't Use Transient for Stopping data from being sent via
    // Get Tables as Gson will still send it
    private String name;
    private boolean isPublic;
    private String creator;
    private HashMap<String, Permission> permissions;
    private transient int id;
    
    public Table(String name, boolean isPublic, String creator)
    {
        this.name = name;
        this.isPublic = isPublic;
        this.creator = creator;
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
    
    public HashMap<String, Permission> getPermissions()
    {
        return permissions;
    }
    
    public void setPermissions(HashMap<String, Permission> permissions)
    {
        this.permissions = permissions;
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
