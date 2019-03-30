package com.jumbodinosaurs.objects;

public class QueryRequest
{
    //LocalDateTime In the Future?
    private String keyword;
    private String path;
    private String user;
    private String postIdentifier;
    private boolean getPostIdentifiersRequest;
    
    public QueryRequest()
    {
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public String getPath()
    {
        return path;
    }
    
    public void setPath(String path)
    {
        this.path = path;
    }
    
    public String getUser()
    {
        return user;
    }
    
    public void setUser(String user)
    {
        this.user = user;
    }
    
    public String getPostIdentifier()
    {
        return postIdentifier;
    }
    
    public void setPostIdentifier(String postIdentifier)
    {
        this.postIdentifier = postIdentifier;
    }
    
    public boolean isGetPostIdentifiersRequest()
    {
        return getPostIdentifiersRequest;
    }
    
    public void setGetPostIdentifiersRequest(boolean getPostIdentifiersRequest)
    {
        this.getPostIdentifiersRequest = getPostIdentifiersRequest;
    }
}
