package com.jumbodinosaurs.post.miscellaneous.img;

import com.jumbodinosaurs.post.object.PostObject;

import java.util.Base64;

public class Image extends PostObject
{
    private String base64ImageContents;
    private String fileType;
    
    @Override
    public boolean isValidObject()
    {
        /*
         * Process for Validating an Image
         * Check it's size
         *  */
        
        if(base64ImageContents == null)
        {
            return false;
        }
        
        if(fileType == null)
        {
            return false;
        }
        
        boolean isAcceptedType = false;
        for(AcceptedImageTypes type : AcceptedImageTypes.values())
        {
            if(type.equals(type.imageType))
            {
                isAcceptedType = true;
            }
        }
        
        if(!isAcceptedType)
        {
            return false;
        }
        
        //Check it's size
        //We only want to allow images the size of 1MB or lower
        //and images that are smaller than 1 byte should be considered invalid
        int length = base64ImageContents.getBytes().length;
        if(length > 1000000 || length < 1)
        {
            return false;
        }
        
        try
        {
            Base64.getDecoder().decode(base64ImageContents);
        }
        catch(IllegalArgumentException e)
        {
            return false;
        }
        
        return true;
    }
    
    
    private enum AcceptedImageTypes
    {
        png("png"), jpeg("jpeg"), jpg("jpg");
        
        public String imageType;
        
        AcceptedImageTypes(String imageType)
        {
            this.imageType = imageType;
        }
    }
}
