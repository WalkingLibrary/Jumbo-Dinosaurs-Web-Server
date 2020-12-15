package com.jumbodinosaurs.webserver.post.object;

import com.jumbodinosaurs.devlib.util.objects.PostRequest;

public abstract class RequestDependantPostObject extends PostObject
{
    protected transient PostRequest postRequest;
    protected transient CRUDRequest crudRequest;
    
    public PostRequest getPostRequest()
    {
        return postRequest;
    }
    
    public void setPostRequest(PostRequest postRequest)
    {
        this.postRequest = postRequest;
    }
    
    public CRUDRequest getCrudRequest()
    {
        return crudRequest;
    }
    
    public void setCrudRequest(CRUDRequest crudRequest)
    {
        this.crudRequest = crudRequest;
    }
}

