package com.jumbodinosaurs.util;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.devlib.util.WebUtil;
import com.jumbodinosaurs.devlib.util.objects.HttpResponse;
import com.jumbodinosaurs.objects.MinecraftSign;
import com.jumbodinosaurs.objects.MinecraftWrittenBook;
import com.jumbodinosaurs.objects.PastPing;
import com.jumbodinosaurs.objects.WritablePost;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerUtil
{
    public static String host = "";
    public static File codeExecutionDir;
    public static File getDirectory;
    public static File postDirectory;
    public static File serverDataDir;
    public static File timeOutHelperDir;
    public static File userInfoDirectory;
    public static File logsDirectory;
   
    
    static
    {
        System.out.println("Setting up Server Util Files");
        codeExecutionDir = new File(System.getProperty("user.dir"));
        System.out.println("USER DIR: " + codeExecutionDir.getAbsolutePath());
        getDirectory = GeneralUtil.checkFor(codeExecutionDir, "GET", true);
        postDirectory = GeneralUtil.checkFor(codeExecutionDir, "POST", true);
        serverDataDir = GeneralUtil.checkFor(codeExecutionDir, "Server Data", true);
        timeOutHelperDir = GeneralUtil.checkFor(serverDataDir, "TimeoutHelper", true);
        userInfoDirectory = GeneralUtil.checkFor(serverDataDir, "UserInfo", true);
        logsDirectory = GeneralUtil.checkFor(serverDataDir, "Log", true);
        System.out.println("Finished Creating Server Util Files");
    }
    
    
    public static String getTypelessName(File file)
    {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }
    
    
    
    public static void writePostData(WritablePost post)
    {
        try
        {
            if(isValidWritablePost(post))
            {
                WritablePost sanitizedPost = WritablePost.getSanitized(post);
                
                
                File fileToWriteTo = GeneralUtil.checkForLocalPath(postDirectory, sanitizedPost.getLocalPath());
                String fileContents = GeneralUtil.scanFileContents(fileToWriteTo);
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
                GeneralUtil.writeContents(fileToWriteTo, contentsToWrite, false);
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
        File[] filesInPostFolder = GeneralUtil.listFilesRecursive(postDirectory);
        for(File file : filesInPostFolder)
        {
            String contents = GeneralUtil.scanFileContents(file);
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
            return new Gson().fromJson(GeneralUtil.scanFileContents(fileToRead), typeToken);
        }
        return null;
    }
    
    
    //Returns the fileWanted if it is in the directory given.
    //The Code Returns null if the file is not in the directory
    //Works with local paths
    public static File safeSearchDir(File dirToSearch, String localPath, boolean matchPath)
    {
        localPath = GeneralUtil.fixPathSeparator(localPath);
        
        
        if(localPath.indexOf(File.separator) != 0)// make helloworld/hello.json into /helloworld/hello.json
        {
            localPath = File.separator + localPath;
        }
        
        
        File fileToGive = null;
        //Gets all files in allowedDir
        File[] filesInAllowedDir = GeneralUtil.listFilesRecursive(dirToSearch);
        //If count is greater than 1 might have duplicate files and could be a problem
        int count = 0;
        
        String pathOfRequestedFile = dirToSearch.getAbsolutePath() + localPath;
        
        String allowedHiddenDir = ".well-known";
        String hiddenDirPattern = File.separator + ".";
        for(File file : filesInAllowedDir)
        {
            if(!file.getAbsolutePath().contains(hiddenDirPattern) || file.getAbsolutePath().contains(allowedHiddenDir))
            {
                if(matchPath)
                {
                    if(file.getAbsolutePath().equals(pathOfRequestedFile))
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
        }
        
        //DEBUG
        if(count > 1)
        {
            OperatorConsole.printMessageFiltered(localPath + " was found " + count + " times in " + dirToSearch.getAbsolutePath(), true, false);
        }
        return fileToGive;
    }
    
    
    public static File getLogFileFromDate(LocalDateTime sessionDate)
    {
        try
        {
            String localPath = "/Session Logs/" + sessionDate.getYear() + "/" + sessionDate.getMonth() + "/" + sessionDate
                                                                                                                       .getDayOfMonth() + "/" + sessionDate
                                                                                                                                                        .getHour() + "/" + "logs.json";
            File logFile = GeneralUtil.checkForLocalPath(logsDirectory, localPath);
            return logFile;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error getting logs.json", false, true);
        }
        
        return null;
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
            //https://www.boutell.com/newfaq/creating/domainlength.html
            if(thisPostConnection.length() > 265 || (!validateMCAddress(thisPostConnection) && !validateMCAddress(
                    thisPostConnection)))
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
                                    if(pastSign.getDateLess()
                                               .equals(temporaySanitizedSign.getDateLess()) && pastPost.getPostIdentifier()
                                                                                                       .equals(thisPostConnection))
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
            Type typeToken = new TypeToken<ArrayList<MinecraftWrittenBook>>() {}.getType();
            ArrayList<MinecraftWrittenBook> postsBooks = new Gson().fromJson(post.getContent(), typeToken);
            ArrayList<MinecraftWrittenBook> sanitizedPostsBooks = new ArrayList<MinecraftWrittenBook>();
            int maxAmountBooksPerListSent = 23328;
            int booksInList = 0;
            //https://www.google.com/search?q=maximum+file+name+windows&rlz=1C1CHZL_enUS752US752&oq=maximum+file+name+windows&aqs=chrome..69i57j0l5.5031j0j7&sourceid=chrome&ie=UTF-8
            if(replaceUnicodeCharacters(post.getPostIdentifier()).length() > 255)
            {
                return false;
            }
            
            for(MinecraftWrittenBook book : postsBooks)
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
                else if(replaceUnicodeCharacters(book.getTitle()).length() > 16)//max book title is 16 https://minecraft.gamepedia.com/Book_and_Quill
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
                
                for(String page : book.getPages())
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
            
            for(WritablePost pastPost : pastPostsByThisUserAndSameObject)
            {
                ArrayList<MinecraftWrittenBook> pastBookListPost = new Gson().fromJson(pastPost.getContent(),
                                                                                       typeToken);
                if(sanitizedPostsBooks.size() == pastBookListPost.size())
                {
                    if(pastPost.getPostIdentifier().equals(rewriteHTMLEscapeCharacters(post.getPostIdentifier())))
                    {
                        boolean hasBookMatch = false;
                        for(MinecraftWrittenBook book : sanitizedPostsBooks)
                        {
                            for(MinecraftWrittenBook pastPostedBook : pastBookListPost)
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
    
    
    public static String pingMCServer(String ip, int port)
    {
        String response = null;
        try
        {
            //First Handshake Packet Creation
            int packetID = 0;
            int protocolVersion = 340;
            int nextState = 1;
            ByteArrayOutputStream handshakePacketBytesStream = new ByteArrayOutputStream();
            DataOutputStream handshakePacketBytes = new DataOutputStream(handshakePacketBytesStream);
            handshakePacketBytes.writeByte(packetID);
            MinecraftPacketUtil.writeVarInt(handshakePacketBytes, protocolVersion);
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
        File pastMcPingsFile = GeneralUtil.checkForLocalPath(logsDirectory, "/pings/mcpings.json");
        String contents = GeneralUtil.scanFileContents(pastMcPingsFile);
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
                if(thisPing.getTimesPinged() < 50 || now.minusDays((long) 2).isAfter(thisPing.getDate()))
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
            else if(now.minusDays((long) 20).isAfter(thisPing.getDate()))
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
        GeneralUtil.writeContents(pastMcPingsFile, pastPingsJsonString, false);
        return thisPing.isGoodPing();
    }
    
    
    public static String rewriteHTMLEscapeCharacters(String postData)
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
    
    
    //http://checkip.amazonaws.com/
    //http://bot.whatismyipaddress.com
    public static void setHost()
    {
        try
        {
            URL address = new URL("http://checkip.amazonaws.com/");
            HttpURLConnection connection = (HttpURLConnection) address.openConnection();
            HttpResponse ipResponse = WebUtil.getResponse(connection);
            host = ipResponse.getResponse();
            OperatorConsole.printMessageFiltered("Public IP: " + host, false, false);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Setting Host", false, true);
        }
    }
    
    
    public static String getHost()
    {
        return host;
    }
    
   
    
    
}




