package com.jumbodinosaurs.util;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class DataControllerTest
{
    
    @Ignore("Creates Annoying Files")
    @Test
    public void checkForLocalPath()
    {
        String localPath = "/JumboDinosaurs/Jumbo Dinosaurs .6/LOG/logs/jumb";
        File fileToCheck;
        DataController controller = new DataController(false);
        fileToCheck = DataController.checkForLocalPath(DataController.logsDirectory.getParentFile().getParentFile().getParentFile(),
                DataController.fixPathSeparator(localPath));
        System.out.println(fileToCheck.getAbsolutePath());
        assertTrue(fileToCheck.getAbsolutePath().contains(DataController.fixPathSeparator(localPath)));
        
        
    }
}