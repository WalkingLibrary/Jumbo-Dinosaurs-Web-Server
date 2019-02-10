package com.jumbodinosaurs;


import com.google.gson.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/*

 */
public class DataController
{
    private static File allowedDirectory;
    private static File logsDirectory;
    private static SessionLogger logger;
    private static String[] domains;
    public static String host = "";


    public DataController()
    {
        try
        {
            this.allowedDirectory = this.checkFor(new File(System.getProperty("user.dir")).getParentFile(), "Shared");
            OperatorConsole.printMessageFiltered(this.allowedDirectory.getAbsolutePath(), true, false);
            this.logsDirectory = checkFor(this.allowedDirectory.getParentFile(), "LOG");
            this.setHost();
            this.makeSiteIndexand404PageDefault();
            //For logging sessions thread
            this.logger = new SessionLogger(this);
            Thread loggerThread = new Thread(this.logger);
            loggerThread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Creating DataController", false,true);
        }
    }

    public DataController(String[] domains)
    {
        try
        {
            this.allowedDirectory = this.checkFor(new File(System.getProperty("user.dir")).getParentFile(), "Shared");
            OperatorConsole.printMessageFiltered(this.allowedDirectory.getAbsolutePath(), true, false);
            this.logsDirectory = checkFor(this.allowedDirectory.getParentFile(), "LOG");
            this.domains = domains;
            this.setHost();
            this.makeSiteIndexand404PageDomains();
            //For logging sessions thread
            this.logger = new SessionLogger(this);
            Thread loggerThread = new Thread(this.logger);
            loggerThread.start();
        }
        catch (Exception e)
        {

            System.out.println("Error Creating DataController");
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Creating DataController", false, true);
        }
    }



    /* @Function: Checks for the String name in the given Dir of File file
     * returns it and makes it if not there.
     *
     * @Return: desired name within file
     * @param1: File file Dir to search for name in
     * @param2: String Name name to search for in file
     * @PreCondition: File must be a Dir and also Exist
     */
    public static File checkFor(File file, String name) throws IOException
    {
        boolean needToMakeFile = true;
        String[] contentsOfFile = file.list();
        for (int i = 0; i < contentsOfFile.length; i++)
        {
            if (contentsOfFile.equals(name))
            {
                needToMakeFile = false;
            }
        }

        File neededFile = new File(file.getPath().toString() + "/" + name);
        if (needToMakeFile)
        {
            if (name.indexOf(".") >= 0)
            {
                neededFile.createNewFile();
            }
            else
            {
                neededFile.mkdir();
            }
        }
        return neededFile;
    }


    private void setHost()
    {
        try
        {
            URL address = new URL("http://bot.whatismyipaddress.com");

            BufferedReader sc = new BufferedReader(new InputStreamReader(address.openStream()));

            this.host = sc.readLine().trim();
            OperatorConsole.printMessageFiltered("Public IP: " + this.host, false, false);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Setting Host", false, true);
        }
    }

    public String getHost()
    {
        return this.host;
    }

    public static void log(Session session)
    {
        logger.addSession(session);
    }

    public void makeSiteIndexand404PageDefault()
    {
        try
        {
            File pageIndex = this.checkFor(this.allowedDirectory, "index.html");
            PrintWriter output = new PrintWriter(pageIndex);
            String indexHTML = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<style>\n" +
                    "body\n" +
                    "{\n" +
                    "    background-color: lightgreen;\n" +
                    "}\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h4>\n" +
                    "Sites<br>";
            for (File file : this.listFilesRecursive(this.allowedDirectory))
            {
                indexHTML += "<a href = http://" + this.host + "/" + file.getName() + ">" + this.host + "/" + file.getName() + "</a><br>";
            }

            indexHTML += "</h4>\n" +
                    "</body>\n" +
                    "</html>";
            output.write(indexHTML);
            output.close();

            File page404 = this.checkFor(this.allowedDirectory, "404.html");
            output = new PrintWriter(page404);

            String HTML404 = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<style>\n" +
                    "body\n" +
                    "{\n" +
                    "    background-color: lightgreen;\n" +
                    "}\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h1>\n" +
                    "404 - File has Either Been Moved or Relocated\n" +
                    "</h1>\n" +
                    "<a href = \"http://" + this.host + "/index.html\">Index</a>\n" +
                    "</body>\n" +
                    "</html>";
            output.write(HTML404);
            output.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Creating Index and 404 Page", false, true);
        }
    }

    public void makeSiteIndexand404PageDomains()
    {
        try
        {
            File pageIndex = this.checkFor(this.allowedDirectory, "index.html");
            PrintWriter output = new PrintWriter(pageIndex);
            String indexHTML = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<style>\n" +
                    "body\n" +
                    "{\n" +
                    "    background-color: lightgreen;\n" +
                    "}\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h4>\n" +
                    "Sites<br>";
            for (String domain : this.domains)
            {
                indexHTML += "<a href = http://" + domain + ">" + domain + "</a><br>";
            }
            indexHTML += "</h4>\n" +
                    "</body>\n" +
                    "</html>";
            output.write(indexHTML);
            output.close();

            File page404 = this.checkFor(this.allowedDirectory, "404.html");
            output = new PrintWriter(page404);

            String HTML404 = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<style>\n" +
                    "body\n" +
                    "{\n" +
                    "    background-color: lightgreen;\n" +
                    "}\n" +
                    "</style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h1>\n" +
                    "404 - File has Either Been Moved or Relocated\n" +
                    "</h1>\n" +
                    "<a href = \"http://www.jumbodinosaurs.com/index.html\">Index</a>\n" +
                    "</body>\n" +
                    "</html>";
            output.write(HTML404);
            output.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Creating Index and 404 Page", false, true);
        }
    }

    public static String[] getDomains()
    {
        return domains;
    }



    public static String getType(File file)
    {
        String temp = file.getName();
        while (temp.indexOf(".") > -1)
        {
            temp = temp.substring(temp.indexOf(".") + 1);
        }
        return temp;
    }

    //Returns the fileWanted if it is in allowedDirectory
    //The Code Returns null if the file is not in the directory
    public static File getFileFromAllowedDirectory(String fileWanted)
    {
        File fileToGive = null;
        //Gets all files in allowedDir
        File[] filesInAllowedDir = listFilesRecursive(allowedDirectory);
        //If count is greater than 1 might have duplicate files and could be a problem
        int count = 0;

        //Try with given slashes to find file
        for (File file : filesInAllowedDir)
        {

            int indexOfFileWanted = file.getAbsolutePath().indexOf(fileWanted);
            int lengthOfAbsolutePath = file.getAbsolutePath().length();
            //This Function is Not as simple as ->  If file is in shared return said file
            //fileWanted can sometimes be a local path and we need to check that path with the absolute path of the current
            //iteration of the loop's file's path to avoid returning domain duplicate Example: /jumbodinosaurs.com/home.html vs /2b2t.buisness/home.html
            //Same as below in inverted slashes check
            if (indexOfFileWanted > -1 && indexOfFileWanted + fileWanted.length() == lengthOfAbsolutePath)//MAKE SURE FILES NAMED THE SAME HAVE DIFFERENT PARENT FOLDERS
            {
                fileToGive = file;
                count++;
            }
        }
        if (fileToGive != null)
        {
            OperatorConsole.printMessageFiltered("GET FILE CALLED -> File Retrieved: " + fileToGive.getAbsolutePath(), true, false);
        }
        else// invert slashes and try to find file for different operating systems
        {
            String invertedWanted = invertSlashes(fileWanted);
            for (File file : filesInAllowedDir)
            {
                int indexOfFileWanted = file.getAbsolutePath().indexOf(invertedWanted);
                int lengthOfAbsolutePath = file.getAbsolutePath().length();
                if (indexOfFileWanted > -1 && indexOfFileWanted + invertedWanted.length() == lengthOfAbsolutePath)//MAKE SURE FILES NAMED THE SAME HAVE DIFFERENT PARENT FOLDERS
                {
                    fileToGive = file;
                    count++;
                }
            }
        }

        //DEBUG
        if (count > 1)
        {
            System.out.println("getFileFromAllowedDirectory() Count: " + count);
        }
        return fileToGive;
    }


    public static File getLogsJson()
    {
        try
        {
            File logFile = checkFor(logsDirectory, "logs.json");
            return logFile;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error get logs.json", false, true);
        }

        return null;
    }

    //For Reading any file on the system
    public static String getFileContents(File file)
    {
        //Read File
        String fileRequestedContents = "";
        try
        {
            Scanner input = new Scanner(file);
            while (input.hasNextLine())
            {
                fileRequestedContents += input.nextLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Reading File Contents\n File Path: " + file.getPath(), false, true);

        }
        return fileRequestedContents;
    }

    //Cycles thru the given string and if there are any forward slashes or backward slashes it will invert them
    public static String invertSlashes(String str)
    {
        String invertedStr = str;
        for (int i = 0; i < str.length() - 1; i++)
        {
            if (invertedStr.substring(i, i + 1).contains("/")
                    || invertedStr.substring(i, i + 1).contains("\\"))
            {
                if (invertedStr.substring(i, i + 1).contains("/"))
                {
                    invertedStr = invertedStr.substring(0, i) + "\\" + invertedStr.substring(i + 1);
                }
                else if (invertedStr.substring(i, i + 1).contains("\\"))
                {
                    invertedStr = invertedStr.substring(0, i) + "/" + invertedStr.substring(i + 1);
                }
            }

        }
        return invertedStr;
    }


    /*
    public String getPictureLength(String pictureName)
    {
        String contents = "";
        for (String[] content : this.pictureFilesContents)
        {
            if (content[0].equals(this.getFileFromAllowedDirectory(pictureName).getAbsolutePath()))
            {
                contents = "" + content[1].getBytes().length;
                break;
            }
        }
        return contents;
    }
    */



    public static byte[] readPhoto(File file)
    {
        try
        {
            byte[] imageBytes = new byte[(int) file.length()];
            InputStream imageStream = new BufferedInputStream(new FileInputStream(file));
            imageStream.read(imageBytes, 0, imageBytes.length);
            return imageBytes;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Reading Photo", false, true);

        }
        return null;
    }




    public static File[] listFilesRecursive(File directory)
    {
        ArrayList<File> files = new ArrayList<File>();
        for (File file : directory.listFiles())
        {
            if (file.isDirectory())
            {
                files.addAll(Arrays.asList(listFilesRecursive(file)));
            }
            else
            {
                files.add(file);
            }
        }
        File[] filesToReturn = new File[files.size()];
        for (int i = 0; i < files.size(); i++)
        {
            filesToReturn[i] = files.get(i);
        }
        return filesToReturn;
    }

}
