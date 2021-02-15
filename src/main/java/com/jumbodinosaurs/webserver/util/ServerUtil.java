package com.jumbodinosaurs.webserver.util;

import com.google.gson.Gson;
import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.reflection.ResourceLoaderUtil;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.devlib.util.WebUtil;
import com.jumbodinosaurs.devlib.util.objects.HttpResponse;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
        LogManager.consoleLogger.debug("Setting up Server Util Files");
        codeExecutionDir = new File(System.getProperty("user.dir"));
        LogManager.consoleLogger.debug("USER DIR: " + codeExecutionDir.getAbsolutePath());
        getDirectory = GeneralUtil.checkFor(codeExecutionDir, "GET", true);
        postDirectory = GeneralUtil.checkFor(codeExecutionDir, "POST", true);
        serverDataDir = GeneralUtil.checkFor(codeExecutionDir, "Server Data", true);
        timeOutHelperDir = GeneralUtil.checkFor(serverDataDir, "TimeoutHelper", true);
        userInfoDirectory = GeneralUtil.checkFor(serverDataDir, "UserInfo", true);
        logsDirectory = GeneralUtil.checkFor(serverDataDir, "Log", true);
        LogManager.consoleLogger.debug("Finished Creating Server Util Files");
    
    }
    
    
    public static String getTypelessName(File file)
    {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
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
            LogManager.consoleLogger.debug(localPath +
                                           " was found " +
                                           count +
                                           " times in " +
                                           dirToSearch.getAbsolutePath());
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
            LogManager.consoleLogger.error("Error getting logs.json", e);
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
            fileInputStream.close();
        }
        catch(IOException e)
        {
            LogManager.consoleLogger.error(e.getMessage(), e);
        }
        return fileContents;
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
            imageStream.close();
            return imageBytes;
        }
        catch(Exception e)
        {
            LogManager.consoleLogger.error("Error Reading Photo", e);
            
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
            LogManager.consoleLogger.error("Error Hashing Password", e);
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
            host = host.replace("\n","" );
            host =  host.replace("\r","" );
            LogManager.consoleLogger.info("Public IP: " + host);
    
        }
        catch(Exception e)
        {
            LogManager.consoleLogger.error("Error Setting Host", e);
        }
    }
    
    
    public static String getHost()
    {
        return host;
    }
    
    
    public static HttpResponse sendLocalHostPostRequest(PostRequest postRequest)
            throws IOException
    {
        
        String url = "http://localhost/";
        DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.POST, url);
        
        ResourceLoaderUtil resourceLoader = new ResourceLoaderUtil();
        String response = "";
        int status = 400;
        URL address = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        String message = new Gson().toJson(postRequest);
        byte[] postData = message.getBytes(StandardCharsets.UTF_8);
        connection.setDoOutput(true);
        try(DataOutputStream wr = new DataOutputStream(connection.getOutputStream()))
        {
            
            wr.write(postData);
        }
        
        
        status = connection.getResponseCode();
        InputStream input;
        
        if(status == 200)
        {
            input = connection.getInputStream();
        }
        else
        {
            input = connection.getErrorStream();
        }
        
        while(input.available() > 0)
        {
            response += (char) input.read();
        }
        
        return new HttpResponse(status, response);
    }
    
}




