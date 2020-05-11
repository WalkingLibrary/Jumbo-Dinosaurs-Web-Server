package com.jumbodinosaurs.post.object;

public class Permission
{
    private boolean adminPerms;
    private boolean canAdd;
    private boolean canRemove;
    private boolean canSearch;
    
    public Permission(boolean adminPerms, boolean canAdd, boolean canRemove, boolean canSearch)
    {
        this.adminPerms = adminPerms;
        this.canAdd = canAdd;
        this.canRemove = canRemove;
        this.canSearch = canSearch;
    }
    
    public boolean canAdd()
    {
        return canAdd;
    }
    
    public void setCanAdd(boolean canAdd)
    {
        this.canAdd = canAdd;
    }
    
    public boolean canRemove()
    {
        return canRemove;
    }
    
    public void setCanRemove(boolean canRemove)
    {
        this.canRemove = canRemove;
    }
    
    public boolean canSearch()
    {
        return canSearch;
    }
    
    public void setCanSearch(boolean canSearch)
    {
        this.canSearch = canSearch;
    }
    
    public boolean hasAdminPerms()
    {
        return adminPerms;
    }
    
    public void setAdminPerms(boolean adminPerms)
    {
        this.adminPerms = adminPerms;
    }
}
