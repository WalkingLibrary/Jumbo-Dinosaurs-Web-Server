package com.jumbodinosaurs.webserver.post.miscellaneous.img;

import com.jumbodinosaurs.webserver.auth.util.AuthUtil;
import com.jumbodinosaurs.webserver.post.object.RequestDependantPostObject;

public class Description extends RequestDependantPostObject
{
    private String username;
    private String description;
    
    public Description(String username, String description)
    {
        this.username = username;
        this.description = description;
    }
    
    
    @Override
    public boolean isValidObject()
    {
        int maxDescriptionLength = 1000;
        if(description == null || description.length() > maxDescriptionLength)
        {
            return false;
        }
    
        if(!AuthUtil.isValidUsername(username))
        {
            return false;
        }
    
        if(!this.postRequest.getUsername().equals(username))
        {
            return false;
        }
    
        return true;
    }
}
