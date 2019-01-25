package com.jumbodinosaurs;


import com.google.gson.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/*

 */
public class DataController implements Runnable
{
    private static File allowedDirectory;
    private static File imageJsonDir;
    private static File logs;
    private static ArrayList<String[]> pictureFilesContents = new ArrayList<String[]>();
    private static ArrayList<Session> sessionsToLog = new ArrayList<Session>();
    private static SessionLogger logger;
    private String[] domains;
    private String host = "";


    public DataController()
    {
        try
        {
            this.allowedDirectory = this.checkFor(new File(System.getProperty("user.dir")).getParentFile(), "Shared");
            System.out.println(this.allowedDirectory.getAbsolutePath());
            this.logs = checkFor(this.allowedDirectory.getParentFile(), "LOG");
            this.imageJsonDir = checkFor(this.allowedDirectory.getParentFile(), "ImageJson");
            this.setHost();
            this.makeSiteIndexand404PageDefault();
            this.init();
        }
        catch (Exception e)
        {
            System.out.println("Error Creating DataController");
            e.printStackTrace();
            System.out.println(e.getCause());
        }
    }

    public DataController(String[] domains)
    {
        try
        {
            this.allowedDirectory = this.checkFor(new File(System.getProperty("user.dir")).getParentFile(), "Shared");
            System.out.println(this.allowedDirectory.getAbsolutePath());
            this.logs = checkFor(this.allowedDirectory.getParentFile(), "LOG");
            this.imageJsonDir = checkFor(this.allowedDirectory.getParentFile(), "ImageJson");
            this.domains = domains;
            this.setHost();
            this.makeSiteIndexand404PageDomains();
            this.init();
        }
        catch (Exception e)
        {
            System.out.println("Error Creating DataController");
            e.printStackTrace();
            System.out.println(e.getCause());
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

    public void init()
    {
        Thread initThread = new Thread(this);
        initThread.start();
        //For logging sessions thread
        this.logger = new SessionLogger(this);
        Thread loggerThread = new Thread(this.logger);
        loggerThread.start();
    }

    public void run()
    {
        try
        {
            this.initPictures();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error Initializing Pictures");
        }

    }

    private void setHost()
    {
        try
        {
            URL address = new URL("http://bot.whatismyipaddress.com");

            BufferedReader sc = new BufferedReader(new InputStreamReader(address.openStream()));

            this.host = sc.readLine().trim();
            System.out.println("Public IP: " + this.host);
        }
        catch (Exception e)
        {
            System.out.println("Error Setting Host");
            e.printStackTrace();
            System.out.println(e.getCause());
        }
    }

    public String getHost()
    {
        return this.host;
    }

    public void log(Session session)
    {
        this.logger.addSession(session);
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
            System.out.println("Error Creating Index and 404 Page");
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
            System.out.println("Error Creating Index and 404 Page");
        }
    }

    public String[] getDomains()
    {
        return this.domains;
    }

    //Reads all pictures in Shared and changes them to a "String" and stores them in picture File contents
    // with there name as the first element and the second as the contents
    public void initPictures() throws Exception
    {
        System.out.println("Initializing Pictures");
        File[] files = this.listFilesRecursive(this.allowedDirectory);

        ArrayList<File> picturesFiles = new ArrayList<File>();
        //Gets Files in shared that are pictures
        for (File file : files)
        {
            if ((getType(file).contains("png") ||
                    getType(file).contains("jpeg") ||
                    getType(file).contains("jpg") ||
                    getType(file).contains("ico")))
            {
                picturesFiles.add(file);
            }
        }

        //Load Pictures From Json
        this.loadPicturesFromJsonintoArray();
        for (File file : picturesFiles)
        {
            //Check To See if the file has already be initialized
            boolean needToInit = true;
            for (String[] strings : this.pictureFilesContents)
            {
                if (strings[0].contains(file.getName()))
                {
                    needToInit = false;
                }
            }


            if (needToInit)
            {
                System.out.println("INITIALIZING: " + file.getName());
                byte[] contentBytes = this.readPhoto(file);
                System.out.println("File Size: " + contentBytes.length);
                String imageContents = "";
                for (int i = 0; i < contentBytes.length; i++)
                {
                    byte byt = contentBytes[i];
                    imageContents += (char) byt;

                    String print = "Byte: " + i + " : Out of " + contentBytes.length;
                    System.out.print(print);
                    String clr = "";
                    for (int b = 0; b < print.length(); b++)
                    {
                        clr += "\b";
                    }
                    System.out.print(clr);

                }
                //Add To "MEMORY"
                FastPicture picture = new FastPicture(file.getAbsolutePath(), imageContents, "" + contentBytes.length);
                this.pictureFilesContents.add(picture.getAsArray());
                this.savePictureToImageJson(picture);
            }
        }


        System.out.println("Done Initializing Pictures");
    }

    public void savePictureToImageJson(FastPicture picture) throws Exception
    {
        //GSON Objects for writeing and dealing with json
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        JsonParser parser = new JsonParser();

        System.out.println(picture.getName());
        System.out.println("Saving Picture To Image Json");
        File imageJson = this.checkFor(this.imageJsonDir, "pictures.json");
        String fileContents = this.getFileContents(imageJson);
        //try parsing file with json parser
        JsonElement element = null;
        try
        {
            element = parser.parse(fileContents);
            JsonObject fastPictureList = new JsonObject();

            //If file already has contents
            if (element != null &&
                    element.isJsonObject() &&
                    element.getAsJsonObject().getAsJsonArray("picturelist") != null &&
                    element.getAsJsonObject().getAsJsonArray("picturelist").isJsonArray())
            {
                fastPictureList = element.getAsJsonObject();
                fastPictureList.getAsJsonObject().getAsJsonArray("picturelist").add(gson.toJson(picture, FastPicture.class));
            }
            else//MAKE FILES CONTENTS?
            {
                fastPictureList.add("picturelist", new JsonArray());
                fastPictureList.getAsJsonObject().getAsJsonArray("picturelist").add(gson.toJson(picture, FastPicture.class));
            }
            //write contents of fastPictureList to imageJson and close()
            PrintWriter logOut = new PrintWriter(imageJson);
            logOut.write(fastPictureList.toString());
            logOut.close();
            System.out.println("Done Saving Picture To Image Json");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error Writing Picture to Json");
        }

    }

    /* Loads all Files in imageJson to pictureFileContents in form of
     * {File Name and Extension, File in String Format, File Length in Bytes}
     *
     */
    public void loadPicturesFromJsonintoArray() throws Exception
    {
        //GSON Objects for writeing and dealing with json
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        JsonParser parser = new JsonParser();


        System.out.println("Scanning In Files");
        //GET Pictures JSON
        File imageJson = this.checkFor(this.imageJsonDir, "pictures.json");
        String fileContents = this.getFileContents(imageJson);
        //try parsing file with json parser
        JsonElement element = null;
        try
        {
            element = parser.parse(fileContents);
            //If file already has contents
            if (element != null &&
                    element.isJsonObject() &&
                    element.getAsJsonObject().getAsJsonArray("picturelist") != null &&
                    element.getAsJsonObject().getAsJsonArray("picturelist").isJsonArray())
            {
                ArrayList<FastPicture> fastPictures = new ArrayList<FastPicture>();
                JsonArray preInitilizedPics = element.getAsJsonObject().getAsJsonArray("picturelist");
                //Read
                for (JsonElement fastPicture : preInitilizedPics)
                {
                    fastPictures.add(gson.fromJson(fastPicture.getAsString(), FastPicture.class));
                }
                for (FastPicture picture : fastPictures)
                {
                    String[] temp = {picture.getName(), picture.getContents(), picture.getLength()};
                    this.pictureFilesContents.add(temp);
                }
                System.out.println("Done Scanning Files");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error Reading Picture from Json");
        }
    }

    public String getType(File file)
    {
        String temp = file.getName();
        while (temp.indexOf(".") > -1)
        {
            temp = temp.substring(temp.indexOf(".") + 1);
        }
        return temp;
    }

    //Returns the fileWanted if within allowedDirectory
    //No Checking of the requested file's name
    //null if not in directory
    public File getFile(String fileWanted)
    {
        File fileToGive = null;
        //Gets all files in allowedDir
        File[] filesInAllowedDir = this.listFilesRecursive(this.allowedDirectory);
        //If count is greater than 1 might have duplicate files and could be a problem
        int count = 0;

        //Try with given slashes to find file
        for (File file : filesInAllowedDir)
        {
            int indexOfFileWanted = file.getAbsolutePath().indexOf(fileWanted);
            int lengthOfAbsolutePath = file.getAbsolutePath().length();
            if (indexOfFileWanted > -1 && indexOfFileWanted + fileWanted.length() == lengthOfAbsolutePath)//MAKE SURE FILES NAMED THE SAME HAVE DIFFERENT PARENT FOLDERS
            {
                fileToGive = file;
                count++;
            }
        }
        if (fileToGive != null)
        {
            System.out.println("GET FILE CALLED -> File Retrieved: " + fileToGive.getAbsolutePath());
        }
        else// invert slashes and try to find file for different operating systems
        {
            String invertedWanted = this.invertSlashes(fileWanted);
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
        if (count > 1)
        {
            System.out.println("getFile() Count: " + count);
        }
        return fileToGive;
    }

    //For files that are for sure in shard directory
    public String getFileContents(File file)
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
            System.out.println("Error Reading File Contents\n File Path: " + file.getPath());
            e.printStackTrace();
            System.out.println(e.getCause());
        }
        return fileRequestedContents;
    }

    //Cycles thru the given string and if there are any forward slashes or backward slashes it will invert them
    public String invertSlashes(String str)
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

    public String getPictureContents(String pictureName)
    {
        String contents = "";
        for (String[] content : pictureFilesContents)
        {
            if (content[0].indexOf(pictureName) > -1)
            {
                contents = content[1];
                break;
            }
        }
        return contents;
    }

    public String getPictureLength(String pictureName)
    {
        String contents = "";
        for (String[] content : this.pictureFilesContents)
        {
            if (content[0].equals(this.getFile(pictureName).getAbsolutePath()))
            {
                contents = "" + content[1].getBytes().length;
                break;
            }
        }
        return contents;
    }

    public byte[] readPhoto(File file)
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
            System.out.println("Error Reading Photo");
            e.printStackTrace();
            System.out.println(e.getCause());
        }
        return null;
    }

    public File getLogsDir()
    {
        return this.logs;
    }

    public File getFileSafe(String fileWanted)
    {
        if (nameSafe(fileWanted))
        {
            return getFile(fileWanted);
        }
        return null;
    }

    //
    public File[] listFilesRecursive(File fileToList)
    {
        ArrayList<File> files = new ArrayList<File>();
        for (File file : fileToList.listFiles())
        {
            if (file.isDirectory())
            {
                files.addAll(Arrays.asList(this.listFilesRecursive(file)));
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

    public boolean nameSafe(String fileName)
    {
        if (fileName.contains("\\..") || fileName.contains("//.."))
        {
            return false;
        }

        return true;
    }

}
