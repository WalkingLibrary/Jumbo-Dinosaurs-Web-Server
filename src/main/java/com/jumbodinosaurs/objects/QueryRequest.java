package com.jumbodinosaurs.objects;

public class QueryRequest
{
    //LocalDateTime In the Future?
    private String file;
    private String typeOfData;
    private boolean getPosts;
    private String user;
    private String postIdentifier;
    private String keyword;
    
    
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
    
    public String getFile()
    {
        return file;
    }
    
    public void setFile(String file)
    {
        this.file = file;
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
    
    public boolean isGetPosts()
    {
        return getPosts;
    }
    
    public void setGetPosts(boolean getPosts)
    {
        this.getPosts = getPosts;
    }
    
    public String getTypeOfData()
    {
        return typeOfData;
    }
    
    public void setTypeOfData(String typeOfData)
    {
        this.typeOfData = typeOfData;
    }
}
