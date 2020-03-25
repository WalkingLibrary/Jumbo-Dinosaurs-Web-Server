package com.jumbodinosaurs.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.log.LogManager;
import com.jumbodinosaurs.post.objects.FloatUser;
import com.jumbodinosaurs.post.objects.User;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CredentialsManager
{
    
    
    
    
    
    public static synchronized int getUserCount()
    {
        return getUserList().size();
    }
    
    
    /*
     Checks current user list for old user data. if the user is in the list it replace that user with the newUserInfo
     if there is no list it makes one by adding new user

     returns true if new user is written to the user file
     */
    public static boolean modifyUser(User oldUserInfo,
                                     User newUserInfo)
    {
        ArrayList<User> users = getUserList();
        if(users.size() == 0)
        {
            users = new ArrayList<User>();
            users.add(newUserInfo);
            setUserList(users);
            return true;
        }
        else
        {
            for(int i = 0; i < users.size(); i++)
            {
                if(users.get(i).equals(oldUserInfo))
                {
                    users.remove(i);
                    users.add(newUserInfo);
                    setUserList(users);
                    return true;
                }
            }
        }
        return false;
    }
    
    public static User getUser(String username)
    {
        ArrayList<User> users = getUserList();
        
        for(User user : users)
        {
            if(user.getUsername().equals(username))
            {
                return user;
            }
        }
        
        return null;
    }
    
    public static ArrayList<User> getUserList()
    {
        ArrayList<User> users = new ArrayList<User>();
        try
        {
            File usersInfo = GeneralUtil.checkFor(ServerUtil.userInfoDirectory, "userinfo.json");
            String fileContents = GeneralUtil.scanFileContents(usersInfo);
            Type typeToken = new TypeToken<ArrayList<User>>()
            {}.getType();
            users = new Gson().fromJson(fileContents, typeToken);
            if(users == null)
            {
                users = new ArrayList<User>();
            }
            
            for(User user : users)
            {
                user.setPassword(ServerUtil.replaceUnicodeCharacters(user.getPassword()).toString());
            }
            
        }
        catch(Exception e)
        {
            LogManager.consoleLogger.error("Error Reading User Info", e);
        }
        return users;
    }
    
    public static void emailStrikeIP(String ip)
    {
        LocalDateTime now = LocalDateTime.now();
        boolean newAbuser = true;
        ArrayList<FloatUser> abusers = getWatchList();
        if(abusers != null)
        {
            for(int i = 0; i < abusers.size(); i++)
            {
                if(abusers.get(i).getIp().equals(ip))
                {
                    FloatUser repeatAbuser = abusers.get(i);
                    int emailStrikes = repeatAbuser.getEmailStrikes() + 1;
                    FloatUser updatedRepeatAbuser = new FloatUser(ip,
                                                                  now.toString(),
                                                                  now.plusDays((long) 7).toString(),
                                                                  repeatAbuser.getLoginStrikes(),
                                                                  emailStrikes,
                                                                  repeatAbuser.isCaptchaLocked(),
                                                                  emailStrikes > 15);
                    abusers.remove(i);
                    abusers.add(updatedRepeatAbuser);
                    newAbuser = false;
                    break;
                }
            }
        }
        else
        {
            abusers = new ArrayList<FloatUser>();
        }
        
        if(newAbuser)
        {
            FloatUser newUser = new FloatUser(ip,
                                              LocalDate.now().toString(),
                                              now.plusDays((long) 7).toString(),
                                              0,
                                              1,
                                              false,
                                              false);
            abusers.add(newUser);
        }
        
        setWatchList(abusers);
    }
    
    public static void loginStrikeIP(String ip)
    {
        LocalDateTime now = LocalDateTime.now();
        boolean newAbuser = true;
        ArrayList<FloatUser> abusers = getWatchList();
        if(abusers != null)
        {
            for(int i = 0; i < abusers.size(); i++)
            {
                if(abusers.get(i).getIp().equals(ip))
                {
                    FloatUser repeatAbuser = abusers.get(i);
                    int loginStrikes = repeatAbuser.getLoginStrikes() + 1;
                    FloatUser updatedRepeatAbuser = new FloatUser(ip,
                                                                  now.toString(),
                                                                  now.plusDays((long) 7).toString(),
                                                                  loginStrikes,
                                                                  repeatAbuser.getEmailStrikes(),
                                                                  loginStrikes > 15,
                                                                  repeatAbuser.isEmailQueryLocked());
                    abusers.remove(i);
                    abusers.add(updatedRepeatAbuser);
                    newAbuser = false;
                    break;
                }
            }
        }
        else
        {
            abusers = new ArrayList<FloatUser>();
        }
        
        if(newAbuser)
        {
            FloatUser newUser = new FloatUser(ip,
                                              LocalDate.now().toString(),
                                              now.plusDays((long) 7).toString(),
                                              1,
                                              0,
                                              false,
                                              false);
            abusers.add(newUser);
        }
        
        setWatchList(abusers);
    }
    
    public static void setUserList(ArrayList<User> users)
    {
        try
        {
            String listToWrite = new Gson().toJson(users);
            File usersInfo = GeneralUtil.checkFor(ServerUtil.userInfoDirectory, "userinfo.json");
            GeneralUtil.writeContents(usersInfo, listToWrite, false);
        }
        catch(Exception e)
        {
            LogManager.consoleLogger.error("Error writing to User List", e);
        }
    }
    
    public static ArrayList<FloatUser> getWatchList()
    {
        ArrayList<FloatUser> watchList = new ArrayList<FloatUser>();
        try
        {
            Type typeToken = new TypeToken<ArrayList<FloatUser>>()
            {}.getType();
            File watchListFile = GeneralUtil.checkFor(ServerUtil.timeOutHelperDir, "watchlist.json");
            String fileContents = GeneralUtil.scanFileContents(watchListFile);
            watchList = new Gson().fromJson(fileContents, typeToken);
        }
        catch(Exception e)
        {
            LogManager.consoleLogger.error("Error Reading watchlist.json", e);
      
        }
        return watchList;
    }
    
    public static void setWatchList(ArrayList<FloatUser> users)
    {
        try
        {
            File fileToWriteTo = GeneralUtil.checkFor(ServerUtil.timeOutHelperDir, "watchlist.json");
            String contentsToWrite = new Gson().toJson(users);
            GeneralUtil.writeContents(fileToWriteTo, contentsToWrite, false);
        }
        catch(Exception e)
        {
            LogManager.consoleLogger.error("Error Getting timoutFile", e);
            
        }
    }
    
    public static synchronized void addUser(User userToAdd)
    {
        ArrayList<User> users = getUserList();
        users.add(userToAdd);
        setUserList(users);
    }
    
    public static synchronized boolean usernameAvailable(String username)
    {
        ArrayList<User> users = getUserList();
        
        for(User user : users)
        {
            if(user.getUsername().equals(username))
            {
                return false;
            }
        }
        
        return true;
        
    }
    
    public static synchronized boolean emailInUse(String email)
    {
        
        ArrayList<User> users = getUserList();
        
        for(User user : users)
        {
            if(user.getEmail().equals(email))
            {
                return true;
            }
        }
        
        return false;
    }
    
    public static synchronized String getTokenOneUse(User userToMint,
                                              String ip)
    {
        String token = null;
        LocalDateTime now = LocalDateTime.now();
        String hundredCharRandom = User.generateRandom();
        String tokenTemp = hundredCharRandom.substring(0, 50);
        String tokenRandom = hundredCharRandom.substring(50);
        String password = ip + tokenTemp + now.toString() + tokenRandom;
        try
        {
            String hash = ServerUtil.safeHashPassword(password);
            User updatedUserInfo = userToMint.clone();
            updatedUserInfo.setTokenRandom(tokenRandom);
            updatedUserInfo.setTokenDate(now);
            updatedUserInfo.setToken(hash);
            updatedUserInfo.setTokenIsOneUse(true);
            //The user should only receive this minted token by email.
            updatedUserInfo.setTokenRandomToSend("");
            if(modifyUser(userToMint, updatedUserInfo))
            {
                token = tokenTemp;
            }
            else
            {
                LogManager.consoleLogger.warn("Error setting User Info");
            }
            
        }
        catch(Exception e)
        {
            LogManager.consoleLogger.error("Error getting Token", e);
        }
        
        return token;
    }
    
    public static boolean isIPCaptchaLocked(String ip)
    {
        ArrayList<FloatUser> watchlist = getWatchList();
        if(watchlist != null)
        {
            for(FloatUser user : watchlist)
            {
                if(user.getIp().equals(ip))
                {
                    return user.isCaptchaLocked();
                }
            }
        }
        return false;
    }
    
    public static boolean isIPEmailCheckLocked(String ip)
    {
        ArrayList<FloatUser> watchlist = getWatchList();
        if(watchlist != null)
        {
            for(FloatUser user : watchlist)
            {
                if(user.getIp().equals(ip))
                {
                    return user.isEmailQueryLocked();
                }
            }
        }
        return false;
    }
    
    public static boolean isAbuserUnlocked(String ip)
    {
        ArrayList<FloatUser> abusers = getWatchList();
        LocalDateTime now = LocalDateTime.now();
        if(abusers != null)
        {
            for(int i = 0; i < abusers.size(); i++)
            {
                FloatUser user = abusers.get(i);
                if(user.getIp().equals(ip))
                {
                    LocalDateTime unlockDate = LocalDateTime.parse(user.getUnlockedDate());
                    return now.isAfter(unlockDate);
                }
            }
        }
        return true;
    }
    
    public static synchronized User getUserByEmail(String email)
    {
        
        ArrayList<User> users = getUserList();
        
        for(User user : users)
        {
            if(user.getEmail().equals(email))
            {
                return user;
            }
        }
        
        return null;
    }
    
    public static synchronized User loginToken(String token,
                                        String ip)
    {
        ArrayList<User> users = getUserList();
        
        for(User user : users)
        {
            String password = ip + token + user.getTokenDate() + user.getTokenRandom();
            try
            {
                if(user.getToken() != null)
                {
                    if(PasswordStorage.verifyPassword(password, user.getToken()))
                    {
                        LocalDateTime tokenMintDate = user.getTokenDate();
                        LocalDateTime now = LocalDateTime.now();
                        if(now.minusDays(30).isAfter(tokenMintDate))
                        {
                            return null;
                        }
                        
                        if(user.isTokenIsOneUse())
                        {
                            LocalDateTime lastLogin = user.getLastLoginDate();
                            if(lastLogin != null && lastLogin.isAfter(tokenMintDate))
                            {
                                
                                return null;
                                
                            }
                            else if(now.minusHours((long) 1).isAfter(tokenMintDate))
                            {
                                return null;
                            }
                            
                        }
                        
                        if(!user.isAccountLocked())
                        {
                            User updatedUserInfo = user.clone();
                            updatedUserInfo.setLastLoginDate(now);
                            
                            if(modifyUser(user, updatedUserInfo))
                            {
                                return updatedUserInfo;
                            }
                        }
                    }
                }
            }
            catch(Exception e)
            {
                LogManager.consoleLogger.error("Error Authenticating User Token", e);
            }
        }
        
        return null;
    }
    
    public static synchronized User loginUsernamePassword(String username,
                                                   String password)
    {
        ArrayList<User> users = getUserList();
        
        for(User user : users)
        {
            if(user.getUsername().equals(username))
            {
                try
                {
                    if(PasswordStorage.verifyPassword(password, user.getPassword()))
                    {
                        if(!user.isAccountLocked())
                        {
                            LocalDateTime now = LocalDateTime.now();
                            User updatedUserInfo = user.clone();
                            updatedUserInfo.setLastLoginDate(now);
                            if(modifyUser(user, updatedUserInfo))
                            {
                                return updatedUserInfo;
                            }
                        }
                    }
                }
                catch(PasswordStorage.CannotPerformOperationException e)
                {
                    LogManager.consoleLogger.error("CannotPerformOperationException Error Authenticating User",
                                                   e);
                }
                catch(PasswordStorage.InvalidHashException e)
                {
                    LogManager.consoleLogger.error("InvalidHashException Error Authenticating User", e);
                }
            }
        }
        
        return null;
    }
    
    public static synchronized String getToken(User userToMint,
                                        String ip)
    {
        if(userToMint.getTokenRandomToSend() != null)
        {
            return userToMint.getTokenRandomToSend();
        }
        else
        {
            String token = null;
            LocalDateTime now = LocalDateTime.now();
            
            String hundredCharRandom = User.generateRandom();
            String tokenTemp = hundredCharRandom.substring(0, 50);
            String tokenRandom = hundredCharRandom.substring(50);
            
            String password = ip + tokenTemp + now.toString() + tokenRandom;
            try
            {
                String hash = ServerUtil.safeHashPassword(password);
                
                User updatedUserInfo = userToMint.clone();
                updatedUserInfo.setTokenRandom(tokenRandom);
                updatedUserInfo.setTokenDate(now);
                updatedUserInfo.setToken(hash);
                updatedUserInfo.setTokenIsOneUse(false);
                updatedUserInfo.setTokenRandomToSend(tokenTemp);
                if(modifyUser(userToMint, updatedUserInfo))
                {
                    token = tokenTemp;
                }
                else
                {
                    LogManager.consoleLogger.warn("Error setting User Info");
                }
                
            }
            catch(Exception e)
            {
                LogManager.consoleLogger.error("Error getting Token", e);
                
            }
            
            return token;
        }
    }
    
    public static synchronized boolean createUser(String username,
                                           String password,
                                           String email)
    {
        if(username.length() > 17 || password.length() < 9)
        {
            return false;
        }
        String whiteListedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
        
        ArrayList<User> users = getUserList();
        
        
        for(User user : users)
        {
            if(username.equals(user.getUsername()))
            {
                return false;
            }
            
            if(email.equals(user.getEmail()))
            {
                return false;
            }
        }
        
        
        for(char character : username.toCharArray())
        {
            if(!whiteListedCharacters.contains("" + character))
            {
                return false;
            }
        }
        
        
        String hashedPassword = ServerUtil.safeHashPassword(password);
        
        if(hashedPassword.equals(""))
        {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        User newUser = new User(username, hashedPassword, email, false, now);
        addUser(newUser);
        return true;
    }
    
    
}
