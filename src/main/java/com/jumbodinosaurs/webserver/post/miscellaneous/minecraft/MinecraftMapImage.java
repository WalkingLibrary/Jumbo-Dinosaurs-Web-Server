package com.jumbodinosaurs.webserver.post.miscellaneous.minecraft;

import com.jumbodinosaurs.webserver.post.miscellaneous.img.Image;

public class MinecraftMapImage extends CoordinateBased
{
    private Image image;
    
    
    @Override
    public boolean isValidObject()
    {
        return this.image.isValidObject() && super.isValidObject();
    }
}
