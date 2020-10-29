package com.jumbodinosaurs.webserver.tasks.implementations.startup;

import com.jumbodinosaurs.devlib.task.Phase;
import com.jumbodinosaurs.devlib.task.StartUpTask;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.webserver.util.ServerUtil;

import java.io.File;

public class GenerateIndexFile extends StartUpTask
{
    public GenerateIndexFile()
    {
        super(Phase.PreInitialization);
    }
    
    public static String getIndexPage()
    {
        String indexHTML = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "<style>\n" + "body\n" + "{\n" + "    " +
                                   "background-color: lightgreen;\n" + "}\n" + "</style>\n" + "<title>\n" + "Index\n" + "</title>\n" + "</head>\n" + "<body>\n" + "<h4>\n" + "Pages<br>";
    
        for(File file : GeneralUtil.listFilesRecursive(ServerUtil.getDirectory))
        {
            String allowedHiddenDir = ".well-known";
            String hiddenDirPattern = File.separator + ".";
            if(!file.getAbsolutePath().contains(hiddenDirPattern) || file.getAbsolutePath().contains(allowedHiddenDir))
            {
                String relitivePath = file.getParentFile().getName() + File.separator + file.getName();
                indexHTML += "<a href = ./" + file.getName() + ">" + relitivePath + "</a><br>";
            }
        }
    
        indexHTML += "</h4>\n" + "</body>\n" + "</html>";
        return indexHTML;
    }
    
    @Override
    public void run()
    {
        String indexFileName = "index.html";
        File indexFile = new File(ServerUtil.getDirectory.getAbsolutePath() + indexFileName);
        
        if(!indexFile.exists())
        {
            String indexPage = getIndexPage();
            GeneralUtil.writeContents(GeneralUtil.checkFor(ServerUtil.getDirectory, indexFileName), indexPage, false);
        }
    }
    
}
