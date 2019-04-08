package com.jumbodinosaurs.util;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.ServerControl;
import com.jumbodinosaurs.netty.HTTPSRequest;
import com.jumbodinosaurs.objects.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataController
{
    public static String host = "";
    public static File codeExecutionDir = new File(System.getProperty("user.dir")).getParentFile();
    public static File getDirectory = checkFor(codeExecutionDir, "Shared");
    public static File logsDirectory = checkFor(codeExecutionDir, "LOG");
    public static File certificateDirectory = checkFor(codeExecutionDir, "Certificates");
    public static File userInfoDirectory = checkFor(codeExecutionDir, "UserInfo");
    public static File postDirectory = checkFor(codeExecutionDir, "Post");
    public static File timeOutHelperDir = checkFor(codeExecutionDir, "TimeoutHelper");
    private static SessionLogger logger;
    private static PostWriter postWriter;
    private static CredentialsManager credentialsManager;
    
    public DataController(boolean makePageWithDomains)
    {
        try
        {
            credentialsManager = new CredentialsManager();
            OperatorConsole.printMessageFiltered("Code Execution Dir: " + codeExecutionDir.getAbsolutePath(),
                                                 true,
                                                 false);
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
            
            postWriter = new PostWriter();
            Thread postWriterThread = new Thread(postWriter);
            postWriterThread.start();
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
    public static File checkFor(File file,
                                String name)
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
    public static File checkForLocalPath(File file,
                                         String localPath)
    {
        localPath = fixPathSeparator(localPath);
        ArrayList<String> levels = new ArrayList<String>();
        String temp = localPath;
        String level = "";
        
        if(temp.indexOf(File.separator) != 0)// make helloworld/hello.json into /helloworld/hello.json
        {
            temp = File.separator + temp;
        }
        
        if(temp.lastIndexOf(File.separator) != temp.length())// make /helloworld/hello.json into /helloworld/hello.json/
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
    
    
    public static void writeContents(File fileToWrite,
                                     String contents,
                                     boolean append)
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
    
    
    public static void handOffPost(WritablePost post)
    {
        postWriter.addPost(post);
    }
    
    public static void writePostData(WritablePost post)
    {
        try
        {
            if(isValidWritablePost(post))
            {
                WritablePost sanitizedPost = WritablePost.getSanitized(post);
                
                
                File fileToWriteTo = checkForLocalPath(postDirectory, fixPathSeparator(sanitizedPost.getLocalPath()));
                String fileContents = getFileContents(fileToWriteTo);
                Type typeToken = new TypeToken<ArrayList<WritablePost>>()
                {}.getType();
                ArrayList<WritablePost> pastPosts = new Gson().fromJson(fileContents, typeToken);
                if(pastPosts != null)
                {
                    pastPosts.add(sanitizedPost);
                }
                else
                {
                    pastPosts = new ArrayList<WritablePost>();
                    pastPosts.add(sanitizedPost);
                }
                String contentsToWrite = new Gson().toJson(pastPosts);
                writeContents(fileToWriteTo, contentsToWrite, false);
            }
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
            Type typeToken = new TypeToken<ArrayList<WritablePost>>()
            {}.getType();
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
    
    
    public static ArrayList<WritablePost> getPastPostsFromPath(String localPath)
    {
        File fileToRead = safeSearchDir(postDirectory, localPath, false);
        if(fileToRead != null)
        {
            Type typeToken = new TypeToken<ArrayList<WritablePost>>()
            {}.getType();
            return new Gson().fromJson(getFileContents(fileToRead), typeToken);
        }
        return null;
    }
    
    
    //Returns the fileWanted if it is in the directory given.
    //The Code Returns null if the file is not in the directory
    //Works with local paths
    public static File safeSearchDir(File dirToSearch,
                                     String localPath,
                                     boolean matchPath)
    {
        
        localPath = fixPathSeparator(localPath);
        
        File fileToGive = null;
        //Gets all files in allowedDir
        File[] filesInAllowedDir = listFilesRecursive(dirToSearch);
        //If count is greater than 1 might have duplicate files and could be a problem
        int count = 0;
        
        String pathofRequestedFile = dirToSearch.getAbsolutePath() + localPath;
        
        for(File file : filesInAllowedDir)
        {
            if(matchPath)
            {
                if(file.getAbsolutePath().equals(pathofRequestedFile))
                {
                    fileToGive = file;
                    count++;
                }
            }
            else
            {
                
                String pathToCheck = file.getAbsolutePath().substring(dirToSearch.getAbsolutePath().length());
                if(pathToCheck.contains(localPath))
                {
                    fileToGive = file;
                    count++;
                }
            }
        }
        
        //DEBUG
        if(count > 1)
        {
            System.out.println(localPath + " was found " + count + " times in " + dirToSearch.getAbsolutePath());
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
    public static boolean sendEmail(String userEmailAddress,
                                    String topic,
                                    String message)
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
    
    
    
    //Post should be AS given by the user
    //in other words don't rewrite html escape chars or unicode
    public static boolean isValidWritablePost(WritablePost post)
    {
        LocalDateTime now = LocalDateTime.now();
        ArrayList<WritablePost> pastPosts = getAllPostsList();
        ArrayList<WritablePost> pastPostsByThisUserAndSameObject = new ArrayList<WritablePost>();
        if(pastPosts == null)
        {
            pastPosts = new ArrayList<WritablePost>();
        }
        
        for(WritablePost pastPost : pastPosts)
        {
            if(pastPost.getUser().equals(post.getUser()) && pastPost.getObjectType().equals(post.getObjectType()))
            {
                pastPostsByThisUserAndSameObject.add(pastPost);
            }
        }
        
        if(post.getObjectType().equals(MinecraftSign.class.getTypeName()))//MC SIGNS
        {
            //I don't want your single player signs there are better
            // ways of getting them with tools from https://www.reddit.com/r/MinecraftDataMining/
            if(post.getPostIdentifier().equals("singleplayer"))
            {
                return false;
            }
            String thisPostConnection = rewriteHTMLEscapeCharacters(post.getPostIdentifier());
    
            if(thisPostConnection.length() > 265 || //https://www.boutell.com/newfaq/creating/domainlength.html
                       (!validateMCAddress(thisPostConnection) && !validateMCAddress(thisPostConnection)))
            {
                return false;
            }
            
            MinecraftSign sign = new Gson().fromJson(post.getContent(), MinecraftSign.class);
            for(String text : sign.getTexts())
            {
                if(!(replaceUnicodeCharacters(text).length() < 17)) //if any of the texts are over 16 in length then
                    // not valid vanilla sign
                {
                    return false;
                }
            }
            
            int thirtyMillion = 30000005;//Add that 5 cause ...
            if(sign.getX() >= (-1 * thirtyMillion) && sign.getX() <= thirtyMillion) //Vanilla Mc World Border
            {
               
                if(sign.getZ() >= (-1 * thirtyMillion) && sign.getZ() <= thirtyMillion)
                {
                   
                    if(sign.getY() < 256 && sign.getY() >= 0)//vanilla mc world height
                    {
                        
                        if(-1 <= sign.getDimension() && 1 >= sign.getDimension())//Vanilla mc has 3 dimensions -1, 0 , 1
                        {
                            
                            //if you post a sign from the future or from before mc went online....
                            LocalDateTime minecraftOnlineReleaseDate = LocalDateTime.parse("2009-05-17T00:00:00.000");
                            if(sign.getDate().isAfter(minecraftOnlineReleaseDate) && sign.getDate().isBefore(now))
                            {
                                
                                //if it's the same connection and same dateless sign as all past signs return false
                                boolean spamSign = false;
                                MinecraftSign temporaySanitizedSign = MinecraftSign.getSanitizedSign(sign);
                                for(WritablePost pastPost : pastPostsByThisUserAndSameObject)
                                {
                                    MinecraftSign pastSign = new Gson().fromJson(pastPost.getContent(),
                                                                                 MinecraftSign.class);
                                    if(pastSign.getDateLess().equals(temporaySanitizedSign.getDateLess()) && pastPost.getPostIdentifier().equals(
                                            thisPostConnection))
                                    {
                                        spamSign = true;
                                    }
                                }
                                return !spamSign;
                            }
                           
                        }
                        
                    }
                    
                }
                
            }
            
        }
        else if(post.getObjectType().equals(MinecraftWrittenBook.class.getTypeName()))//MC BOOKS
        {
            Type typeToken = new TypeToken<ArrayList<MinecraftWrittenBook>>(){}.getType();
            ArrayList<MinecraftWrittenBook> postsBooks = new Gson().fromJson(post.getContent(), typeToken);
            ArrayList<MinecraftWrittenBook> sanitizedPostsBooks = new ArrayList<MinecraftWrittenBook>();
            int maxAmountBooksPerListSent = 23328;
            int booksInList = 0;
            //https://www.google.com/search?q=maximum+file+name+windows&rlz=1C1CHZL_enUS752US752&oq=maximum+file+name+windows&aqs=chrome..69i57j0l5.5031j0j7&sourceid=chrome&ie=UTF-8
            if(replaceUnicodeCharacters(post.getPostIdentifier()).length() > 255)
            {
                return false;
            }
            
            for(MinecraftWrittenBook book: postsBooks)
            {
                int generation = Integer.parseInt(book.getGeneration());
                if(book.getCount() < 1)
                {
                    return false;
                }
                
                booksInList += book.getCount();
                
                if(book.getPages().size() > 100)
                {
                    return false;
                }
                // you may say we should check characters since mc doesn't allow specfic chars in usernames but
                //https://imgur.com/a/Q6ubP3g
                else if(replaceUnicodeCharacters(book.getAuthor()).length() > 16)//max player name size is 16
                    // https://namemc.com/search?q=12345678911234567
                {
                    return false;
                }
                else if(replaceUnicodeCharacters(book.getTitle()).length() > 16 )//max book title is 16 https://minecraft.gamepedia.com/Book_and_Quill
                {
                    return false;
                }
                else if(0 > generation || generation > 3)//books have 4 types of generations
                {
                    return false;
                }
                else if(booksInList > maxAmountBooksPerListSent)//
                {
                    return false;
                }
                
                for(String page: book.getPages())
                {
                    String unicodeCharRemovedPage = replaceUnicodeCharacters(page).substring(0);
                    if(unicodeCharRemovedPage.length() > 255)
                    {
                        return false;
                    }
                    
                    int numberOfNewlines = 0;
                    String temp = unicodeCharRemovedPage;
                    String newlineSeq = "\n";
                    while(temp.contains(newlineSeq))//Checking the amount of newLineSequences
                    {
                        numberOfNewlines++;
                        temp = temp.substring(temp.indexOf(newlineSeq) + newlineSeq.length());
                    }
                    if(numberOfNewlines > 14)
                    {
                        return false;
                    }
                    
                    if(replaceUnicodeCharacters(page).length() > 255)//255 characters per page
                    {
                        return false;
                    }
                }
                sanitizedPostsBooks.add(MinecraftWrittenBook.getSanitizedBook(book));
            }
            
            for(WritablePost pastPost: pastPostsByThisUserAndSameObject)
            {
                ArrayList<MinecraftWrittenBook> pastBookListPost = new Gson().fromJson(pastPost.getContent(), typeToken);
                if(sanitizedPostsBooks.size() == pastBookListPost.size())
                {
                    if(pastPost.getPostIdentifier().equals(rewriteHTMLEscapeCharacters(post.getPostIdentifier())))
                    {
                        boolean hasBookMatch = false;
                        for(MinecraftWrittenBook book: sanitizedPostsBooks)
                        {
                            for(MinecraftWrittenBook pastPostedBook: pastBookListPost)
                            {
                                if(book.equals(pastPostedBook))
                                {
                                    hasBookMatch = true;
                                }
                            }
    
                            if(!hasBookMatch)
                            {
                                return true;
                            }
                        }
                        return false;
                    }
                }
            }
            
            return true;
        }
        // comments/ thread in the future
        return false;
    }
    
    
    public static String pingMCServer(String ip,
                                      int port)
    {
        String response = null;
        try
        {
            //First Handshake Packet Creation
            int packetID = 0;
            int version = 340;
            int nextState = 1;
            ByteArrayOutputStream handshakePacketBytesStream = new ByteArrayOutputStream();
            DataOutputStream handshakePacketBytes = new DataOutputStream(handshakePacketBytesStream);
            handshakePacketBytes.writeByte(packetID);
            MinecraftPacketUtil.writeVarInt(handshakePacketBytes, version);
            MinecraftPacketUtil.writeString(handshakePacketBytes, ip);
            handshakePacketBytes.writeShort(port);
            MinecraftPacketUtil.writeVarInt(handshakePacketBytes, nextState);
            
            //In and Out Streams for IP and Port
            Socket socket = new Socket(ip, port);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            
            //First Handshake Packet Sent
            MinecraftPacketUtil.writeVarInt(output, handshakePacketBytesStream.size());
            output.write(handshakePacketBytesStream.toByteArray());
            output.flush();
            
            //Request Packet Sent
            output.writeByte(0x01);
            output.writeByte(0x00);
            output.flush();
            
            
            response = input.readLine();
            input.close();
            output.close();
        }
        catch(IOException e)
        {
            OperatorConsole.printMessageFiltered("MC Ping Failed", true, false);
        }
        return response;
    }
    
    
    public static boolean validateMCAddress(String address)
    {
        String ip = address;
        int port = 25565;
        if(address.contains(":"))
        {
            ip = address.substring(0, address.indexOf(":"));
            try
            {
                port = Integer.parseInt(address.substring(address.indexOf(":") + 1));
            }
            catch(NumberFormatException e)
            {
                OperatorConsole.printMessageFiltered("Port Wasn't a Number for MC Ping", true, false);
                return false;
            }
        }
        LocalDateTime now = LocalDateTime.now();
        
        Type typeToken = new TypeToken<ArrayList<PastPing>>() {}.getType();
        File pastMcPingsFile = checkForLocalPath(logsDirectory, "/pings/mcpings.json");
        String contents = getFileContents(pastMcPingsFile);
        ArrayList<PastPing> pastPings = new Gson().fromJson(contents, typeToken);
        PastPing thisPing = null;
        
        if(pastPings == null)
        {
            pastPings = new ArrayList<PastPing>();
        }
        
        for(int i = 0; i < pastPings.size(); i++)
        {
            if(pastPings.get(i).getIp().equals(ip))
            {
                thisPing = pastPings.remove(i);
            }
        }
        
        
        String response = "";
        if(thisPing == null)
        {
            response = pingMCServer(ip, port);
            thisPing = new PastPing(ip, now, response);
        }
        else
        {
            if(!thisPing.isGoodPing())
            {
                if(thisPing.getTimesPinged() < 50 || now.minusDays((long)2).isAfter(thisPing.getDate()))
                {
                    response = pingMCServer(ip, port);
                    if(thisPing.getTimesPinged() > 49)
                    {
                        thisPing = new PastPing(ip, now, response, 0);
                    }
                    else
                    {
                        thisPing = new PastPing(ip, now, response, thisPing.getTimesPinged() + 1);
                    }
                }
                
            }
            else if(now.minusDays((long)20).isAfter(thisPing.getDate()))
            {
                response = pingMCServer(ip, port);
                thisPing = new PastPing(ip, now, response);
            }
            else if(thisPing.isGoodPing())
            {
            }
            else
            {
                OperatorConsole.printMessageFiltered("MC ping was neither good nor Bad", false, true);
                thisPing = new PastPing(ip, now, null);
            }
        }
        
        pastPings.add(thisPing);
        String pastPingsJsonString = new Gson().toJson(pastPings);
        writeContents(pastMcPingsFile, pastPingsJsonString, false);
        return thisPing.isGoodPing();
    }
    
    
    public static  String rewriteHTMLEscapeCharacters(String postData)
    {
        String[][] charsToChange = {{"&", "&amp;"}, {"<", "&lt;"}, {">", "&gt;"}, {"\"", "&quot;"}, {"\'", "&apos;"}};
        
        String temp = postData;
        
        for(int i = 0; i < temp.length(); i++)
        {
            for(String[] escapeChar : charsToChange)
            {
                if(temp.substring(i, i + 1).equals(escapeChar[0]))
                {
                    temp = temp.substring(0, i) + escapeChar[1] + temp.substring(i + 1);
                    i += charsToChange[1].length;
                }
            }
            
        }
        return temp;
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
    
    public static StringBuffer replaceUnicodeCharacters(String data)
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
            if(hashPasswordTemp.equals(replaceUnicodeCharacters(gsonPassword).toString()))
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
            String HTML404 = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "<style>\n" + "body\n" + "{\n" + "    background-color: lightgreen;\n" + "}\n" + "</style>\n" + "<title>\n" + "404 :(\n" + "</title>\n" + "</head>\n" + "<body>\n" + "<h1>\n" + "404 - File has Either Been Moved or Relocated\n" + "</h1>\n" + "<a href = \"./index.html\">Index</a>\n" + "</body>\n" + "</html>";
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
            String HTML404 = "<!DOCTYPE html>\n" + "<html>\n" + "<head>\n" + "<style>\n" + "body\n" + "{\n" + "    background-color: lightgreen;\n" + "}\n" + "</style>\n" + "<title>\n" + "404 :(\n" + "</title>\n" + "</head>\n" + "<body>\n" + "<h1>\n" + "404 - File has Either Been Moved or Relocated\n" + "</h1>\n" + "<a href = \"./index.html\">Index</a>\n" + "</body>\n" + "</html>";
            writeContents(page404, HTML404, false);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Creating Index and 404 Page", false, true);
        }
    }
    
    
}




