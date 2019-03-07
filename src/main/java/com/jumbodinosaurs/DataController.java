package com.jumbodinosaurs;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class DataController
{
    public static String host = "";
    private static File codeExecutionDir = new File(System.getProperty("user.dir")).getParentFile();
    private static File allowedDirectory;
    private static File logsDirectory;
    private static File certificateDirectory;
    private static File userInfoDirectory;
    private static File postDirectory;
    private static File timeOutHelperDir;
    private static SessionLogger logger;
    private static CredentialsManager credentialsManager;

    public DataController(boolean makePageWithDomains)
    {
        try
        {

            this.allowedDirectory = this.checkFor(this.codeExecutionDir, "Shared");
            this.certificateDirectory = this.checkFor(this.codeExecutionDir, "Certificates");
            this.userInfoDirectory = this.checkFor(this.codeExecutionDir, "UserInfo");
            this.postDirectory = this.checkFor(this.codeExecutionDir, "Post");
            this.timeOutHelperDir = this.checkFor(this.codeExecutionDir, "TimeoutHelper");

            this.credentialsManager = new CredentialsManager();

            OperatorConsole.printMessageFiltered(this.allowedDirectory.getAbsolutePath(), true, false);
            this.logsDirectory = checkFor(this.allowedDirectory.getParentFile(), "LOG");
            this.setHost();
            if(makePageWithDomains)
            {
                this.makeSiteIndexand404PageDomains();
            }
            else
            {
                this.makeSiteIndexand404PageDefault();
            }
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



    public static File[] getCertificates()
    {
        return listFilesRecursive(certificateDirectory);
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


    /*
         tl;dr it checks the given file for the given local path and makes it if it's not there

         Example: /home/systemop -> File file
                  /stats/home.txt -> String localPath
                  checks for /home/systemop/stats/home.txt
                  if not there then it will check and make each file in the local path

                  Local Paths Should already have the operating systems File Path Separator in them
                  and start with the File Path Separator

                  This function should never have Non-Operator Made localPaths Run thru it.
     */
    public static File checkForLocalPath(File file, String localPath)
    {
        File[] files = listFilesRecursive(file);
        String pathOfRequestedFile = file.getAbsolutePath() + localPath;
        for (File subFile : files)
        {
            if (subFile.getAbsolutePath().equals(pathOfRequestedFile))
            {
                return subFile;
            }
        }

        ArrayList<String> levels = new ArrayList<String>();
        String temp = localPath;
        String level = "";

        while (temp.contains(File.separator))
        {
            int indexOfSlash = temp.indexOf(File.separator);
            if (temp.substring(indexOfSlash + 1).contains(File.separator))
            {
                level = temp.substring(indexOfSlash + 1, temp.substring(indexOfSlash + 1).indexOf(File.separator));
            }
            else
            {
                level = temp.substring(indexOfSlash + 1);
            }
            levels.add(level);
        }

        File lastParent = file;
        for (String subPath : levels)
        {
            try
            {
                File fileToMake = checkFor(lastParent, subPath);
                lastParent = fileToMake;
            }
            catch (Exception e)
            {
                OperatorConsole.printMessageFiltered("Error checking for Local Path", false, true);
                e.printStackTrace();
            }

        }
        return lastParent;
    }

    public static String fixPathSeparator(String path)
    {


        char[] charToChange = path.toCharArray();
        if (File.separator.equals("\\"))
        {
            for (int i = 0; i < charToChange.length; i++)
            {
                if (charToChange[i] == '/')
                {
                    charToChange[i] = '\\';
                }
            }
        }
        else
        {
            for (int i = 0; i < charToChange.length; i++)
            {
                if (charToChange[i] == '\\')
                {
                    charToChange[i] = '/';
                }
            }
        }

        String pathToReturn = "";
        for (char character : charToChange)
        {
            pathToReturn += character;
        }
        return pathToReturn;

    }

    public static boolean isIPCaptchaLocked(String ip)
    {
        for (FloatUser user : getWatchList())
        {
            if (user.getIp().equals(ip))
            {
                return user.isCaptchaLocked();
            }
        }
        return false;
    }

    public static void strikeIP(String ip)
    {
        FloatUser newUser = new FloatUser(ip, LocalDate.now().toString(), 0, false);
        boolean newAbuser = true;
        ArrayList<FloatUser> abusers = getWatchList();
        for (FloatUser user : abusers)
        {
            if (user.equals(newUser))
            {
                user = new FloatUser(newUser.getIp(), newUser.getDate(), user.getStrikes() + 1, false);
                if (user.getStrikes() >= 15)
                {
                    user.setCaptchaLocked(true);
                }
                newAbuser = false;
                break;
            }
        }

        if (newAbuser)
        {
            newUser.setStrikes(1);
            abusers.add(newUser);
        }

        try
        {
            File fileToWriteTo = checkFor(timeOutHelperDir, "watchlist.json");
            String contentsToWrite = new Gson().toJson(abusers);
            writeContents(fileToWriteTo, contentsToWrite, false);
        }
        catch (Exception e)
        {
            OperatorConsole.printMessageFiltered("Error Getting timoutFile", false, true);
            e.printStackTrace();
        }
    }

    public static void writeContents(File fileToWrite, String contents, boolean append)
    {
        try
        {
            PrintWriter output = new PrintWriter(new FileOutputStream(fileToWrite, append));
            output.write(contents);
            output.close();
        }
        catch (Exception e)
        {
            OperatorConsole.printMessageFiltered("Error Writing to File", false, true);
        }
    }

    public static ArrayList<FloatUser> getWatchList()
    {
        ArrayList<FloatUser> watchList = new ArrayList<FloatUser>();
        try
        {
            Type typeToken = new TypeToken<ArrayList<FloatUser>>()
            {
            }.getType();
            File watchListFile = checkFor(timeOutHelperDir, "watchlist.json");
            String fileContents = getFileContents(watchListFile);
            watchList = new Gson().fromJson(fileContents, typeToken);
        }
        catch (Exception e)
        {
            OperatorConsole.printMessageFiltered("Error Reading watchlist.json", false, true);
            e.printStackTrace();
        }
        return watchList;
    }

    public static void log(Session session)
    {
        logger.addSession(session);
    }

    public static String[] getDomains()
    {
        ArrayList<Domain> hosts = ServerControl.getArguments().getDomains();
        String[] domains = new String[hosts.size()];
        for(int i = 0; i < domains.length; i++)
        {
            domains[i] = hosts.get(i).getDomain();
        }
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

    public static void writePostData(WritablePost post)
    {
        try
        {
            File fileToWriteTo = checkForLocalPath(postDirectory, fixPathSeparator(post.getLocalPath()));
            String fileContents = getFileContents(fileToWriteTo);
            Type typeToken = new TypeToken<ArrayList<WritablePost>>()
            {
            }.getType();
            ArrayList<WritablePost> pastPosts = new Gson().fromJson(fileContents, typeToken);
            if (pastPosts != null)
            {
                pastPosts.add(post);
            }
            else
            {
                pastPosts = new ArrayList<WritablePost>();
                pastPosts.add(post);
            }
            String contentsToWrite = new Gson().toJson(pastPosts);
            PrintWriter output = new PrintWriter(fileToWriteTo);
            output.write(contentsToWrite);
            output.close();
        }
        catch (Exception e)
        {
            OperatorConsole.printMessageFiltered("Error Writing Post Data", false, true);
            e.printStackTrace();
        }
    }

    //Returns the fileWanted if it is in allowedDirectory
    //The Code Returns null if the file is not in the directory
    public static File getFileFromAllowedDirectory(String fileWanted)
    {

        fileWanted = fixPathSeparator(fileWanted);

        File fileToGive = null;
        //Gets all files in allowedDir
        File[] filesInAllowedDir = listFilesRecursive(allowedDirectory);
        //If count is greater than 1 might have duplicate files and could be a problem
        int count = 0;

        //Try with given slashes to find file
        String pathofRequestedFile = allowedDirectory.getAbsolutePath() + fileWanted;
        OperatorConsole.printMessageFiltered("Path of Requested File: " + pathofRequestedFile, true, false);
        for (File file : filesInAllowedDir)
        {
            if (pathofRequestedFile.equals(file.getAbsolutePath()))
            {
                fileToGive = file;
                count++;
            }
        }

        if (fileToGive != null)
        {
            OperatorConsole.printMessageFiltered("GET FILE CALLED -> File Retrieved: " + fileToGive.getAbsolutePath(), true, false);
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
            OperatorConsole.printMessageFiltered("Error getting logs.json", false, true);
        }

        return null;
    }

    public static boolean createUser(String username, String password, String email)
    {
        return credentialsManager.createUser(username, password, email);
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
            OperatorConsole.printMessageFiltered("Error Reading File Contents", false, true);
        }
        return fileRequestedContents;
    }

    /*
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
    */

    public static byte[] readZip(File file)
    {
        byte[] fileContents = new byte[(int) file.length()];
        try
        {

            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(fileContents, 0, fileContents.length);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return fileContents;
    }


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

    public static User loginUsernamePassword(String username, String password)
    {
        return credentialsManager.loginUsernamePassword(username, password);
    }

    public static User loginToken(String token, String ip)
    {
        return credentialsManager.loginToken(token, ip);
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
            for (String domain : getDomains())
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

    private class CredentialsManager
    {
        public CredentialsManager()
        {

        }


        public User loginToken(String token, String ip)
        {
            for (User user : this.getUserList())
            {
                String password = ip + user.getTokenDate() + user.getTokenRandom();
                try
                {
                    if (PasswordStorage.verifyPassword(password, token))
                    {
                        LocalDate tokenMintDate = LocalDate.parse(user.getTokenDate());
                        LocalDate now = LocalDate.now();
                        if (now.minusDays(30).isAfter(tokenMintDate))
                        {
                            return null;
                        }
                        return user;
                    }
                }
                catch (Exception e)
                {
                    OperatorConsole.printMessageFiltered("Error Authenticating User Token", false, true);
                    e.printStackTrace();
                }
            }
            return null;
        }

        public User loginUsernamePassword(String username, String password)
        {
            for (User user : this.getUserList())
            {
                if (user.getUsername().equals(username))
                {
                    try
                    {
                        if (PasswordStorage.verifyPassword(password, user.getPassword()))
                        {
                            return user;
                        }
                    }
                    catch (Exception e)
                    {
                        OperatorConsole.printMessageFiltered("Error Authenticating User", false, true);
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        public void addUser(User userToAdd)
        {
            ArrayList<User> users = getUserList();
            users.add(userToAdd);
            String listToWrite = new Gson().toJson(users);
            try
            {
                File usersInfo = checkFor(userInfoDirectory, "userinfo.json");
                PrintWriter output = new PrintWriter(usersInfo);
                output.write(listToWrite);
                output.close();
            }
            catch (Exception e)
            {
                OperatorConsole.printMessageFiltered("Error writing new User to UserList", false, true);
                e.printStackTrace();
            }
        }

        public ArrayList<User> getUserList()
        {
            ArrayList<User> users = new ArrayList<User>();
            try
            {
                File usersInfo = checkFor(userInfoDirectory, "userinfo.json");
                String fileContents = getFileContents(usersInfo);
                Type typeToken = new TypeToken<ArrayList<User>>()
                {
                }.getType();
                users = new Gson().fromJson(fileContents, typeToken);
            }
            catch (Exception e)
            {
                OperatorConsole.printMessageFiltered("Error Reading User Info", false, true);
                e.printStackTrace();
            }
            return users;
        }


        public boolean createUser(String username, String password, String email)
        {
            String whiteListedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";

            ArrayList<User> users = getUserList();

            for (User user : users)
            {
                if (username.equals(user.getUsername()))
                {
                    return false;
                }

                if (email.equals(user.getEmail()))
                {
                    return false;
                }
            }

            for (char character : username.toCharArray())
            {
                if (!whiteListedCharacters.contains("" + character))
                {
                    return false;
                }
            }

            try
            {
                User newUser = new User(username,
                        PasswordStorage.createHash(password),
                        LocalDate.now().toString(),
                        User.generateRandom(),
                        email,
                        0,
                        false);
                addUser(newUser);
                return true;
            }
            catch (Exception e)
            {
                OperatorConsole.printMessageFiltered("Error creating new User", false, true);
                e.printStackTrace();
            }
            return false;
        }
    }

}
