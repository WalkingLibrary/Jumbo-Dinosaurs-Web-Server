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
    
    public boolean isCanAdd()
    {
        return canAdd;
    }
    
    public void setCanAdd(boolean canAdd)
    {
        this.canAdd = canAdd;
    }
    
    public boolean isCanRemove()
    {
        return canRemove;
    }
    
    public void setCanRemove(boolean canRemove)
    {
        this.canRemove = canRemove;
    }
    
    public boolean isCanSearch()
    {
        return canSearch;
    }
    
    public void setCanSearch(boolean canSearch)
    {
        this.canSearch = canSearch;
    }
    
    public boolean isAdminPerms()
    {
        return adminPerms;
    }
    
    public void setAdminPerms(boolean adminPerms)
    {
        this.adminPerms = adminPerms;
    }
}
