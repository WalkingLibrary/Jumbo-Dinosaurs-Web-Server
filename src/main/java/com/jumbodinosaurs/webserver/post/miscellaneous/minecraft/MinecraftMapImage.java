package com.jumbodinosaurs.webserver.post.miscellaneous.minecraft;

import com.jumbodinosaurs.webserver.post.miscellaneous.img.Image;

import java.util.ArrayList;

public class MinecraftMapImage extends CoordinateBased
{
    private Image image;
    private ArrayList<String> identifiers;
    
    
    @Override
    public boolean isValidObject()
    {
        if(this.identifiers == null)
        {
            this.identifiers = new ArrayList<String>();
        }
        
        if(this.identifiers.size() > 255)
        {
            return false;
        }
        
        for(String identifier : this.identifiers)
        {
            if(identifier.length() > 128 || identifier.length() <= 0)
            {
                return false;
            }
        }
        
        return this.image.isValidObject() && super.isValidObject();
    }
}
