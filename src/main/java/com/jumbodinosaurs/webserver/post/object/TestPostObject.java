package com.jumbodinosaurs.webserver.post.object;

public class TestPostObject extends PostObject
{
    private String name;
    
    @Override
    public boolean isValidObject()
    {
        
        return name != null;
    }
}
