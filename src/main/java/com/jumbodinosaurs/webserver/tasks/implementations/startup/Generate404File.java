package com.jumbodinosaurs.webserver.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.webserver.util.ServerUtil;

import java.io.File;

public class Generate404File extends StartUpTask
{
    public Generate404File()
    {
        super(Phase.PreInitialization);
    }
    
    public static String getDefault404Page()
    {
        String HTML404 = "<!DOCTYPE html>\n" +
                                 "<html>\n" +
                                 "<head>\n" +
                                 "<style>\n" +
                                 "body\n" +
                                 "{\n" +
                                 "    " +
                                 "background-color: lightgreen;\n" +
                                 "}\n" +
                                 "</style>\n" +
                                 "<title>\n" +
                                 "404 :(\n" +
                                 "</title>\n" +
                                 "</head>\n" +
                                 "<body>\n" +
                                 "<h1>\n" +
                                 "404 - File has Either Been Moved or Relocated\n" +
                                 "</h1>\n" +
                                 "<a href = \"./home.html\">Home</a>\n" +
                                 "</body>\n" +
                                 "</html>";
        
        return HTML404;
    }
    
    @Override
    public void run()
    {
        String fouroFourFileName = "404.html";
        File fouroFourHtml = new File(ServerUtil.getDirectory.getAbsolutePath() + fouroFourFileName);
        if(!fouroFourHtml.exists())
        {
            String default404 = getDefault404Page();
            GeneralUtil.writeContents(GeneralUtil.checkFor(ServerUtil.getDirectory, fouroFourFileName), default404, false);
        }
        
    }
    
}
