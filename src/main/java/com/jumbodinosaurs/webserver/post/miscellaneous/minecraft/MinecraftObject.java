package com.jumbodinosaurs.webserver.post.miscellaneous.minecraft;

import com.jumbodinosaurs.webserver.post.object.RequestDependantPostObject;

public class MinecraftObject extends RequestDependantPostObject
{
    private String server;
    
    
    @Override
    public boolean isValidObject()
    {
        if(this.server == null || this.server.length() > 256)
        {
            return false;
        }
        return true;
    }
}
