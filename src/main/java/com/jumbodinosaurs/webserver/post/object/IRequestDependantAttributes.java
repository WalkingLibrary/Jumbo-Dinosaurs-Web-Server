package com.jumbodinosaurs.webserver.post.object;

import com.jumbodinosaurs.devlib.util.objects.PostRequest;

public interface IRequestDependantAttributes
{
    void setAttributesRequests(PostRequest postRequest, CRUDRequest crudRequest);
}
