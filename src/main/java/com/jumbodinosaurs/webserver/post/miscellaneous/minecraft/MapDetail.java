package com.jumbodinosaurs.webserver.post.miscellaneous.minecraft;

import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.post.miscellaneous.img.Description;
import com.jumbodinosaurs.webserver.post.object.CRUDRequest;
import com.jumbodinosaurs.webserver.post.object.IRequestDependantAttributes;

public class MapDetail extends CoordinateBased implements IRequestDependantAttributes
{
    private Description description;
    
    @Override
    public boolean isValidObject()
    {
        if(!description.isValidObject())
        {
            return false;
        }
        return super.isValidObject();
    }
    
    public Description getDescription()
    {
        return description;
    }
    
    public void setDescription(Description description)
    {
        this.description = description;
    }
    
    @Override
    public void setAttributesRequests(PostRequest postRequest, CRUDRequest crudRequest)
    {
        this.description.setPostRequest(postRequest);
        this.description.setCrudRequest(crudRequest);
    }
}
