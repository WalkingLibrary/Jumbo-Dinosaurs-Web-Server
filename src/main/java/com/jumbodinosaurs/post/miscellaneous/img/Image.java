package com.jumbodinosaurs.post.miscellaneous.img;

import com.jumbodinosaurs.post.object.PostObject;

public class Image extends PostObject
{
    private String imageContents;
    
    public Image(String imageContents)
    {
        this.imageContents = imageContents;
    }
    
    @Override
    public boolean isValidObject()
    {
        /*
         * Process for Validating an Image
         * Check it's size
         *  */
        
        //Check it's size
        //We only want to allow images the size of 1MB or lower
        //and images that are smaller than 1 byte should be considered invalid
        if(imageContents.getBytes().length > 1000000 || imageContents.getBytes().length < 1)
        {
            return false;
        }
        
        return true;
    }
}
