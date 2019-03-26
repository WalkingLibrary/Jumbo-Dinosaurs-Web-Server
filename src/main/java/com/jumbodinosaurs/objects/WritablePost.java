package com.jumbodinosaurs.objects;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class WritablePost
{


    private String localPath;
    private String user;
    private String content;
    private String postIdentifier;
    private LocalDateTime date;

    public WritablePost()
    {
    
    }
    
    public WritablePost clone()
    {
        return new Gson().fromJson(new Gson().toJson(this), WritablePost.class);
    }
    

    public String getUser()
    {
        return user;
    }

    public String getContent()
    {
        return content;
    }

    public String getLocalPath()
    {
        return localPath;
    }
    
    public void setLocalPath(String localPath)
    {
        this.localPath = localPath;
    }
    
    public void setUser(String user)
    {
        this.user = user;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public String getPostIdentifier()
    {
        return postIdentifier;
    }
    
    public void setPostIdentifier(String postIdentifier)
    {
        this.postIdentifier = postIdentifier;
    }
    
    
    public LocalDateTime getDate()
    {
        return date;
    }
    
    public void setDate(LocalDateTime date)
    {
        this.date = date;
    }
    
   
}
