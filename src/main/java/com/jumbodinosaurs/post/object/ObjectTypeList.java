package com.jumbodinosaurs.post.object;

import java.util.ArrayList;

public class ObjectTypeList
{
    private ArrayList<String> objectTypes;
    
    public ObjectTypeList(ArrayList<String> objectTypes)
    {
        this.objectTypes = objectTypes;
    }
    
    public ArrayList<String> getObjectTypes()
    {
        return objectTypes;
    }
    
    public void setObjectTypes(ArrayList<String> objectTypes)
    {
        this.objectTypes = objectTypes;
    }
}
