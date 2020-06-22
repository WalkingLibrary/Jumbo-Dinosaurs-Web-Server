package com.jumbodinosaurs.auth.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.auth.exceptions.NoSuchUserException;
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
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthUtil
{
    public static final boolean testMode = true;
    public static final String userTableName = testMode ? "testUsers" : "users";
    public static final String emailActivationUseName = "email";
    public static final String authUseName = "auth";
    public static final String changePasswordUseName = "changePassword";
    
    public static final String generalWhiteListedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
    
    public static boolean isValidUsername(String username)
    {
        if(username.length() > 15)
        {
            return false;
        }
    
        if(username.length() == 0)
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
    
    //https://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address
    public static boolean isValidEmail(String emailAddress)
    {
        return emailAddress.length() <= 254;
    }
    
    public static boolean isValidPassword(String password)
    {
        return password.length() <= 255;
    }
    
    public static CaptchaResponse getCaptchaResponse(String captchaToken)
            throws IOException
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
    
    public static String generateCharacters(int amount)
    {
        String characters = "";
        for(int i = 0; i < amount; i++)
        {
            characters += (char) i;
        }
        return characters;
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
    
    public static void queryUserDataBase(Query query, boolean manipulate)
            throws NoSuchDataBaseException, SQLException
    {
        if(manipulate)
        {
            DataBaseUtil.manipulateDataBase(query, getUserDataBase());
        }
        else
        {
            DataBaseUtil.queryDataBase(query, getUserDataBase());
        }
    }
    
    private static DataBase getUserDataBase()
            throws NoSuchDataBaseException
    {
        return DataBaseManager.getDataBase(OptionUtil.getServersDataBaseName());
    }
    
    private static User getUser(DataBase dataBase, String username)
            throws SQLException, WrongStorageFormatException, NoSuchUserException
    {
        if(!isValidUsername(username))
        {
            throw new NoSuchUserException(username + " is not a Valid Username");
        }
    
        String statement = "SELECT * FROM " + userTableName;
        statement += " WHERE JSON_EXTRACT(" + DataBaseUtil.objectColumnName + ", \"$.username\") =?;";
        Query query = new Query(statement);
        ArrayList<String> parameters = new ArrayList<String>();
        parameters.add(username);
        query.setParameters(parameters);
    
        ArrayList<User> resultUsers = DataBaseUtil.getObjectsDataBase(query, dataBase, new TypeToken<User>() {});
    
        if(resultUsers.size() > 1)
        {
            throw new IllegalStateException("More Than one User named " + username);
        }
    
        if(resultUsers.size() == 0)
        {
            throw new NoSuchUserException("No user named " + username + " found in " + dataBase.getDataBaseName());
        }
    
        return resultUsers.get(0);
    
    }
    
    public static boolean isUserNameTaken(String username)
    {
        if(!isValidUsername(username))
        {
            return true;
        }
    
        try
        {
            getUser(getUserDataBase(), username);
            return true;
        }
        catch(WrongStorageFormatException | NoSuchDataBaseException | SQLException e)
        {
            return true;
        }
        catch(NoSuchUserException e)
        {
            return false;
        }
    }
    
    public static AuthSession authenticateUser(PostRequest request)
    {
        /* Process for Authenticating a user
         *
         *
         * Set AuthSessions User
         *
         * Check/Verify Post Request Attributes (Password, Token, and Token Use)
         * Determine if it's a Token or Password Auth
         *
         * Check if account has been activated
         * Check given credentials with stored credentials
         *
         *
         * NOTE:
         * To avoid over querying for users we will ensure that a user is set to the AuthSession from the username.
         * This allows Following Post Commands to use the user. Invalid authentications should be caught in the
         * response generator
         *
         */
        
        AuthSession authSession;
        authSession = new AuthSession(null);
        authSession.setSuccess(false);
        
        
        
        
        /* Setting the AuthSessions User
         *
         * Check/Verify Post Request Attributes (Username)
         * Get the user Database
         * Sanitize UserName
         * Get User From DataBase
         *
         * */
        
        //Check/Verify Post Request Attributes (Username)
        if(request.getUsername() == null)
        {
            authSession.setFailureCode(FailureReasons.MISSING_USERNAME);
            return authSession;
        }
        
        
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
        
        
        //Sanitize UserName
        if(!isValidUsername(request.getUsername()))
        {
            authSession.setFailureCode(FailureReasons.INVALID_USERNAME);
            return authSession;
        }
        
        //Get User From DataBase
        User currentUser;
        try
        {
            currentUser = AuthUtil.getUser(dataBase, request.getUsername());
            
        }
        catch(NoSuchUserException e)
        {
            authSession.setFailureCode(FailureReasons.MISSING_USER);
            return authSession;
        }
        catch(WrongStorageFormatException | SQLException e)
        {
            authSession.setFailureCode(FailureReasons.SERVER_ERROR);
            return authSession;
        }
        
        authSession = new AuthSession(currentUser);
        authSession.setSuccess(false);
        
        
        //Check/Verify Post Request Attributes (Password, Token, and Token Use)
        boolean passwordAuth = true;
        String use = null;
        
        //If the user name is null or the (password && token or token use) is null
        if((request.getPassword() == null && (request.getToken() == null || request.getTokenUse() == null)))
        {
            authSession.setFailureCode(FailureReasons.MISSING_ATTRIBUTES);
            return authSession;
        }
        
        //Determine if it's a Token or Password Auth
        if(request.getPassword() == null)
        {
            passwordAuth = false;
            use = request.getTokenUse();
        }
        
        
        try
        {
            //Check if account has been activated
            if(!currentUser.isActive())
            {
                authSession.setFailureCode(FailureReasons.ACCOUNT_NOT_ACTIVATED);
                return authSession;
            }
            
            
            //Check given credentials with stored credentials
            boolean correctPassword, correctToken;
            if(passwordAuth)
            {
                correctPassword = AuthUtil.authenticateUser(currentUser, request.getPassword());
                if(!correctPassword)
                {
                    authSession.setFailureCode(FailureReasons.INCORRECT_PASSWORD);
                    return authSession;
                }
                
                authSession.setPasswordAuth(true);
            }
            else
            {
                correctToken = AuthUtil.authenticateUser(currentUser, request.getToken(), use);
                if(!correctToken)
                {
                    authSession.setFailureCode(FailureReasons.INCORRECT_TOKEN);
                    return authSession;
                }
                
                authSession.setTokenUsed(currentUser.getToken(use));
            }
            
            authSession.setSuccess(true);
            return authSession;
            
            
        }
        catch(PasswordStorage.InvalidHashException | PasswordStorage.CannotPerformOperationException e)
        {
            e.printStackTrace();
            authSession.setFailureCode(FailureReasons.SERVER_ERROR);
            return authSession;
        }
    }
    
    
    private static boolean authenticateUser(User user, String token, String use)
            throws PasswordStorage.CannotPerformOperationException, PasswordStorage.InvalidHashException
    {
        AuthToken authToken = user.getToken(use);
        if(authToken == null)
        {
            return false;
        }
        return authToken.isValidToken(token);
    }
    
    private static boolean authenticateUser(User user, String password)
            throws PasswordStorage.CannotPerformOperationException, PasswordStorage.InvalidHashException
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
            DataBaseUtil.manipulateDataBase(updateQuery, userDataBase);
            return true;
        }
        catch(NoSuchDataBaseException | SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean addUser(User user)
    {
        Query insertQuery = DataBaseUtil.getInsertQuery(AuthUtil.userTableName, user);
        try
        {
            DataBaseUtil.manipulateDataBase(insertQuery, getUserDataBase());
        }
        catch(SQLException | NoSuchDataBaseException e)
        {
            e.printStackTrace();
            return false;
        }
        
        return insertQuery.getResponseCode() == 1;
    }
    
    public static ArrayList<User> getAllUsers()
            throws NoSuchDataBaseException, SQLException, WrongStorageFormatException
    {
        return DataBaseUtil.getObjectsDataBase(new Query("SELECT * FROM testUsers"),
                                               getUserDataBase(),
                                               new TypeToken<User>() {});
    }
    
    
    public static boolean updateUserNoAuthCheck(AuthSession authSession, User newUserInfo)
    {
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
