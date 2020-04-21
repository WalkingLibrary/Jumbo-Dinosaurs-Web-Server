package com.jumbodinosaurs.auth.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.auth.server.AuthToken;
import com.jumbodinosaurs.auth.server.User;
import com.jumbodinosaurs.auth.server.captcha.CaptchaResponse;
import com.jumbodinosaurs.devlib.database.DataBase;
import com.jumbodinosaurs.devlib.database.DataBaseManager;
import com.jumbodinosaurs.devlib.database.DataBaseUtil;
import com.jumbodinosaurs.devlib.database.Query;
import com.jumbodinosaurs.devlib.database.exceptions.NoSuchDataBaseException;
import com.jumbodinosaurs.devlib.database.exceptions.WrongStorageFormatException;
import com.jumbodinosaurs.devlib.util.WebUtil;
import com.jumbodinosaurs.devlib.util.objects.HttpResponse;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.util.OptionUtil;
import com.jumbodinosaurs.util.PasswordStorage;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthUtil
{
    public static final boolean testMode = true;
    public static final String userTableName = testMode ? "testUsers" : "users";
    public static final String emailUseName = "email";
    public static final String authUseName = "auth";
    
    private static final String generalWhiteListedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
    
    public static boolean isValidUsername(String username)
    {
        if(username.length() > 15)
        {
            return false;
        }
        
        char[] usernameArray = username.toCharArray();
        for(int i = 0; i < usernameArray.length; i++)
        {
            if(!generalWhiteListedCharacters.contains("" + usernameArray[i]))
            {
                return false;
            }
        }
        return true;
    }
    
    public static CaptchaResponse getCaptchaResponse(String captchaToken) throws MalformedURLException, IOException
    {
        if(testMode)
        {
            return new CaptchaResponse(true, "TESTMODE", .9, "TESTMODE");
        }
    
        String captchaSecret = OptionUtil.getCaptchaKey().getSecretKey();
        if(captchaSecret == null)
        {
            return new CaptchaResponse(false, "", .0, "null");
        }
        String urlString = "https://www.google.com/recaptcha/api/siteverify?secret=";
        urlString += captchaSecret;
        urlString += "&response=";
        urlString += captchaToken;
        
        URL url = new URL(urlString);
        HttpURLConnection connection;
        connection = (HttpsURLConnection) url.openConnection();
        HttpResponse response = WebUtil.getResponse(connection);
        
        return new Gson().fromJson(response.getResponse(), CaptchaResponse.class);
    }
    
    public static String generateRandomString(int size)
    {
        String random = "";
        while(random.length() <= size)
        {
            int randomNumber = (int) (Math.random() * generalWhiteListedCharacters.length());
            random += generalWhiteListedCharacters.toCharArray()[randomNumber];
        }
        return random;
    }
    
    public static DataBase getUserDataBase() throws NoSuchDataBaseException
    {
        if(testMode)
        {
            return DataBaseManager.getDataBase(OptionUtil.getUserDataBaseName());
        }
        else
        {
            return DataBaseManager.getDataBase(OptionUtil.getUserDataBaseName() + "TEST");
        }
    }
    
    public static User getUser(DataBase dataBase, String username) throws SQLException, WrongStorageFormatException
    {
        String statement = "SELECT * FROM " + userTableName;
        statement += " WHERE JSON_EXTRACT(" + DataBaseUtil.objectColumnName + ", \"$.username\") = \"" + username +
                             "\";";
        Query query = new Query(statement);
        ArrayList<User> resultUsers = DataBaseUtil.getObjectsDataBase(query, dataBase, new TypeToken<User>() {});
    
        int count = 0;
        for(User user : resultUsers)
        {
            if(user.getUsername().equals(username))
            {
                count++;
            }
            
            if(count >= 2)
            {
                throw new IllegalStateException("More Than one User named " + username);
            }
        }
        
        for(User user : resultUsers)
        {
            if(user.getUsername().equals(username))
            {
                return user;
            }
        }
        return null;
    }
    
    public static AuthSession authenticateUser(PostRequest request)
    {
        /* Process for Authenticating a user
         * Get the user Database
         * Check/Verify Post Request Attributes
         * Get User From DataBase
         * Check given credentials with stored credentials
         *
         *
         */
    
    
        AuthSession authSession = new AuthSession();
        authSession.setSuccess(false);
    
        //Get the user Database
        DataBase dataBase;
        try
        {
            dataBase = AuthUtil.getUserDataBase();
        }
        catch(NoSuchDataBaseException e)
        {
            authSession.setFailureCode(FailureReasons.NO_DATABASE);
            return authSession;
        }
    
        if(dataBase == null)
        {
            authSession.setFailureCode(FailureReasons.NO_DATABASE);
            return authSession;
        }
    
    
        //Check/Verify Post Request Attributes
        if(request.getUsername() == null)
        {
            authSession.setFailureCode(FailureReasons.MISSING_ATTRIBUTES);
            return authSession;
        }
    
    
        boolean passwordAuth = true;
        String use = null;
    
        if(request.getPassword() == null && (request.getToken() == null || request.getContent() == null))
        {
            authSession.setFailureCode(FailureReasons.MISSING_ATTRIBUTES);
            return authSession;
        }
    
        if(request.getPassword() == null)
        {
            passwordAuth = false;
            use = request.getContent();
        }
    
    
        try
        {
            //Get User From DataBase
            User currentUser = AuthUtil.getUser(dataBase, request.getUsername());
            if(currentUser == null)
            {
                authSession.setFailureCode(FailureReasons.MISSING_USER);
                return authSession;
            }
    
            if(!currentUser.isActive())
            {
                authSession.setFailureCode(FailureReasons.ACCOUNT_NOT_ACTIVATED);
                return authSession;
            }
    
            //Check given credentials with stored credentials
            boolean correctPassword, correctToken;
            correctPassword = false;
            correctToken = false;
    
            if(passwordAuth)
            {
                correctPassword = AuthUtil.authenticateUser(currentUser, request.getPassword());
                if(!correctPassword)
                {
                    authSession.setFailureCode(FailureReasons.INCORRECT_PASSWORD);
                }
                else
                {
                    authSession.setPasswordAuth(true);
                }
                
            }
            else
            {
                correctToken = AuthUtil.authenticateUser(currentUser, request.getToken(), use);
                if(!correctToken)
                {
                    authSession.setFailureCode(FailureReasons.INCORRECT_TOKEN);
                }
                
            }
            
            if(correctPassword || correctToken)
            {
                authSession.setUser(currentUser);
                authSession.setSuccess(true);
                return authSession;
            }
            
            authSession.setSuccess(false);
            return authSession;
            
        }
        catch(SQLException | PasswordStorage.InvalidHashException | PasswordStorage.CannotPerformOperationException | WrongStorageFormatException e)
        {
            authSession.setFailureCode(FailureReasons.SERVER_ERROR);
            return authSession;
        }
    }
    
    private static boolean authenticateUser(User user,
                                            String token,
                                            String use) throws PasswordStorage.CannotPerformOperationException, PasswordStorage.InvalidHashException
    {
        AuthToken authToken = user.getToken(use);
        if(authToken == null)
        {
            return false;
        }
        return authToken.isValidToken(token);
    }
    
    private static boolean authenticateUser(User user,
                                            String password) throws PasswordStorage.CannotPerformOperationException, PasswordStorage.InvalidHashException
    {
        return PasswordStorage.verifyPassword(password, user.getHashedPassword());
    }
    
    public static boolean updateUser(AuthSession authSession, User newUserInfo)
    {
        if(!authSession.isSuccess())
        {
            return false;
        }
        
        try
        {
            Query updateQuery = DataBaseUtil.getUpdateObjectQuery(userTableName, authSession.getUser(), newUserInfo);
            DataBase userDataBase = getUserDataBase();
            DataBaseUtil.queryDataBase(updateQuery, userDataBase);
            return true;
        }
        catch(NoSuchDataBaseException | SQLException e)
        {
            return false;
        }
    }
    
}
