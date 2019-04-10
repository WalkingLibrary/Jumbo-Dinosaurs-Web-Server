package com.jumbodinosaurs.netty;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.ServerControl;
import com.jumbodinosaurs.objects.*;
import com.jumbodinosaurs.util.CredentialsManager;
import com.jumbodinosaurs.util.DataController;
import com.jumbodinosaurs.util.OperatorConsole;
import com.jumbodinosaurs.util.PasswordStorage;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;

public class HTTPSRequest
{
    //Status Codes
    private final String sC100 = "HTTP/1.1 100 Continue";
    private final String sC200 = "HTTP/1.1 200 OK";
    private final String sC301 = "HTTP/1.1 301  Permanently";
    private final String sC303 = "HTTP/1.1 303 Temporary";
    private final String sC304 = "HTTP/1.1 304 Not Modified";
    private final String sC400 = "HTTP/1.1 400 Bad";
    private final String sC401 = "HTTP/1.1 401 Unauthorized";
    private final String sC403 = "HTTP/1.1 403 Forbidden";
    private final String sC404 = "HTTP/1.1 404 Not Found";
    private final String sC501 = "HTTP/1.1 501 Not Implemented";
    //headers
    private final String keepAlive = "\r\nConnection: keep-alive\r\n\r\n";
    private final String closeHeader = " \r\nConnection: Close\r\n\r\n";
    private final String acceptedLanguageHeader = "\r\nAccept-Language: en-US";
    private final String originHeader = "\r\nOrigin: http://www.jumbodinosaurs.com/";
    private final String locationHeader = "\r\nLocation:";
    private final String contentTextHeader = "\r\nContent-Type: text/";
    private final String contentImageHeader = "\r\nContent-Type: image/";
    private final String contentApplicationHeader = "\r\nContent-Type: application/";
    private final String contentLengthHeader = "\r\nContent-Length: "; //[length in bytes of the image]\r\n
    
    private String messageFromClient;
    private String messageToSend;
    private String ip;
    private byte[] byteArrayToSend;
    private Boolean leaveMessageTheSame = true;
    private PostRequest postRequest;
    
    
    public HTTPSRequest(String messageFromClient, String ip)
    {
        this.messageFromClient = messageFromClient;
        this.ip = ip;
        this.messageToSend = "";
    }
    
    
    public boolean isGet()
    {
        return this.messageFromClient.substring(0, 4).contains("GET");
    }
    
    public boolean isPost()
    {
        return this.messageFromClient.substring(0, 5).contains("POST");
    }
    
    public boolean isHTTP()
    {
        return this.messageFromClient.indexOf(" HTTP/1.1") > -1;
    }
    
    public void generateMessage()
    {
        //If Get Request
        if(this.isGet())
        {
            //Clean Name from get Request for dataIO
            String requestCheck = this.getGetRequest();
            if(requestCheck != null)
            {
                String fileToGet = this.mendPageRequest(requestCheck);
                
                OperatorConsole.printMessageFiltered("File To Get: " + fileToGet, true, false);
                File fileRequested = DataController.safeSearchDir(DataController.getDirectory, fileToGet, true);
                
                //If if have file
                if(fileRequested != null)
                {
                    //add Good Code
                    
                    
                    String fileType = DataController.getType(fileRequested);
                    if(fileType.contains("png") || fileType.contains("jpeg") || fileType.contains("jpg") || fileType.contains("ico"))
                    {
                        if(!DataController.readPhoto(fileRequested).equals(""))
                        {
                            this.messageToSend += this.sC200;
                            this.messageToSend += this.contentImageHeader + DataController.getType(fileRequested);
                            //this.messageToSend += this.contentLengthHeader + dataIO.getPictureLength(fileRequested.getName());
                            this.messageToSend += this.closeHeader;
                            this.byteArrayToSend = DataController.readPhoto(fileRequested);
                            
                        }
                        else
                        {
                            this.setMessage404();
                        }
                        
                        
                    }
                    else if(fileType.contains("zip"))
                    {
                        this.messageToSend += this.sC200;
                        this.messageToSend += this.contentApplicationHeader + fileType;
                        this.messageToSend += this.closeHeader;
                        this.byteArrayToSend = DataController.readZip(fileRequested);
                    }
                    else
                    {
                        this.messageToSend += this.sC200;
                        this.messageToSend += this.contentTextHeader + DataController.getType(fileRequested);
                        this.messageToSend += this.closeHeader;
                        this.messageToSend += DataController.getFileContents(fileRequested);
                        
                    }
                }
                else//Send 404 not found server doesn't have the file
                {
                    //Make sure the client didn't send any thing that can be a post request
                    //If the get request can become a Post request  then change the client message sent to post for censorship
                    // in the session handler
                    String getGetReplacedWithPost = getGetReplacedWithPost();
                    if(!getGetReplacedWithPost.equals(""))
                    {
                        try
                        {
                            String postInfo = getPostRequestUTF8(getGetReplacedWithPost);
                            PostRequest postRequest = new Gson().fromJson(postInfo, PostRequest.class);
                            this.leaveMessageTheSame = false;
                            this.messageFromClient = getGetReplacedWithPost + " was GET ";
                            this.setMessage400();
                        }
                        catch(JsonParseException e)
                        {
                            OperatorConsole.printMessageFiltered("GET was Not Json", true, false);
                        }
                        
                        
                    }
                    
                    if(this.messageToSend.equals(""))
                    {
                        this.setMessage404();
                    }
                    
                }
            }
            else
            {
                this.setMessage400();
            }
        }
        else if(this.isPost())
        {
            //To avoid saving password in the logs.json
            
            this.leaveMessageTheSame = false;
           /*
           //See Post Diagram for more Post Insight
           */
            if(OperatorConsole.allowPost())
            {
                String postJson = this.getPostRequestUTF8();
                //System.out.println("Post Json: " + postJson);
                if(postJson != null)
                {
                    try
                    {
                        this.postRequest = new Gson().fromJson(postJson, PostRequest.class);
                        if(this.postRequest != null)
                        {
                            if(this.postRequest.getCommand() != null)
                            {
                                String command = this.postRequest.getCommand();
                                WritablePost post = null;
                                boolean send400Code = true;
                                double captchaScore = 0;
                                
                                if(this.postRequest.getCaptchaCode() != null)
                                {
                                    captchaScore = this.getCaptchaScore(this.postRequest.getCaptchaCode());
                                }
                                else if(ServerControl.getArguments() != null && ServerControl.getArguments().isInTestMode())
                                {
                                    captchaScore = .9;
                                }
                                
                                if(command != null)
                                {
                                    if(command.equals("query"))
                                    {
                                        QueryRequest query = postRequest.getQueryRequest();
                                        ArrayList<String> responseList = getQueryList(query);
                                        
                                        send400Code = false;
                                        this.messageToSend += sC200;
                                        this.messageToSend += closeHeader;
                                        if(responseList.size() > 0)
                                        {
                                            this.messageToSend += new Gson().toJson(responseList);
                                        }
                                        else
                                        {
                                            this.messageToSend += "null";
                                        }
                                        
                                    }
                                    else if(command.equals("confirmMailServer"))
                                    {
                                        if(postRequest.getEmail() != null)
                                        {
                                            if(lookUpEmail(this.postRequest.getEmail()))
                                            {
                                                send400Code = false;
                                                this.messageToSend += sC200;
                                                this.messageToSend += closeHeader;
                                            }
                                        }
                                    }
                                    else if(command.equals("usernameCheck"))
                                    {
                                        if(this.postRequest.getUsername() != null)
                                        {
                                            if(DataController.getCredentialsManager().usernameAvailable(this.postRequest.getUsername()))
                                            {
                                                send400Code = false;
                                                this.messageToSend += sC200;
                                                this.messageToSend += closeHeader;
                                            }
                                        }
                                    }
                                    else if(command.equals("emailCheck"))
                                    {
                                        if(this.postRequest.getEmail() != null && ((!CredentialsManager.isIPEmailCheckLocked(this.ip) || CredentialsManager.isAbuserUnlocked(this.ip))))
                                        {
                                            if(!DataController.getCredentialsManager().emailInUse(postRequest.getEmail()))
                                            {
                                                send400Code = false;
                                                this.messageToSend += sC200;
                                                this.messageToSend += closeHeader;
                                            }
                                            
                                            CredentialsManager.emailStrikeIP(this.ip);
                                        }
                                    }
                                    else if(command.equals("createAccount"))
                                    {
                                        if(this.postRequest.getUsername() != null && this.postRequest.getPassword() != null && this.postRequest.getEmail() != null)
                                        {
                                            if(captchaScore > .5)
                                            {
                                                if(DataController.getCredentialsManager().createUser(this.postRequest.getUsername(), this.postRequest.getPassword(), this.postRequest.getEmail()))
                                                {
                                                    send400Code = false;
                                                    this.messageToSend += sC200;
                                                    this.messageToSend += closeHeader;
                                                }
                                            }
                                        }
                                    }
                                    else if(command.equals("resetPassword"))
                                    {
                                        if(this.postRequest.getEmail() != null && DataController.getCredentialsManager().emailInUse(this.postRequest.getEmail()))
                                        {
                                            User userToSendCodeTo = DataController.getCredentialsManager().getUserByEmail(this.postRequest.getEmail());
                                            
                                            
                                            if((captchaScore >= .8) || ((captchaScore > .5) && userToSendCodeTo.isEmailVerified()))
                                            {
                                                
                                                
                                                LocalDateTime now = LocalDateTime.now();
                                                LocalDateTime tokenMintDate = userToSendCodeTo.getTokenDate();
                                                boolean sendEmail = false;
                                                
                                                if(tokenMintDate == null || !userToSendCodeTo.isTokenIsOneUse())
                                                {
                                                    sendEmail = true;
                                                }
                                                else if(userToSendCodeTo.getLastLoginDate().isAfter(tokenMintDate))
                                                {
                                                    sendEmail = true;
                                                }
                                                else if(now.minusMinutes((long) 2).isAfter(tokenMintDate))
                                                {
                                                    sendEmail = true;
                                                }
                                                
                                                if(sendEmail)
                                                {
                                                    String token = DataController.getCredentialsManager().getTokenOneUse(userToSendCodeTo, this.ip);
                                                    String messageToSend = "There has been a password change request to the account linked to this email" + " at https://jumbodinosaurs.com/. If this was not you contact us at jumbodinosaurs@gmail.com.\n\n" + "If this was you visit https://www.jumbodinosaurs.com/changepassword.html and enter this code\n To change your password." + "\n\n" + token;
                                                    DataController.sendEmail(userToSendCodeTo.getEmail(), "Password Change", messageToSend);
                                                }
                                            }
                                            
                                            
                                        }
                                        send400Code = false;
                                        this.messageToSend += sC200;
                                        this.messageToSend += closeHeader;
                                    }
                                    else
                                    {
                                        User user = null;
                                        boolean tokenLogin = true;
                                        if(!CredentialsManager.isIPCaptchaLocked(this.ip) || CredentialsManager.isAbuserUnlocked(this.ip))
                                        {
                                            if(this.postRequest.getUsername() != null && this.postRequest.getPassword() != null)
                                            {
                                                user = DataController.getCredentialsManager().loginUsernamePassword(this.postRequest.getUsername(), this.postRequest.getPassword());
                                                tokenLogin = false;
                                            }
                                            else if(this.postRequest.getToken() != null)
                                            {
                                                user = DataController.getCredentialsManager().loginToken(this.postRequest.getToken(), this.ip);
                                            }
                                            
                                            if(user == null)
                                            {
                                                CredentialsManager.loginStrikeIP(this.ip);
                                            }
                                        }
                                        else if(captchaScore > .5)
                                        {
                                            if(this.postRequest.getUsername() != null && this.postRequest.getPassword() != null)
                                            {
                                                user = DataController.getCredentialsManager().loginUsernamePassword(this.postRequest.getUsername(), this.postRequest.getPassword());
                                                tokenLogin = false;
                                            }
                                            else if(this.postRequest.getToken() != null)
                                            {
                                                user = DataController.getCredentialsManager().loginToken(this.postRequest.getToken(), this.ip);
                                            }
                                        }
                                        
                                        
                                        if(user != null)
                                        {
                                            if(tokenLogin)
                                            {
                                                this.postRequest.setUsername(user.getUsername());
                                            }
                                            String content = this.postRequest.getContent();
                                            LocalDateTime now = LocalDateTime.now();
                                            if(tokenLogin && user.isTokenIsOneUse())
                                            {
                                                if(command.equals("passwordChange"))
                                                {
                                                    if(postRequest.getPassword() != null && this.postRequest.getPassword().length() >= 9)
                                                    {
                                                        
                                                        String password = this.postRequest.getPassword();
                                                        User updatedUserInfo = user.clone();
                                                        updatedUserInfo.setPassword(DataController.safeHashPassword(password));
                                                        
                                                        if(CredentialsManager.modifyUser(user, updatedUserInfo))
                                                        {
                                                            String messageToSend = "The password for your account at https://jumbodinosaurs/ has been changed." + "\nIf this was not you contact us at jumbodinosaurs@gmail.com.";
                                                            DataController.sendEmail(updatedUserInfo.getEmail(), "Password Changed", messageToSend);
                                                            send400Code = false;
                                                            this.messageToSend += sC200;
                                                            this.messageToSend += closeHeader;
                                                            this.messageToSend += "passwordChanged";
                                                            
                                                        }
                                                        else
                                                        {
                                                            OperatorConsole.printMessageFiltered("Error Setting User Info", false, true);
                                                        }
                                                        
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                switch(command)
                                                {
                                                    case "postBookList":
                                                        try
                                                        {
                                                            Type tokenType = new TypeToken<ArrayList<MinecraftWrittenBook>>()
                                                            {
                                                            }.getType();
                                                            ArrayList<MinecraftWrittenBook> books = new Gson().fromJson(content, tokenType);
                                                            if(books != null && postRequest.getListName() != null)
                                                            {
                                                                String localPath = DataController.fixPathSeparator("/booklist/books.json");
                                                                
                                                                content = new Gson().toJson(books);
                                                                
                                                                WritablePost temp = new WritablePost();
                                                                temp.setLocalPath(localPath);
                                                                temp.setUser(user.getUsername());
                                                                temp.setContent(content);
                                                                temp.setDate(now);
                                                                temp.setPostIdentifier(postRequest.getListName());
                                                                temp.setObjectType(MinecraftWrittenBook.class.getTypeName());
                                                                post = temp.clone();
                                                                
                                                                send400Code = false;
                                                                this.messageToSend += sC200;
                                                                this.messageToSend += closeHeader;
                                                            }
                                                        }
                                                        catch(JsonParseException e)
                                                        {
                                                            this.setMessage400();
                                                        }
                                                        
                                                        
                                                        break;
                                                    
                                                    
                                                    case "postSign":
                                                        try
                                                        {
                                                            MinecraftSign sign = new Gson().fromJson(content, MinecraftSign.class);
                                                            if(sign != null && postRequest.getConnectionName() != null)
                                                            {
                                                                String localPath = DataController.fixPathSeparator("/signlist/signs.json");
                                                                
                                                                
                                                                content = new Gson().toJson(sign);
                                                                WritablePost temp = new WritablePost();
                                                                temp.setLocalPath(localPath);
                                                                temp.setUser(user.getUsername());
                                                                temp.setContent(content);
                                                                temp.setDate(now);
                                                                temp.setPostIdentifier(postRequest.getConnectionName());
                                                                temp.setObjectType(MinecraftSign.class.getTypeName());
                                                                post = temp.clone();
                                                                
                                                                send400Code = false;
                                                                this.messageToSend += sC200;
                                                                this.messageToSend += closeHeader;
                                                            }
                                                        }
                                                        catch(JsonParseException e)
                                                        {
                                                            this.setMessage400();
                                                        }
                                                        break;
                                                    
                                                    case "postComment":
                                                        //WIP
                                                        break;
                                                    
                                                    case "getToken":
                                                        if(!tokenLogin)
                                                        {
                                                            this.messageToSend += sC200;
                                                            this.messageToSend += closeHeader;
                                                            this.messageToSend += DataController.getCredentialsManager().getToken(user, this.ip);
                                                            send400Code = false;
                                                        }
                                                        break;
                                                    case "emailConfirm":
                                                        LocalDateTime emailCodeSendDate = user.getEmailDateTime();
                                                        if(!now.minusDays((long) 1).isAfter(emailCodeSendDate))
                                                        {
                                                            try
                                                            {
                                                                String emailCode = this.ip + emailCodeSendDate.toString() + this.postRequest.getEmailCode();
                                                                if(PasswordStorage.verifyPassword(emailCode, user.getEmailCode()))
                                                                {
                                                                    User userConfirmedEmail = user.clone();
                                                                    userConfirmedEmail.setEmailVerified(true);
                                                                    if(CredentialsManager.modifyUser(user, userConfirmedEmail))
                                                                    {
                                                                        this.messageToSend += sC200;
                                                                        this.messageToSend += closeHeader;
                                                                        this.messageToSend += "emailConfirmed";
                                                                        send400Code = false;
                                                                    }
                                                                    else
                                                                    {
                                                                        OperatorConsole.printMessageFiltered("Error Confirming Email: Setting User Data", false, true);
                                                                    }
                                                                }
                                                            }
                                                            catch(PasswordStorage.CannotPerformOperationException e)
                                                            {
                                                                OperatorConsole.printMessageFiltered("CannotPerformOperationException Confirming Email", false, true);
                                                            }
                                                            catch(PasswordStorage.InvalidHashException e)
                                                            {
                                                                OperatorConsole.printMessageFiltered("InvalidHashException Confirming Email", false, true);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            send400Code = false;
                                                            this.setMessage400();
                                                            this.messageToSend += "resetCode";
                                                        }
                                                        break;
                                                    case "setEmailCode":
                                                        if(!user.isEmailVerified())
                                                        {
                                                            
                                                            boolean sendEmail = false;
                                                            if(user.getEmailDateTime() != null)
                                                            {
                                                                if(now.minusHours((long) 1).isAfter(user.getEmailDateTime()))
                                                                {
                                                                    sendEmail = true;
                                                                }
                                                            }
                                                            else
                                                            {
                                                                sendEmail = true;
                                                            }
                                                            
                                                            if(sendEmail)
                                                            {
                                                                String randomEmailCode = User.generateRandomEmailCode();
                                                                String emailCode = this.ip + now.toString() + randomEmailCode;
                                                                String safeHash = DataController.safeHashPassword(emailCode);
                                                                String message = "You have been sent a code to verify your email at Jumbo Dinosaurs. \n" + "To verify your email you need to visit https://www.jumbodinosaurs.com/verifyemail.html and enter your code." + "\n\nCode for Verification: " + randomEmailCode + " \n\n\n   Regards, Jumbo";
                                                                User updatedUserInfo = user.clone();
                                                                updatedUserInfo.setEmailCode(safeHash);
                                                                updatedUserInfo.setEmailDateTime(now);
                                                                
                                                                if(CredentialsManager.modifyUser(user, updatedUserInfo))
                                                                {
                                                                    if(DataController.sendEmail(user.getEmail(), "Email Verification Code", message))
                                                                    {
                                                                        
                                                                        this.messageToSend += sC200;
                                                                        this.messageToSend += closeHeader;
                                                                        this.messageToSend += "codeSent";
                                                                        send400Code = false;
                                                                    }
                                                                    else
                                                                    {
                                                                        OperatorConsole.printMessageFiltered("Error Sending Email Code", false, true);
                                                                    }
                                                                    
                                                                }
                                                                else
                                                                {
                                                                    OperatorConsole.printMessageFiltered("Error Setting User Info Email Code", false, true);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                send400Code = false;
                                                                this.setMessage400();
                                                                this.messageToSend += "codeCoolDown";
                                                            }
                                                            
                                                        }
                                                        else
                                                        {
                                                            send400Code = false;
                                                            this.setMessage400();
                                                            this.messageToSend += "emailAlreadyConfirmed";
                                                        }
                                                        break;
                                                    case "getUsername":
                                                        this.messageToSend += this.sC200;
                                                        this.messageToSend += this.closeHeader;
                                                        this.messageToSend += user.getUsername();
                                                        send400Code = false;
                                                        break;
                                                    case "getUserInfo":
                                                        this.messageToSend += this.sC200;
                                                        this.messageToSend += this.closeHeader;
                                                        this.messageToSend += user.getUserInfoJson();
                                                        send400Code = false;
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                
                                if(post != null && !send400Code)
                                {
                                    DataController.handOffPost(post);
                                    //Message to send is determined case by case
                                }
                                
                                if(send400Code)
                                {
                                    this.setMessage400();
                                }
                            }
                            else
                            {
                                this.setMessage400();
                            }
                        }
                        else
                        {
                            this.setMessage400();
                        }
                    }
                    catch(JsonParseException e)
                    {
                        this.setMessage400();
                    }
                }
                else
                {
                    this.setMessage400();
                }
            }
            else
            {
                this.setMessage400();
            }
        }
        else
        {
            this.setMessage501();
        }
        
    }
    
    
    //Given a nullable query with all fields nullable
    //this method will return an Array list of strings who's content is specified via the query's parameters
    public ArrayList<String> getQueryList(QueryRequest query)
    {
        ArrayList<String> content = new ArrayList<String>();
        if(query != null)
        {
            if(query.getFile() != null)
            {
                ArrayList<WritablePost> pastPosts = DataController.getPastPostsFromPath(query.getFile());
                if(query.isGetPosts())
                {
                    for(WritablePost pastPost : pastPosts)
                    {
                        
                        if(query.getUser() != null)
                        {
                            if(pastPost.getUser().equals(query.getUser()))
                            {
                                content.add(pastPost.toJsonString());
                            }
                        }
                        else if(query.getPostIdentifier() != null)
                        {
                            if(pastPost.getPostIdentifier().equals(query.getPostIdentifier()))
                            {
                                content.add(pastPost.toJsonString());
                            }
                        }
                        else if(query.getKeyword() != null)
                        {
                            if(pastPost.getContent().contains(query.getKeyword()) || pastPost.getPostIdentifier().contains(query.getKeyword()))
                            {
                                content.add(pastPost.toJsonString());
                            }
                        }
                    }
                }
                else
                {
                    String typeOfData = query.getTypeOfData();
                    if(typeOfData != null)
                    {
                        switch(typeOfData)
                        {
                            case "user":
                                
                                for(WritablePost pastPost : pastPosts)
                                {
                                    if(!content.contains(pastPost.getUser()))
                                    {
                                        content.add(pastPost.getUser());
                                    }
                                }
                                
                                break;
                            case "identifier":
                                for(WritablePost pastPost : pastPosts)
                                {
                                    if(!content.contains(pastPost.getPostIdentifier()))
                                    {
                                        content.add(pastPost.getPostIdentifier());
                                    }
                                }
                                break;
                        }
                    }
                    
                }
            }
            else
            {
                ArrayList<WritablePost> allPastPosts = DataController.getAllPostsList();
                if(query.isGetPosts())
                {
                    for(WritablePost pastPost : allPastPosts)
                    {
                        
                        if(query.getUser() != null)
                        {
                            if(pastPost.getUser().equals(query.getUser()))
                            {
                                content.add(pastPost.toJsonString());
                            }
                        }
                        else if(query.getPostIdentifier() != null)
                        {
                            if(pastPost.getPostIdentifier().equals(query.getPostIdentifier()))
                            {
                                content.add(pastPost.toJsonString());
                            }
                        }
                        else if(query.getKeyword() != null)
                        {
                            if(pastPost.getContent().contains(query.getKeyword()) || pastPost.getPostIdentifier().contains(query.getKeyword()))
                            {
                                content.add(pastPost.toJsonString());
                            }
                        }
                        
                    }
                }
                else
                {
                    String typeOfData = query.getTypeOfData();
                    if(typeOfData != null)
                    {
                        switch(typeOfData)
                        {
                            case "user":
                                
                                for(WritablePost pastPost : allPastPosts)
                                {
                                    if(!content.contains(pastPost.getUser()))
                                    {
                                        content.add(pastPost.getUser());
                                    }
                                }
                                
                                break;
                            case "identifier":
                                for(WritablePost pastPost : allPastPosts)
                                {
                                    if(!content.contains(pastPost.getPostIdentifier()))
                                    {
                                        content.add(pastPost.getPostIdentifier());
                                    }
                                }
                                break;
                            case "files":
                                File[] filesInPost = DataController.listFilesRecursive(DataController.postDirectory);
                                for(File file: filesInPost)
                                {
                                    String pathToParse = file.getAbsolutePath();
                                    if(pathToParse.substring(pathToParse.length() - 1).equals(File.separator))
                                    {
                                        pathToParse = pathToParse.substring(0, pathToParse.length() - 2);
                                    }
                                    // Example File Path /home/system/WebServer/POST/books.json
                                    // books is what should be put in content
                                    content.add(pathToParse.substring(pathToParse.lastIndexOf(File.separator) + 1, pathToParse.lastIndexOf(".")));
                                }
                             break;
                        }
                    }
                }
                
            }
        }
        return content;
    }
    
    public String getCensoredMessageSentToClient()
    {
        return this.messageToSend.substring(0, this.messageToSend.indexOf(this.closeHeader));
    }
    
    
    public boolean messageSentContained200Code()
    {
        return this.messageToSend.contains(this.sC200);
    }
    
    public String getCensoredMessageFromClient()
    {
        String postDataSent = this.getPostRequestUTF8();
        if(postDataSent != null)
        {
            try
            {
                String messageFromClientUTF = URLDecoder.decode(this.messageFromClient, "UTF-8");
                int indexOfPostData = messageFromClientUTF.indexOf(postDataSent);
                String allowedPostedData = "";
                if(postRequest != null)
                {
                    if(this.postRequest.getCommand() != null)
                    {
                        allowedPostedData += " Command: " + this.postRequest.getCommand() + " ";
                    }
                    if(this.postRequest.getUsername() != null)
                    {
                        allowedPostedData += " Username: " + this.postRequest.getUsername() + " ";
                    }
                    if(this.postRequest.getContent() != null)
                    {
                        allowedPostedData += " Content: " + this.postRequest.getContent() + " ";
                    }
                    if(this.postRequest.getListName() != null)
                    {
                        allowedPostedData += " List Name: " + this.postRequest.getListName() + " ";
                    }
                    if(this.postRequest.getConnectionName() != null)
                    {
                        allowedPostedData += " Connection Name: " + this.postRequest.getConnectionName() + " ";
                    }
                }
                return messageFromClientUTF.substring(messageFromClientUTF.indexOf("POST /"), "POST /".length()) + allowedPostedData + messageFromClientUTF.substring(messageFromClientUTF.lastIndexOf(" HTTP/1.1"));
            }
            catch(UnsupportedEncodingException e)
            {
            
            }
            return "UnsupportedEncodingException";
        }
        else
        {
            return this.messageFromClient;
        }
    }
    
    
    public Boolean leaveMessageTheSame()
    {
        return leaveMessageTheSame;
    }
    
    public static boolean lookUpEmail(String email)
    {
        boolean exists = false;
        if(email.contains("@") && email.indexOf("@") + 1 < email.length())
        {
            String domain = email.substring(email.indexOf("@") + 1);
            Hashtable env = new Hashtable();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            try
            {
                DirContext ictx = new InitialDirContext(env);
                Attributes attrs = ictx.getAttributes(domain, new String[]{"MX"});
                Attribute attr = attrs.get("MX");
                if(attr != null && attrs.size() > 0)
                {
                    exists = true;
                }
            }
            catch(NamingException e)
            {
                OperatorConsole.printMessageFiltered("Email Server Non-Existent", true, false);
            }
        }
        return exists;
    }
    
    
    public double getCaptchaScore(String captchaCode)
    {
        double score = 0;
        
        if(ServerControl.getArguments() == null || ServerControl.getArguments().getCaptchaKey() == null)
        {
            
            return .9;
        }
        
        try
        {
            String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + ServerControl.getArguments().getCaptchaKey() + "&response=" + captchaCode + "";
            
            URL address = new URL(url);
            BufferedReader sc = new BufferedReader(new InputStreamReader(address.openStream()));
            String response = "";
            while(sc.ready())
            {
                response += sc.readLine();
            }
            CaptchaResponse captchaResponse = new Gson().fromJson(response, CaptchaResponse.class);
            OperatorConsole.printMessageFiltered("Captcha Score:" + score, true, false);
            return captchaResponse.getScore();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Exception Getting Captcha Score", true, false);
        }
        return score;
    }
    
    
    
    
    public void setMessage400()
    {
        this.messageToSend += this.sC400;
        this.messageToSend += this.closeHeader;
    }
    
    //Sets the message to send as 404
    public void setMessage404()
    {
        this.messageToSend += this.sC404;
        this.messageToSend += this.closeHeader;
        this.messageToSend += DataController.getFileContents(DataController.safeSearchDir(DataController.getDirectory, "/404.html", true));
    }
    
    public void setMessage501()
    {
        this.messageToSend += this.sC501;
        this.messageToSend += this.closeHeader;
    }
    
    
    public boolean hasHostHeader()
    {
        String hostHead = "Host: ";
        return this.messageFromClient.contains(hostHead);
    }
    
    
    public String getHost()
    {
        if(DataController.getDomains() != null)
        {
            String hostHead = "Host: ";
            for(String host : DataController.getDomains())
            {
                if(this.messageFromClient.contains(hostHead + host) || this.messageFromClient.contains(hostHead + host.substring(4)))
                {
                    return host;
                }
            }
            
        }
        return DataController.host;
    }



    /*
    For Polishing of Get Requests
    Examples:

    If I Request "/index.html" with a host the server wil return /index.html instead of /host/index.html

    If I Request "/" with a Host Header The the Server will look for the file /host/home.html

    If I Request "/picture.png" with a Host Header the Server will look for the file /host/picture.png

     */
    
    
    public String mendPageRequest(String request)
    {
        if(!request.equals("/index.html"))
        {
            if(this.hasHostHeader())
            {
                if(!this.getHost().equals(DataController.host))//If it's a domain the server hosts
                {
                    if(request.equals("/"))
                    {
                        return "/" + this.getHost() + "/home.html";
                    }
                    else//some other file request then "home.html"
                    {
                        return "/" + this.getHost() + request;
                    }
                }
            }
        }
        return request;
    }
    
    public String getGetRequest()
    {
        String GET = "GET ";
        String HTTP = " HTTP/1.1";
        int indexofGET = this.messageFromClient.indexOf(GET);
        int indexofHTTP = this.messageFromClient.lastIndexOf(HTTP);
        if(indexofGET == 0 && indexofGET < indexofHTTP)
        {
            return this.messageFromClient.substring(indexofGET + GET.length(), indexofHTTP);
        }
        else
        {
            return null;
        }
    }
    
    public String getGetReplacedWithPost()
    {
        String temp = this.messageFromClient;
        String GET = "GET /";
        String POST = "POST /";
        String HTTP = " HTTP/1.1";
        int indexOfGet = temp.indexOf(GET);
        int indexOfHttp = temp.indexOf(HTTP);
        if(indexOfGet >= 0 && indexOfHttp > indexOfGet)
        {
            return POST + temp.substring(indexOfGet + GET.length());
        }
        return "";
    }
    
    public String getPostRequestUTF8()
    {
        String POST = "POST /";
        String HTTP = " HTTP/1.1";
        int indexofPOST = this.messageFromClient.indexOf(POST);
        int indexofHTTP = this.messageFromClient.lastIndexOf(HTTP);
        
        if(indexofPOST >= 0 && indexofPOST < indexofHTTP)
        {
            String postJson = this.messageFromClient.substring(indexofPOST + POST.length(), indexofHTTP);
            try
            {
                postJson = URLDecoder.decode(postJson, "UTF-8");
            }
            catch(Exception e)
            {
                OperatorConsole.printMessageFiltered("Error Decoding Post Message", false, true);
            }
            return postJson;
        }
        return null;
    }
    
    public String getPostRequestUTF8(String message)
    {
        String POST = "POST /";
        String HTTP = " HTTP/1.1";
        int indexofPOST = message.indexOf(POST);
        int indexofHTTP = message.lastIndexOf(HTTP);
        
        if(indexofPOST >= 0 && indexofPOST < indexofHTTP)
        {
            String postJson = message.substring(indexofPOST + POST.length(), indexofHTTP);
            try
            {
                postJson = URLDecoder.decode(postJson, "UTF-8");
            }
            catch(Exception e)
            {
                OperatorConsole.printMessageFiltered("Error Decoding Post Message", false, true);
            }
            return postJson;
        }
        return null;
    }
    
    public String getPostRequestUnchanged()
    {
        String POST = "POST /";
        String HTTP = " HTTP/1.1";
        int indexofPOST = this.messageFromClient.indexOf(POST);
        int indexofHTTP = this.messageFromClient.lastIndexOf(HTTP);
        
        if(indexofPOST >= 0 && indexofPOST < indexofHTTP)
        {
            String postJson = this.messageFromClient.substring(indexofPOST + POST.length(), indexofHTTP);
            return postJson;
        }
        return null;
    }

    
    public byte[] getByteArrayToSend()
    {
        return this.byteArrayToSend;
    }
    
    
    public String getMessageToSend()
    {
        return this.messageToSend;
    }
    
    @Override
    public String toString()
    {
        return "HTTPSRequest{" + "\r\n\r\nmessageFromClient='" + messageFromClient + '\'' + ",\r\n\r\n messageToSend='" + messageToSend + '\'' + ",\r\n\r\n ip='" + ip + '\'' + ",\r\n\r\n leaveMessageTheSame=" + leaveMessageTheSame + '}' + "\r\n\r\n";
    }
}

