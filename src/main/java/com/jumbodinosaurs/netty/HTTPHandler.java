package com.jumbodinosaurs.netty;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.ServerControl;
import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.objects.*;
import com.jumbodinosaurs.objects.HTTP.HTTPRequest;
import com.jumbodinosaurs.objects.HTTP.HTTPResponse;
import com.jumbodinosaurs.util.CredentialsManager;
import com.jumbodinosaurs.util.DataController;
import com.jumbodinosaurs.util.PasswordStorage;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;

public class HTTPHandler
{
    //headers
    private final String keepAlive = "\r\nConnection: keep-alive\r\n\r\n";
    private final String acceptedLanguageHeader = "\r\nAccept-Language: en-US";
    private final String originHeader = "\r\nOrigin: http://www.jumbodinosaurs.com/";
    private final String contentTextHeader = "\r\nContent-Type: text/";
    private final String contentImageHeader = "\r\nContent-Type: image/";
    private final String contentApplicationHeader = "\r\nContent-Type: application/";
    private final String contentLengthHeader = "\r\nContent-Length: "; //[length in bytes of the image]\r\n
    
    private HTTPRequest request;
    
    public HTTPHandler(HTTPRequest request)
    {
        this.request = request;
    }
    
    public HTTPResponse generateResponse()
    {
        HTTPResponse response = new HTTPResponse();
        String headers = "";
        //If Get Request
        if(this.request.isGet())
        {
            //Clean Name from get Request for dataIO
            String getRequest = this.request.getGetRequest();
            if(getRequest != null)
            {
                if(getRequest.equals("/"))
                {
                    getRequest = "/home.html";
                }
                
                File fileRequested = null;
                
                if(this.request.hasHostHeader())
                {
                    Domain requestsDomain = this.request.getDomainFromHostHeader();
                    if(requestsDomain != null)
                    {
                        
                        File temp = DataController.safeSearchDir(DataController.getDirectory,
                                                                 requestsDomain.getSecondLevelDomainName() + getRequest,
                                                                 true);
                        
                        if(temp == null)
                        {
                            fileRequested = DataController.safeSearchDir(DataController.getDirectory, getRequest, true);
                        }
                        else
                        {
                            fileRequested = temp;
                        }
                    }
                    else
                    {
                        fileRequested = DataController.safeSearchDir(DataController.getDirectory, getRequest, true);
                    }
                }
                else
                {
                    fileRequested = DataController.safeSearchDir(DataController.getDirectory, getRequest, true);
                }
                
                
                //If if have file
                if(fileRequested != null)
                {
                    //add Good Code
                    String fileType = DataController.getType(fileRequested);
                    if(fileType.contains("png") || fileType.contains("jpeg") || fileType.contains("JPG") || fileType.contains(
                            "ico"))
                    {
                        if(!DataController.readPhoto(fileRequested).equals(""))
                        {
                            
                            headers += this.contentImageHeader + DataController.getType(fileRequested);
                            byte[] photoBytes = DataController.readPhoto(fileRequested);
                            headers += this.contentLengthHeader + photoBytes.length;
                            //this.messageToSend += this.contentLengthHeader + dataIO.getPictureLength(fileRequested.getName());
                            response.setMessage200(headers, photoBytes);
                        }
                        else
                        {
                            response.setMessage404();
                        }
                        
                        
                    }
                    else if(fileType.contains("zip"))
                    {
                        headers += this.contentApplicationHeader + fileType;
                        byte[] zipBytes = DataController.readZip(fileRequested);
                        headers += this.contentLengthHeader + zipBytes.length;
                        response.setMessage200(headers, zipBytes);
                    }
                    else
                    {
                        headers += this.contentTextHeader + DataController.getType(fileRequested);
                        response.setMessage200(headers, DataController.getFileContents(fileRequested));
                    }
                }
                else//Send 404 not found server doesn't have the file
                {
                    //Make sure the client didn't send any thing that can be a post request
                    //If the get request can become a Post request then add it to the request for censoring
                    try
                    {
                        if(this.request.getGetRequestUTF8() != null && this.request.getGetRequestUTF8().length() > 1)
                        {
                            String postInfo = this.request.getGetRequest().substring(1);
                            PostRequest postRequest = new Gson().fromJson(postInfo, PostRequest.class);
                            this.request.setPostRequest(postRequest);
                            response.setMessage400();
                        }
                        else
                        {
                            response.setMessage404();
                        }
                    }
                    catch(JsonParseException e)
                    {
                        OperatorConsole.printMessageFiltered("GET was Not Json", true, false);
                        response.setMessage404();
                    }
                }
            }
            else
            {
                response.setMessage501();
            }
        }
        else if(this.request.isPost())
        {
            if(this.request.isEncryptedConnection())
            {
                //To avoid saving password in the logs.json
                /*
                //See Post Diagram for more Post Insight
                */
                if(OperatorConsole.allowPost())
                {
                    String postJson = this.request.getPostRequestUTF8();
                    //System.out.println("Post Json: " + postJson);
                    if(postJson != null)
                    {
                        try
                        {
                            PostRequest postRequest = new Gson().fromJson(postJson, PostRequest.class);
                            if(postRequest != null)
                            {
                                this.request.setPostRequest(postRequest);
                                response = generateResponse(this.request.getPostRequest());
                            }
                            else
                            {
                                response.setMessage400();
                            }
                        }
                        catch(JsonParseException e)
                        {
                            response.setMessage400();
                        }
                    }
                    else
                    {
                        response.setMessage400();
                    }
                }
                else
                {
                    response.setMessage400();
                }
            }
            else
            {
                response.setMessageToRedirectToHTTPS(this.request);
            }
        }
        else
        {
            response.setMessage501();
        }
        return response;
    }
    
    
    private HTTPResponse generateResponse(PostRequest postRequest)
    {
        HTTPResponse response = new HTTPResponse();
        
        if(postRequest.getCommand() != null)
        {
            String command = postRequest.getCommand();
            WritablePost post = null;
            boolean send400Code = true;
            double captchaScore = 0;
            
            if(postRequest.getCaptchaCode() != null)
            {
                captchaScore = this.getCaptchaScore(postRequest.getCaptchaCode());
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
                    String payload = "";
                    if(responseList.size() > 0)
                    {
                        payload += new Gson().toJson(responseList);
                    }
                    else
                    {
                        payload += "null";
                    }
                    response.setMessage200(payload);
                }
                else if(command.equals("confirmMailServer"))
                {
                    if(postRequest.getEmail() != null)
                    {
                        if(lookUpEmail(postRequest.getEmail()))
                        {
                            send400Code = false;
                            response.setMessage200();
                        }
                    }
                }
                else if(command.equals("usernameCheck"))
                {
                    if(postRequest.getUsername() != null)
                    {
                        if(DataController.getCredentialsManager().usernameAvailable(postRequest.getUsername()))
                        {
                            send400Code = false;
                            response.setMessage200();
                        }
                    }
                }
                else if(command.equals("emailCheck"))
                {
                    if(postRequest.getEmail() != null && ((!CredentialsManager.isIPEmailCheckLocked(this.request.getIp()) || CredentialsManager.isAbuserUnlocked(
                            this.request.getIp()))))
                    {
                        if(!DataController.getCredentialsManager().emailInUse(postRequest.getEmail()))
                        {
                            send400Code = false;
                            response.setMessage200();
                        }
                        
                        CredentialsManager.emailStrikeIP(this.request.getIp());
                    }
                }
                else if(command.equals("createAccount"))
                {
                    if(postRequest.getUsername() != null && postRequest.getPassword() != null && postRequest.getEmail() != null)
                    {
                        if(captchaScore > .5)
                        {
                            if(DataController.getCredentialsManager().createUser(postRequest.getUsername(),
                                                                                 postRequest.getPassword(),
                                                                                 postRequest.getEmail()))
                            {
                                send400Code = false;
                                response.setMessage200();
                            }
                        }
                    }
                }
                else if(command.equals("resetPassword"))
                {
                    if(postRequest.getEmail() != null && DataController.getCredentialsManager().emailInUse(postRequest.getEmail()))
                    {
                        User userToSendCodeTo = DataController.getCredentialsManager().getUserByEmail(postRequest.getEmail());
                        
                        
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
                                String token = DataController.getCredentialsManager().getTokenOneUse(userToSendCodeTo,
                                                                                                     this.request.getIp());
                                String messageToSend = "There has been a password change request to the account linked to this email" + " at https://jumbodinosaurs.com/. If this was not you contact us at jumbodinosaurs@gmail.com.\n\n" + "If this was you visit https://www.jumbodinosaurs.com/changepassword.html and enter this code\n To change your password." + "\n\n" + token;
                                DataController.sendEmail(userToSendCodeTo.getEmail(), "Password Change", messageToSend);
                            }
                        }
                        
                        
                    }
                    send400Code = false;
                    response.setMessage200();
                }
                else
                {
                    User user = null;
                    boolean tokenLogin = true;
                    if(!CredentialsManager.isIPCaptchaLocked(this.request.getIp()) || CredentialsManager.isAbuserUnlocked(
                            this.request.getIp()))
                    {
                        if(postRequest.getUsername() != null && postRequest.getPassword() != null)
                        {
                            user = DataController.getCredentialsManager().loginUsernamePassword(postRequest.getUsername(),
                                                                                                postRequest.getPassword());
                            tokenLogin = false;
                        }
                        else if(postRequest.getToken() != null)
                        {
                            user = DataController.getCredentialsManager().loginToken(postRequest.getToken(),
                                                                                     this.request.getIp());
                        }
                        
                        if(user == null)
                        {
                            CredentialsManager.loginStrikeIP(this.request.getIp());
                        }
                    }
                    else if(captchaScore > .5)
                    {
                        if(postRequest.getUsername() != null && postRequest.getPassword() != null)
                        {
                            user = DataController.getCredentialsManager().loginUsernamePassword(postRequest.getUsername(),
                                                                                                postRequest.getPassword());
                            tokenLogin = false;
                        }
                        else if(postRequest.getToken() != null)
                        {
                            user = DataController.getCredentialsManager().loginToken(postRequest.getToken(),
                                                                                     this.request.getIp());
                        }
                    }
                    
                    
                    if(user != null)
                    {
                        if(tokenLogin)
                        {
                            postRequest.setUsername(user.getUsername());
                        }
                        String content = postRequest.getContent();
                        LocalDateTime now = LocalDateTime.now();
                        if(tokenLogin && user.isTokenIsOneUse())
                        {
                            if(command.equals("passwordChange"))
                            {
                                if(postRequest.getPassword() != null && postRequest.getPassword().length() >= 9)
                                {
                                    
                                    String password = postRequest.getPassword();
                                    User updatedUserInfo = user.clone();
                                    updatedUserInfo.setPassword(DataController.safeHashPassword(password));
                                    
                                    if(CredentialsManager.modifyUser(user, updatedUserInfo))
                                    {
                                        String messageToSend = "The password for your account at https://jumbodinosaurs/ has been changed." + "\nIf this was not you contact us at jumbodinosaurs@gmail.com.";
                                        DataController.sendEmail(updatedUserInfo.getEmail(),
                                                                 "Password Changed",
                                                                 messageToSend);
                                        send400Code = false;
                                        response.setMessage200("passwordChanged");
                                        
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
                                        {}.getType();
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
                                            response.setMessage200();
                                        }
                                    }
                                    catch(JsonParseException e)
                                    {
                                        response.setMessage400();
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
                                            response.setMessage200();
                                        }
                                    }
                                    catch(JsonParseException e)
                                    {
                                        response.setMessage400();
                                    }
                                    break;
                                
                                case "postComment":
                                    //WIP
                                    break;
                                
                                case "getToken":
                                    if(!tokenLogin)
                                    {
                                        
                                        response.setMessage200(DataController.getCredentialsManager().getToken(user,
                                                                                                               this.request.getIp()));
                                        send400Code = false;
                                    }
                                    break;
                                case "emailConfirm":
                                    LocalDateTime emailCodeSendDate = user.getEmailDateTime();
                                    if(!now.minusDays((long) 1).isAfter(emailCodeSendDate))
                                    {
                                        try
                                        {
                                            String emailCode = this.request.getIp() + emailCodeSendDate.toString() + postRequest.getEmailCode();
                                            if(PasswordStorage.verifyPassword(emailCode, user.getEmailCode()))
                                            {
                                                User userConfirmedEmail = user.clone();
                                                userConfirmedEmail.setEmailVerified(true);
                                                if(CredentialsManager.modifyUser(user, userConfirmedEmail))
                                                {
                                                    send400Code = false;
                                                    response.setMessage200("emailConfirmed");
                                                }
                                                else
                                                {
                                                    OperatorConsole.printMessageFiltered(
                                                            "Error Confirming Email: Setting User Data",
                                                            false,
                                                            true);
                                                }
                                            }
                                        }
                                        catch(PasswordStorage.CannotPerformOperationException e)
                                        {
                                            OperatorConsole.printMessageFiltered(
                                                    "CannotPerformOperationException Confirming Email",
                                                    false,
                                                    true);
                                        }
                                        catch(PasswordStorage.InvalidHashException e)
                                        {
                                            OperatorConsole.printMessageFiltered("InvalidHashException Confirming Email",
                                                                                 false,
                                                                                 true);
                                        }
                                    }
                                    else
                                    {
                                        send400Code = false;
                                        response.setMessage400("resetCode");
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
                                            String emailCode = this.request.getIp() + now.toString() + randomEmailCode;
                                            String safeHash = DataController.safeHashPassword(emailCode);
                                            String message = "You have been sent a code to verify your email at Jumbo Dinosaurs. \n" + "To verify your email you need to visit https://www.jumbodinosaurs.com/verifyemail.html and enter your code." + "\n\nCode for Verification: " + randomEmailCode + " \n\n\n   Regards, Jumbo";
                                            User updatedUserInfo = user.clone();
                                            updatedUserInfo.setEmailCode(safeHash);
                                            updatedUserInfo.setEmailDateTime(now);
                                            
                                            if(CredentialsManager.modifyUser(user, updatedUserInfo))
                                            {
                                                if(DataController.sendEmail(user.getEmail(),
                                                                            "Email Verification Code",
                                                                            message))
                                                {
                                                    send400Code = false;
                                                    response.setMessage200("codeSent");
                                                }
                                                else
                                                {
                                                    OperatorConsole.printMessageFiltered("Error Sending Email Code",
                                                                                         false,
                                                                                         true);
                                                }
                                                
                                            }
                                            else
                                            {
                                                OperatorConsole.printMessageFiltered(
                                                        "Error Setting User Info Email Code",
                                                        false,
                                                        true);
                                            }
                                        }
                                        else
                                        {
                                            send400Code = false;
                                            response.setMessage400("codeCoolDown");
                                        }
                                        
                                    }
                                    else
                                    {
                                        send400Code = false;
                                        response.setMessage400("emailAlreadyConfirmed");
                                    }
                                    break;
                                case "getUsername":
                                    send400Code = false;
                                    response.setMessage200(user.getUsername());
                                    break;
                                case "getUserInfo":
                                    send400Code = false;
                                    response.setMessage200(user.getUserInfoJson());
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
                response.setMessage400();
            }
        }
        else
        {
            response.setMessage400();
        }
        return response;
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
                            if(pastPost.getContent().contains(query.getKeyword()) || pastPost.getPostIdentifier().contains(
                                    query.getKeyword()))
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
                            if(pastPost.getContent().contains(query.getKeyword()) || pastPost.getPostIdentifier().contains(
                                    query.getKeyword()))
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
                                for(File file : filesInPost)
                                {
                                    String pathToParse = file.getAbsolutePath();
                                    if(pathToParse.substring(pathToParse.length() - 1).equals(File.separator))
                                    {
                                        pathToParse = pathToParse.substring(0, pathToParse.length() - 2);
                                    }
                                    // Example File Path /home/system/WebServer/POST/books.json
                                    // books is what should be put in content
                                    content.add(pathToParse.substring(pathToParse.lastIndexOf(File.separator) + 1,
                                                                      pathToParse.lastIndexOf(".")));
                                }
                                break;
                        }
                    }
                }
                
            }
        }
        return content;
    }
    
    
    
    
    
    
    
    
}
