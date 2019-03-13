package com.jumbodinosaurs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CredentialsManager
{
    public CredentialsManager()
    {

    }


    public synchronized String getTokenOneUse(User userToMint, String ip)
    {
        String token = null;
        LocalDateTime now = LocalDateTime.now();
        String hundredCharRandom = User.generateRandom();
        String tokenTemp = hundredCharRandom.substring(0, 50);
        String tokenRandom = hundredCharRandom.substring(50);
        String password = ip  + tokenTemp + now.toString() + tokenRandom;
        try
        {
            String hash = DataController.safeHashPassword(password);
            User updatedUserInfo = userToMint.clone();
            updatedUserInfo.setTokenRandom(tokenRandom);
            updatedUserInfo.setTokenDate(now);
            updatedUserInfo.setToken(hash);
            updatedUserInfo.setTokenIsOneUse(true);
            if(modifyUser(userToMint, updatedUserInfo))
            {
                token = tokenTemp;
            }
            else
            {
                OperatorConsole.printMessageFiltered("Error setting User Info", false, true);
            }

        }
        catch (Exception e)
        {
            OperatorConsole.printMessageFiltered("Error getting Token", false, true);
            e.printStackTrace();
        }

        return token;
    }


    public synchronized String getToken(User userToMint, String ip)
    {
        String token = null;
        LocalDateTime now = LocalDateTime.now();

        String hundredCharRandom = User.generateRandom();
        String tokenTemp = hundredCharRandom.substring(0, 50);
        String tokenRandom = hundredCharRandom.substring(50);

        String password = ip  + tokenTemp + now.toString() + tokenRandom;
        try
        {
            String hash = DataController.safeHashPassword(password);

            User updatedUserInfo = userToMint.clone();
            updatedUserInfo.setTokenRandom(tokenRandom);
            updatedUserInfo.setTokenDate(now);
            updatedUserInfo.setToken(hash);
            updatedUserInfo.setTokenIsOneUse(false);

            if(modifyUser(userToMint, updatedUserInfo))
            {
                token = tokenTemp;
            }
            else
            {
                OperatorConsole.printMessageFiltered("Error setting User Info", false, true);
            }

        }
        catch (Exception e)
        {
            OperatorConsole.printMessageFiltered("Error getting Token", false, true);
            e.printStackTrace();
        }

        return token;
    }

    /*
     Checks current user list for old user data. if the user is in the list it replace that user with the newUser
     if there is no list it makes one by adding new user

     returns true if new user is written to the user file
     */
    public synchronized boolean modifyUser(User oldUser, User newUser)
    {
        ArrayList<User> users = getUserList();
        if (users == null)
        {
            users = new ArrayList<User>();
            users.add(newUser);
            setUserList(users);
            return true;
        }
        else
        {
            for (int i = 0; i < users.size(); i++)
            {
                if (users.get(i).equals(oldUser))
                {
                    users.remove(i);
                    users.add(newUser);
                    setUserList(users);
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized boolean usernameAvailable(String username)
    {
        ArrayList<User> users = getUserList();
        if (users != null)
        {
            for (User user : users)
            {
                if (user.getUsername().equals(username))
                {
                    return false;
                }
            }
        }
        return true;

    }

    public synchronized boolean emailInUse(String email)
    {

        ArrayList<User> users = getUserList();
        if (users != null)
        {
            for (User user : users)
            {
                if (user.getEmail().equals(email))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized User getUserByEmail(String email)
    {

        ArrayList<User> users = getUserList();
        if (users != null)
        {
            for (User user : users)
            {
                if (user.getEmail().equals(email))
                {
                    return user;
                }
            }
        }
        return null;
    }


    public synchronized User loginToken(String token, String ip)
    {
        for (User user : this.getUserList())
        {
            String password = ip + token + user.getTokenDate() + user.getTokenRandom();
            try
            {
                if (PasswordStorage.verifyPassword(password, user.getToken()))
                {
                    LocalDateTime tokenMintDate = user.getTokenDate();
                    LocalDateTime now = LocalDateTime.now();
                    if (now.minusDays(30).isAfter(tokenMintDate))
                    {
                        return null;
                    }

                    if(user.isTokenIsOneUse())
                    {
                        LocalDateTime lastLogin = user.getLastLoginDate();
                        if(lastLogin.isAfter(tokenMintDate))
                        {
                            return null;
                        }
                        else if(now.minusHours((long)1).isAfter(tokenMintDate))
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
            catch (Exception e)
            {
                OperatorConsole.printMessageFiltered("Error Authenticating User Token", false, true);
                e.printStackTrace();
            }
        }
        return null;
    }

    public synchronized User loginUsernamePassword(String username, String password)
    {
        ArrayList<User> users = getUserList();
        if (users != null)
        {
            for (User user : users)
            {
                if (user.getUsername().equals(username))
                {
                    try
                    {
                        if (PasswordStorage.verifyPassword(password, user.getPassword().toString()))
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
                    catch (Exception e)
                    {
                        OperatorConsole.printMessageFiltered("Error Authenticating User", false, true);
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public synchronized void addUser(User userToAdd)
    {
        ArrayList<User> users = getUserList();
        if (users == null)
        {
            users = new ArrayList<User>();
        }
        users.add(userToAdd);

        setUserList(users);
    }

    public synchronized ArrayList<User> getUserList()
    {
        ArrayList<User> users = new ArrayList<User>();
        try
        {
            File usersInfo = DataController.checkFor(DataController.userInfoDirectory, "userinfo.json");
            String fileContents = DataController.getFileContents(usersInfo);
            Type typeToken = new TypeToken<ArrayList<User>>()
            {
            }.getType();
            users = new Gson().fromJson(fileContents, typeToken);
            if (users != null)
            {
                for (User user : users)
                {
                    user.setPassword(DataController.removeUTFCharacters(user.getPassword()).toString());
                }
            }
        }
        catch (Exception e)
        {
            OperatorConsole.printMessageFiltered("Error Reading User Info", false, true);
            e.printStackTrace();
        }
        return users;
    }

    public synchronized void setUserList(ArrayList<User> users)
    {
        try
        {
            String listToWrite = new Gson().toJson(users);
            File usersInfo = DataController.checkFor(DataController.userInfoDirectory, "userinfo.json");
            DataController.writeContents(usersInfo, listToWrite, false);
        }
        catch (Exception e)
        {
            OperatorConsole.printMessageFiltered("Error writing to User List", false, true);
            e.printStackTrace();
        }
    }


    public synchronized boolean createUser(String username, String password, String email)
    {
        if (username.length() > 17)
        {
            return false;
        }
        String whiteListedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";

        ArrayList<User> users = getUserList();

        if (users != null)
        {
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
        }

        for (char character : username.toCharArray())
        {
            if (!whiteListedCharacters.contains("" + character))
            {
                return false;
            }
        }


        String hashedPassword = DataController.safeHashPassword(password);

        if (hashedPassword.equals(""))
        {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        User newUser = new User(username, hashedPassword, email, false, now);
        addUser(newUser);
        return true;
    }


}
