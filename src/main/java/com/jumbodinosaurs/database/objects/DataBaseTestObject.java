package com.jumbodinosaurs.database.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataBaseTestObject
{
    private String text;
    
    public DataBaseTestObject()
    {
        text = "Hello World [" + Math.random() + "]";
    }
    
    @Override
    public String toString()
    {
        return "DataBaseTestObject{" +
                       "text='" + text + '\'' +
                       '}';
    }
    
    public DataBaseTestObject clone()
    {
        DataBaseTestObject clone = new Gson().fromJson(new Gson().toJson(this),
                                                       new TypeToken<DataBaseTestObject>() {}.getType());
        return clone;
    }
    
    public String getText()
    {
        return text;
    }
    
    public void setText(String text)
    {
        this.text = text;
    }
}
