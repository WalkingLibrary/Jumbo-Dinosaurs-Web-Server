package com.jumbodinosaurs.objects;

public class WritablePost
{


    private String localPath;
    private String user;
    private String content;
    private String date;

    public WritablePost(String localPath, String user, String content, String date)
    {
        this.localPath = localPath;
        this.user = user;
        this.content = content;
        this.date = date;
    }

    public String getDate()
    {
        return date;
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
}
