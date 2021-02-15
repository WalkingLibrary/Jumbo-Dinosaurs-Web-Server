package com.jumbodinosaurs.webserver.post.miscellaneous.minecraft;

import com.google.gson.Gson;
import com.jumbodinosaurs.webserver.post.miscellaneous.img.Image;

public class MinecraftJourneyMapChunkImage extends MinecraftLoadedChunk
{
    protected Image image;
    
    @Override
    public boolean isValidObject()
    {
        System.out.println("Object: " + new Gson().toJson(this));
        return this.image.isValidObject() && super.isValidObject();
    }
    
    public Image getImage()
    {
        return image;
    }
    
    public void setImage(Image image)
    {
        this.image = image;
    }
}
