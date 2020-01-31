package com.jumbodinosaurs.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.util.ServerUtil;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class WritablePost
{


    private String localPath;
    private String user;
    private String content;
    private String postIdentifier;
    private String objectType;
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
    
    
    public String getObjectType()
    {
        return objectType;
    }
    
    public String toJsonString()
    {
        return new Gson().toJson(this);
    }
    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }
    
    
    public static WritablePost getSanitized(WritablePost post)
    {
        WritablePost tempPost = new WritablePost();
        tempPost.setLocalPath(post.getLocalPath());
        tempPost.setUser(post.getUser());
        tempPost.setObjectType(post.getObjectType());
        tempPost.setDate(post.getDate());
    
        tempPost.setPostIdentifier(ServerUtil.rewriteHTMLEscapeCharacters(post.getPostIdentifier()));
        String objectType = post.getObjectType();
       
        if(objectType.equals(MinecraftWrittenBook.class.getTypeName()))
        {
            Type typeToken = new TypeToken<ArrayList<MinecraftWrittenBook>>(){}.getType();
            ArrayList<MinecraftWrittenBook> books = new Gson().fromJson(post.getContent(), typeToken);
            ArrayList<MinecraftWrittenBook> sanitizedBooks = new ArrayList<MinecraftWrittenBook>();
            for(MinecraftWrittenBook book: books)
            {
                sanitizedBooks.add(MinecraftWrittenBook.getSanitizedBook(book));
            }
            tempPost.setContent(new Gson().toJson(sanitizedBooks));
        }
        else if(objectType.equals(MinecraftSign.class.getTypeName()))
        {
            tempPost.setContent(new Gson().toJson(MinecraftSign.getSanitizedSign(new Gson().fromJson(post.getContent(),
                                                                                         MinecraftSign.class))));
        }
        else
        {
            tempPost.setContent(ServerUtil.rewriteHTMLEscapeCharacters(post.getContent()));
        }
        return tempPost;
    }
}
