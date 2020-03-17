package com.jumbodinosaurs.util;


import com.jumbodinosaurs.ServerController;
import com.jumbodinosaurs.objects.WritablePost;

import java.util.ArrayList;

public class PostWriter implements Runnable
{
    public static ArrayList<WritablePost> postsToWrite = new ArrayList<WritablePost>();
    
    
    public static synchronized void addPost(WritablePost post)
    {
        postsToWrite.add(post);
    }
    
    public void run()
    {
        try
        {
            while (true)
            {
                if (this.postsToWrite.size() > 0)
                {
                    ServerUtil.writePostData(this.postsToWrite.remove(0));
                }
                Thread.sleep(10);
            }
        }
        catch (Exception e)
        {
            ServerController.consoleLogger.error("Error Logging Session", e);
        }
    }
    
    public static void initializePostWriter()
    {
        Thread postWriterThread = new Thread(new PostWriter());
        postWriterThread.start();
    }
    
    
    
}
