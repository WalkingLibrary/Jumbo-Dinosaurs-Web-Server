package com.jumbodinosaurs.webserver.post.miscellaneous.minecraft;

import com.jumbodinosaurs.webserver.post.object.RequestDependantPostObject;

public class MinecraftObject extends RequestDependantPostObject
{
    protected String server;
    
    
    @Override
    public boolean isValidObject()
    {
        if(this.server == null || this.server.length() > 256)
        {
            return false;
        }
        return true;
    }
    
    public String getServer()
    {
        return server;
    }
    
    public void setServer(String server)
    {
        this.server = server;
    }
}
