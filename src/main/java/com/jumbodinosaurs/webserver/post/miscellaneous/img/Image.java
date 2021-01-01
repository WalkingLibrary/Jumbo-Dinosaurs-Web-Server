package com.jumbodinosaurs.webserver.post.miscellaneous.img;

import com.jumbodinosaurs.webserver.post.object.PostObject;

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
        
        if(this.base64ImageContents == null || this.fileType == null)
        {
            return false;
        }
    
        this.fileType = this.fileType.trim().toLowerCase();
        
        boolean isAcceptedType = false;
        for(AcceptedImageTypes type : AcceptedImageTypes.values())
        {
            if(type.getImageType().equals(this.fileType))
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
        int length = this.base64ImageContents.getBytes().length;
        int maxLength = 1000000;
        if(length > maxLength || length < 1)
        {
            return false;
        }
    
        try
        {
            Base64.getDecoder().decode(this.base64ImageContents);
        }
        catch(IllegalArgumentException e)
        {
            return false;
        }
    
        return true;
    }
    
    public String getBase64ImageContents()
    {
        return base64ImageContents;
    }
    
    public void setBase64ImageContents(String base64ImageContents)
    {
        this.base64ImageContents = base64ImageContents;
    }
    
    public String getFileType()
    {
        return fileType;
    }
    
    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }
    
    
    private enum AcceptedImageTypes
    {
        png("png"), jpeg("jpeg"), jpg("jpg");
        
        public String imageType;
        
        AcceptedImageTypes(String imageType)
        {
            this.imageType = imageType;
        }
        
        public String getImageType()
        {
            return imageType;
        }
    }
}
