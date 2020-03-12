package com.jumbodinosaurs.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.util.ServerUtil;

import java.io.File;

public class GenerateHomeFile extends StartUpTask
{
    public GenerateHomeFile()
    {
        super(Phase.PreInitialization);
    }
    
    public static String getDefaultHomePage()
    {
        String homeHTML = "<!DOCTYPE html>\n" +
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
                                   "Index\n" +
                                   "</title>\n" +
                                   "</head>\n" +
                                   "<body>\n" +
                                   "<h1>\n";
        
        homeHTML += "<a href = ./index.html>Index</a><br>";
        
        
        homeHTML += "</h1>\n" + "</body>\n" + "</html>";
        
        return homeHTML;
    }
    
    @Override
    public void run()
    {
        String homeHTMLFileName = "home.html";
        File homeHTMLFile = new File(ServerUtil.getDirectory.getAbsolutePath() + homeHTMLFileName);
        if(!homeHTMLFile.exists())
        {
            String defaultHomePage = getDefaultHomePage();
            GeneralUtil.writeContents(GeneralUtil.checkFor(ServerUtil.getDirectory, homeHTMLFileName), defaultHomePage, false);
        }
    }
    
    
}
