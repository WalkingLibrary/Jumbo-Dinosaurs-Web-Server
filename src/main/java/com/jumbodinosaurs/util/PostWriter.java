package com.jumbodinosaurs.util;


import com.jumbodinosaurs.objects.WritablePost;

import java.util.ArrayList;

public class PostWriter implements Runnable
{
    public static ArrayList<WritablePost> postsToWrite = new ArrayList<WritablePost>();
    
    public PostWriter()
    {
    
    }
    
    
    public synchronized void addPost(WritablePost post)
    {
        this.postsToWrite.add(post);
    }
    
    public void run()
    {
        try
        {
            while (true)
            {
                if (this.postsToWrite.size() > 0)
                {
                    DataController.writePostData(this.postsToWrite.remove(0));
                }
                Thread.sleep(10);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Logging Session",false, true);
        }
    }
    
    
    
}
