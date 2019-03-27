package com.jumbodinosaurs.util;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.ServerControl;
import com.jumbodinosaurs.objects.Domain;
import com.jumbodinosaurs.objects.Email;
import com.jumbodinosaurs.objects.Session;
import com.jumbodinosaurs.objects.WritablePost;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataController
{
    public static String host = "";
    public static File codeExecutionDir = new File(System.getProperty("user.dir")).getParentFile();
    public static File getDirectory = checkFor(codeExecutionDir, "Shared");
    public static File logsDirectory;
    public static File certificateDirectory = checkFor(codeExecutionDir, "Certificates");
    public static File userInfoDirectory = checkFor(codeExecutionDir, "UserInfo");
    public static File postDirectory = checkFor(codeExecutionDir, "Post");
    public static File timeOutHelperDir = checkFor(codeExecutionDir, "TimeoutHelper");
    private static SessionLogger logger;
    private static CredentialsManager credentialsManager;
    
    public DataController(boolean makePageWithDomains)
    {
        try
        {
            credentialsManager = new CredentialsManager();
            
            OperatorConsole.printMessageFiltered(getDirectory.getAbsolutePath(), true, false);
            logsDirectory = checkFor(getDirectory.getParentFile(), "LOG");
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
            logger = new SessionLogger();
            Thread loggerThread = new Thread(logger);
            loggerThread.start();
        }
        catch(Exception e)
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
    public static File checkFor(File file, String name)
    {
        boolean needToMakeFile = true;
        String[] contentsOfFile = file.list();
        for(int i = 0; i < contentsOfFile.length; i++)
        {
            if(contentsOfFile.equals(name))
            {
                needToMakeFile = false;
            }
        }
        
        File neededFile = new File(file.getPath() + "/" + name);
        if(needToMakeFile)
        {
            if(name.indexOf(".") >= 0)
            {
                try
                {
                    neededFile.createNewFile();
                }
                catch(Exception e)
                {
                    OperatorConsole.printMessageFiltered("Error Createing File", false, true);
                }
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

                
                  This function should never have Non-Operator Made localPaths Run thru it.

     */
    public static File checkForLocalPath(File file, String localPath)
    {
        
        ArrayList<String> levels = new ArrayList<String>();
        String temp = localPath;
        String level = "";
        if(temp.indexOf(File.separator) != 0)
        {
            temp = File.separator + temp;
        }
        
        if(temp.lastIndexOf(File.separator) != temp.length())
        {
            temp += File.separator;
        }
        
        char[] tempchars = temp.toCharArray();
        int indexOfLastSlash = 0;
        for(int i = 1; i < temp.length(); i++)
        {
            if(tempchars[i] == File.separatorChar)
            {
                level = temp.substring(indexOfLastSlash + 1, i);
                levels.add(level);
                indexOfLastSlash = i;
            }
        }
        
        File lastParent = file;
        for(String subPath : levels)
        {
            try
            {
                File fileToMake = checkFor(lastParent, subPath);
                lastParent = fileToMake;
            }
            catch(Exception e)
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
        if(File.separator.equals("\\"))
        {
            for(int i = 0; i < charToChange.length; i++)
            {
                if(charToChange[i] == '/')
                {
                    charToChange[i] = '\\';
                }
            }
        }
        else
        {
            for(int i = 0; i < charToChange.length; i++)
            {
                if(charToChange[i] == '\\')
                {
                    charToChange[i] = '/';
                }
            }
        }
        
        String pathToReturn = "";
        for(char character : charToChange)
        {
            pathToReturn += character;
        }
        return pathToReturn;
        
    }
    
    
    public static void writeContents(File fileToWrite, String contents, boolean append)
    {
        try
        {
            PrintWriter output = new PrintWriter(new FileOutputStream(fileToWrite, append));
            output.write(contents);
            output.close();
        }
        catch(Exception e)
        {
            OperatorConsole.printMessageFiltered("Error Writing to File", false, true);
        }
    }
    
    
    public static void log(Session session)
    {
        logger.addSession(session);
    }
    
    public static String[] getDomains()
    {
        String[] domains = null;
        if(ServerControl.getArguments() != null && ServerControl.getArguments().getDomains() != null)
        {
            ArrayList<Domain> hosts = ServerControl.getArguments().getDomains();
            domains = new String[hosts.size()];
            for(int i = 0; i < domains.length; i++)
            {
                domains[i] = hosts.get(i).getDomain();
            }
        }
        return domains;
    }
    
    public static String getType(File file)
    {
        String temp = file.getName();
        while(temp.indexOf(".") > -1)
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
            if(pastPosts != null)
            {
                pastPosts.add(post);
            }
            else
            {
                pastPosts = new ArrayList<WritablePost>();
                pastPosts.add(post);
            }
            String contentsToWrite = new Gson().toJson(pastPosts);
            writeContents(fileToWriteTo, contentsToWrite, false);
        }
        catch(Exception e)
        {
            OperatorConsole.printMessageFiltered("Error Writing Post Data", false, true);
            e.printStackTrace();
        }
    }
    
    public static ArrayList<WritablePost> getAllPostsList()
    {
        ArrayList<WritablePost> allPastPosts = new ArrayList<WritablePost>();
        File[] filesInPostFolder = listFilesRecursive(postDirectory);
        for(File file : filesInPostFolder)
        {
            String contents = DataController.getFileContents(file);
            Type typeToken = new TypeToken<ArrayList<WritablePost>>(){}.getType();
            try
            {
                ArrayList<WritablePost> pastPosts = new Gson().fromJson(contents, typeToken);
                allPastPosts.addAll(pastPosts);
            }
            catch(JsonParseException e)
            {
                OperatorConsole.printMessageFiltered("A File in POST Dir is not a WritablePost[]", false, true);
            }
        }
        return allPastPosts;
    }
    
    //Returns the fileWanted if it is in getDirectory
    //The Code Returns null if the file is not in the directory
    //Works with local paths
    public static File getFileFromGETDirectory(String fileWanted)
    {
        
        fileWanted = fixPathSeparator(fileWanted);
        
        File fileToGive = null;
        //Gets all files in allowedDir
        File[] filesInAllowedDir = listFilesRecursive(getDirectory);
        //If count is greater than 1 might have duplicate files and could be a problem
        int count = 0;
        
        //Try with given slashes to find file
        String pathofRequestedFile = getDirectory.getAbsolutePath() + fileWanted;
        for(File file : filesInAllowedDir)
        {
            if(file.getAbsolutePath().equals(pathofRequestedFile))
            {
                fileToGive = file;
                count++;
            }
        }
        
        //DEBUG
        if(count > 1)
        {
            System.out.println("getFileFromGETDirectory() Count: " + count);
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
        catch(Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error getting logs.json", false, true);
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
            while(input.hasNextLine())
            {
                fileRequestedContents += input.nextLine();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Reading File Contents", false, true);
        }
        return fileRequestedContents;
    }
    
    public static byte[] readZip(File file)
    {
        byte[] fileContents = new byte[(int) file.length()];
        try
        {
            
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(fileContents, 0, fileContents.length);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return fileContents;
    }
    
    
    //https://dzone.com/articles/sending-mail-using-javamail-api-for-gmail-server
    public static boolean sendEmail(String userEmailAddress, String topic, String message)
    {
        if(ServerControl.getArguments() != null && ServerControl.getArguments().getEmails() != null && ServerControl.getArguments().getEmails().size() > 0)
        {
            Email email = ServerControl.getArguments().getEmails().get(0);
            String emailUsername, emailPassword;
            emailUsername = email.getUsername();
            emailPassword = email.getPassword();
            //Setting up configurations for the email connection to the Google SMTP server using TLS
            Properties props = new Properties();
            props.put("mail.smtp.host", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            //Establishing a session with required user details
            javax.mail.Session session = javax.mail.Session.getInstance(props, new javax.mail.Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(emailUsername, emailPassword);
                }
            });
            try
            {
                //Creating a Message object to set the email content
                MimeMessage msg = new MimeMessage(session);
                //Storing the comma seperated values to email addresses
                String to = userEmailAddress;
                /*Parsing the String with defualt delimiter as a comma by marking the boolean as true and storing the email
                addresses in an array of InternetAddress objects*/
                InternetAddress[] address = InternetAddress.parse(to, true);
                //Setting the recepients from the address variable
                msg.setRecipients(Message.RecipientType.TO, address);
                
                msg.setSubject(topic);
                msg.setSentDate(new Date());
                msg.setText(message);
                msg.setHeader("XPriority", "1");
                Transport.send(msg);
                return true;
            }
            catch(Exception e)
            {
                OperatorConsole.printMessageFiltered("Error Sending E-Mail", false, true);
                e.printStackTrace();
            }
        }
        return false;
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
        catch(Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Reading Photo", false, true);
            
        }
        return null;
    }
    
    public static File[] listFilesRecursive(File directory)
    {
        ArrayList<File> files = new ArrayList<File>();
        for(File file : directory.listFiles())
        {
            if(file.isDirectory())
            {
                files.addAll(Arrays.asList(listFilesRecursive(file)));
            }
            else
            {
                files.add(file);
            }
        }
        File[] filesToReturn = new File[files.size()];
        for(int i = 0; i < files.size(); i++)
        {
            filesToReturn[i] = files.get(i);
        }
        return filesToReturn;
    }



    /*
    public String getPictureLength(String pictureName)
    {
        String contents = "";
        for (String[] content : this.pictureFilesContents)
        {
            if (content[0].equals(this.getFileFromGETDirectory(pictureName).getAbsolutePath()))
            {
                contents = "" + content[1].getBytes().length;
                break;
            }
        }
        return contents;
    }
    */
    
    public static StringBuffer removeUTFCharacters(String data)
    {
        Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
        Matcher m = p.matcher(data);
        StringBuffer buf = new StringBuffer(data.length());
        while(m.find())
        {
            String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
            m.appendReplacement(buf, Matcher.quoteReplacement(ch));
        }
        m.appendTail(buf);
        return buf;
    }
    
    
    //Takes the given password and returns a GSON safe hash
    public static String safeHashPassword(String password)
    {
        try
        {
            String hashPassword = PasswordStorage.createHash(password);
            String hashPasswordTemp;
            String gsonPassword = new Gson().toJson(hashPassword);
            hashPasswordTemp = "\"" + hashPassword + "\"";
            if(hashPasswordTemp.equals(removeUTFCharacters(gsonPassword).toString()))
            {
                return hashPassword;
            }
            else
            {
                return safeHashPassword(password);
            }
        }
        catch(Exception e)
        {
            OperatorConsole.printMessageFiltered("Error Hashing Password", false, true);
            e.printStackTrace();
        }
        return "";
    }
    
    public static CredentialsManager getCredentialsManager()
    {
        return credentialsManager;
    }
    
    private void setHost()
    {
        try
        {
            URL address = new URL("http://bot.whatismyipaddress.com");
            
            BufferedReader sc = new BufferedReader(new InputStreamReader(address.openStream()));
            
            host = sc.readLine().trim();
            OperatorConsole.printMessageFiltered("Public IP: " + host, false, false);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Setting Host", false, true);
        }
    }

    /*
    public static void writeSilentConsole(String message)
    {
        try
        {
            if(logsDirectory != null)
            {
                File fileToWriteTo = checkFor(logsDirectory, "silentconsole.json");
                String contents = getFileContents(fileToWriteTo);
                LocalDateTime now = LocalDateTime.now();
                SilentConsoleMessage consoleMessage = new SilentConsoleMessage(message, now.toString());
                Type type = new TypeToken<ArrayList<SilentConsoleMessage>>(){}.getType();
                ArrayList<SilentConsoleMessage> messages = new Gson().fromJson(contents, type);
                if(messages != null)
                {
                    messages.add(consoleMessage);
                }
                else
                {
                    messages = new ArrayList<SilentConsoleMessage>();
                    messages.add(consoleMessage);
                }
                String messagesJson = new Gson().toJson(messages);
                writeContents(fileToWriteTo, messagesJson, false);
            }

        }
        catch (Exception e)
        {
            System.out.println("Problems with silent console");
            e.printStackTrace();
        }
    }
    */
    
    public String getHost()
    {
        return host;
    }
    
    public void makeSiteIndexand404PageDefault()
    {
        try
        {
            File pageIndex = checkFor(getDirectory, "index.html");
            String indexHTML = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "<style>\n" + "body\n" + "{\n" + "    background-color: lightgreen;\n" + "}\n" + "</style>\n" + "<title>\n" + "Index\n" + "</title>\n" + "</head>\n" + "<body>\n" + "<h4>\n" + "Sites<br>";
            for(File file : listFilesRecursive(getDirectory))
            {
                indexHTML += "<a href = http://" + host + "/" + file.getName() + ">" + host + "/" + file.getName() + "</a><br>";
            }
            
            indexHTML += "</h4>\n" + "</body>\n" + "</html>";
            writeContents(pageIndex, indexHTML, false);
            
            File page404 = checkFor(getDirectory, "404.html");
            String HTML404 = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "<style>\n" + "body\n" + "{\n" + "    background-color: lightgreen;\n" + "}\n" + "</style>\n" + "<title>\n" + "404 :(\n" + "</title>\n" + "</head>\n" + "<body>\n" + "<h1>\n" + "404 - File has Either Been Moved or Relocated\n" + "</h1>\n" + "<a href = \"http://" + host + "/index.html\">Index</a>\n" + "</body>\n" + "</html>";
            writeContents(page404, HTML404, false);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Creating Index and 404 Page", false, true);
        }
    }
    
    public void makeSiteIndexand404PageDomains()
    {
        try
        {
            File pageIndex = checkFor(getDirectory, "index.html");
            String indexHTML = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "<style>\n" + "body\n" + "{\n" + "    background-color: lightgreen;\n" + "}\n" + "</style>\n" + "<title>\n" + "Index\n" + "</title>\n" + "</head>\n" + "<body>\n" + "<h4>\n" + "Sites<br>";
            for(String domain : getDomains())
            {
                indexHTML += "<a href = http://" + domain + ">" + domain + "</a><br>";
            }
            indexHTML += "</h4>\n" + "</body>\n" + "</html>";
            writeContents(pageIndex, indexHTML, false);
            
            File page404 = checkFor(getDirectory, "404.html");
            String HTML404 = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "<style>\n" + "body\n" + "{\n" + "    background-color: lightgreen;\n" + "}\n" + "</style>\n" + "<title>\n" + "404 :(\n" + "</title>\n" + "</head>\n" + "<body>\n" + "<h1>\n" + "404 - File has Either Been Moved or Relocated\n" + "</h1>\n" + "<a href = \"http://" + host + "/index.html\">Index</a>\n" + "</body>\n" + "</html>";
            writeContents(page404, HTML404, false);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Creating Index and 404 Page", false, true);
        }
    }
    
    
}




