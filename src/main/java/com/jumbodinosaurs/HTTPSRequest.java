package com.jumbodinosaurs;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Boolean hasByteArray = false;
    private Boolean leaveMessageTheSame = true;


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
        if (this.isGet())
        {
            //Clean Name from get Request for dataIO
            String requestCheck = this.getGetRequest();
            if (requestCheck != null)
            {
                String fileToGet = this.mendPageRequest(requestCheck);

                File fileRequested;

                //If if have file
                OperatorConsole.printMessageFiltered("File To Get: " + fileToGet, true, false);
                if ((fileRequested = DataController.getFileFromGETDirectory(fileToGet)) != null)
                {
                    //add Good Code


                    String fileType = DataController.getType(fileRequested);
                    if (fileType.contains("png") ||
                            fileType.contains("jpeg") ||
                            fileType.contains("jpg") ||
                            fileType.contains("ico"))
                    {
                        if (!DataController.readPhoto(fileRequested).equals(""))
                        {
                            this.messageToSend += this.sC200;
                            this.messageToSend += this.contentImageHeader + DataController.getType(fileRequested);
                            //this.messageToSend += this.contentLengthHeader + dataIO.getPictureLength(fileRequested.getName());
                            this.messageToSend += this.closeHeader;
                            this.hasByteArray = true;
                            this.byteArrayToSend = DataController.readPhoto(fileRequested);

                        }
                        else
                        {
                            this.setMessage404();
                        }


                    }
                    else if (fileType.contains("zip"))
                    {
                        ;
                        this.messageToSend += this.sC200;
                        this.messageToSend += this.contentApplicationHeader + fileType;
                        this.messageToSend += this.closeHeader;
                        this.hasByteArray = true;
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
                else//Send 404 not found
                {
                    this.setMessage404();
                }
            }
            else
            {
                this.setMessage501();
            }
        }
        else if (this.isPost())
        {
            //To avoid saving password in the logs.json

            this.leaveMessageTheSame = false;
           /*
           //See Post Diagram for more Post Insight
           */
            if (OperatorConsole.allowPost())
            {
                String postJson = this.getPostRequestUTF8();
                System.out.println("Post Json: " + postJson);
                if (postJson != null)
                {
                    try
                    {
                        PostRequest postRequest = new Gson().fromJson(postJson, PostRequest.class);
                        WritablePost post = null;
                        boolean send400Code = true;
                        String command = postRequest.getCommand();

                        if (command != null)
                        {
                            if (command.equals("confirmMailServer"))
                            {
                                if (postRequest.getEmail() != null)
                                {
                                    if (lookUpEmail(postRequest.getEmail()))
                                    {
                                        this.messageToSend += sC200;
                                        this.messageToSend += closeHeader;
                                        send400Code = false;
                                    }
                                }
                            }
                            else if (command.equals("usernameCheck"))
                            {
                                if (postRequest.getUsername() != null)
                                {
                                    if (DataController.getCredentialsManager().usernameAvailable(postRequest.getUsername()))
                                    {
                                        this.messageToSend += sC200;
                                        this.messageToSend += closeHeader;
                                        send400Code = false;
                                    }
                                }
                            }
                            else if (command.equals("emailCheck"))
                            {
                                if (postRequest.getEmail() != null && (!DataController.isIPEmailCheckLocked(this.ip)))
                                {
                                    if (!DataController.getCredentialsManager().emailInUse(postRequest.getEmail()))
                                    {
                                        this.messageToSend += sC200;
                                        this.messageToSend += closeHeader;
                                        send400Code = false;
                                    }

                                    DataController.emailStrikeIP(this.ip);
                                }
                            }
                            else if (command.equals("createAccount"))
                            {
                                if (postRequest.getUsername() != null &&
                                        postRequest.getPassword() != null &&
                                        postRequest.getEmail() != null &&
                                        postRequest.getCaptchaCode() != null)
                                {
                                    if (getCaptchaScore(postRequest.getCaptchaCode()) > .5)
                                    {

                                        if (DataController.getCredentialsManager().createUser(postRequest.getUsername(), postRequest.getPassword(), postRequest.getEmail()))
                                        {
                                            send400Code = false;
                                            this.messageToSend += sC200;
                                            this.messageToSend += closeHeader;
                                        }
                                    }
                                }
                            }
                            else if (command.equals("resetPassword"))
                            {
                                if (postRequest.getEmail() != null)
                                {
                                    if (DataController.getCredentialsManager().emailInUse(postRequest.getEmail()))
                                    {
                                        User userToSendCodeTo = DataController.getCredentialsManager().getUserByEmail(postRequest.getEmail());
                                        LocalDateTime now = LocalDateTime.now();
                                        LocalDateTime tokenMintDate = userToSendCodeTo.getTokenDate();
                                        boolean sendEmail = false;

                                        if (tokenMintDate == null || !userToSendCodeTo.isTokenIsOneUse())
                                        {
                                            sendEmail = true;
                                        }
                                        else if(userToSendCodeTo.getLastLoginDate().isAfter(tokenMintDate))
                                        {
                                            sendEmail = true;
                                        }
                                        else if (now.minusMinutes((long) 2).isAfter(tokenMintDate))
                                        {
                                            sendEmail = true;
                                        }

                                        if (sendEmail)
                                        {
                                            String token = DataController.getCredentialsManager().getTokenOneUse(userToSendCodeTo, this.ip);
                                            String messageToSend = "There has been a password change request to the account linked to this email" +
                                                    " at https://jumbodinosaurs.com/. If this was not you feel free to contact us at jumbodinosaurs@gmail.com.\n\n" +
                                                    "If this was you visit https://www.jumbodinosaurs.com/changepassword.html and enter this code\n To change your password." +
                                                    "\n\n" + token;
                                            DataController.sendEmail(userToSendCodeTo.getEmail(), messageToSend);
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
                                if (!DataController.isIPCaptchaLocked(this.ip))
                                {
                                    if (postRequest.getUsername() != null && postRequest.getPassword() != null)
                                    {
                                        user = DataController.getCredentialsManager().loginUsernamePassword(postRequest.getUsername(), postRequest.getPassword());
                                        tokenLogin = false;
                                    }
                                    else if (postRequest.getToken() != null)
                                    {
                                        user = DataController.getCredentialsManager().loginToken(postRequest.getToken(), this.ip);
                                    }

                                    if (user == null)
                                    {
                                        DataController.loginStrikeIP(this.ip);
                                    }
                                }
                                else if (postRequest.getCaptchaCode() != null && getCaptchaScore(postRequest.getCaptchaCode()) > .5)
                                {
                                    if (postRequest.getUsername() != null && postRequest.getPassword() != null)
                                    {
                                        user = DataController.getCredentialsManager().loginUsernamePassword(postRequest.getUsername(), postRequest.getPassword());
                                        tokenLogin = false;
                                    }
                                    else if (postRequest.getToken() != null)
                                    {
                                        user = DataController.getCredentialsManager().loginToken(postRequest.getToken(), this.ip);
                                    }
                                }


                                if (user != null)
                                {
                                    String content = postRequest.getContent();
                                    LocalDateTime now = LocalDateTime.now();
                                    if (tokenLogin && user.isTokenIsOneUse())
                                    {
                                        if (command.equals("passwordChange"))
                                        {
                                            if (postRequest.getPassword() != null)
                                            {
                                                if (postRequest.getCaptchaCode() != null && this.getCaptchaScore(postRequest.getCaptchaCode()) > .7)
                                                {
                                                    String password = postRequest.getPassword();
                                                    User updatedUserInfo = user.clone();
                                                    updatedUserInfo.setPassword(DataController.safeHashPassword(password));

                                                    if (DataController.getCredentialsManager().modifyUser(user, updatedUserInfo))
                                                    {
                                                        String messageToSend = "The password for your account at https://jumbodinosaurs/ has been changed." +
                                                                "\nIf this was not you contact us at jumbodinosaurs@gmail.com.";
                                                        DataController.sendEmail(updatedUserInfo.getEmail(), messageToSend);
                                                        send400Code = false;
                                                        this.messageToSend += sC200;
                                                        this.messageToSend += closeHeader;
                                                        this.messageToSend += "passwordChanged";

                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        switch (command)
                                        {
                                            case "postBook":
                                                MinecraftWrittenBook book = new Gson().fromJson(content, MinecraftWrittenBook.class);
                                                if (book.isGoodPost())
                                                {
                                                    String localPath = "/booklist/books.json";
                                                    String username = user.getUsername();
                                                    content = this.rewriteHTMLEscapeCharacters(new Gson().toJson(book));
                                                    String date = LocalDate.now().toString();
                                                    post = new WritablePost(localPath, username, content, date);
                                                    send400Code = false;
                                                    this.messageToSend += sC200;
                                                    this.messageToSend += closeHeader;
                                                }
                                                break;

                                            case "postSign":
                                                MinecraftSign sign = new Gson().fromJson(content, MinecraftSign.class);
                                                if (sign.isGoodPost())
                                                {
                                                    String localPath = "/signlist/signlist.json";
                                                    String username = user.getUsername();
                                                    content = this.rewriteHTMLEscapeCharacters(new Gson().toJson(sign));
                                                    String date = LocalDate.now().toString();
                                                    post = new WritablePost(localPath, username, content, date);
                                                    send400Code = false;
                                                    this.messageToSend += sC200;
                                                    this.messageToSend += closeHeader;
                                                }
                                                break;

                                            case "postComment":
                                                //WIP
                                                break;

                                            case "getToken":
                                                if (!tokenLogin)
                                                {
                                                    this.messageToSend += sC200;
                                                    this.messageToSend += closeHeader;
                                                    this.messageToSend += DataController.getCredentialsManager().getToken(user, this.ip);
                                                    send400Code = false;
                                                }
                                                break;
                                            case "emailConfirm":
                                                LocalDateTime emailCodeSendDate = user.getEmailDateTime();
                                                if (!now.minusDays((long) 1).isAfter(emailCodeSendDate))
                                                {
                                                    try
                                                    {
                                                        String emailCode = this.ip + emailCodeSendDate.toString() + postRequest.getEmailCode();
                                                        if (PasswordStorage.verifyPassword(emailCode, user.getEmailCode()))
                                                        {
                                                            User userConfirmedEmail = user.clone();
                                                            userConfirmedEmail.setEmailVerified(true);
                                                            if (DataController.getCredentialsManager().modifyUser(user, userConfirmedEmail))
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
                                                    catch (PasswordStorage.CannotPerformOperationException e)
                                                    {
                                                        OperatorConsole.printMessageFiltered("CannotPerformOperationException Confirming Email", false, true);
                                                    }
                                                    catch (PasswordStorage.InvalidHashException e)
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
                                                if (!user.isEmailVerified())
                                                {

                                                    boolean sendEmail = false;

                                                    if (user.getEmailDateTime() != null)
                                                    {
                                                        if (now.minusHours((long) 1).isAfter(user.getEmailDateTime()))
                                                        {
                                                            sendEmail = true;
                                                        }
                                                    }
                                                    else
                                                    {
                                                        sendEmail = true;
                                                    }

                                                    if (sendEmail)
                                                    {
                                                        String randomEmailCode = User.generateRandomEmailCode();
                                                        String emailCode = this.ip + now.toString() + randomEmailCode;
                                                        String safeHash = DataController.safeHashPassword(emailCode);
                                                        String message = "You have been sent a code to verify your email at Jumbo Dinosaurs. \n" +
                                                                "To verify your email you need to visit https://www.jumbodinosaurs.com/verifyemail.html and enter your code." +
                                                                "\n\nCode for Verification: " + randomEmailCode + " \n\n\n   Regards, Jumbo";
                                                        User updatedUserInfo = user.clone();
                                                        updatedUserInfo.setEmailCode(safeHash);
                                                        updatedUserInfo.setEmailDateTime(now);

                                                        if (DataController.getCredentialsManager().modifyUser(user, updatedUserInfo))
                                                        {
                                                            if (DataController.sendEmail(user.getEmail(), message))
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
                                                this.messageToSend += sC200;
                                                this.messageToSend += closeHeader;
                                                this.messageToSend += user.getUsername();
                                                send400Code = false;
                                                break;
                                            case "getUserInfo":
                                                this.messageToSend += sC200;
                                                this.messageToSend += closeHeader;
                                                this.messageToSend += user.getUserInfoJson();
                                                send400Code = false;
                                                break;
                                        }
                                    }
                                }
                            }
                        }


                        if (post != null && !send400Code)
                        {
                            DataController.writePostData(post);
                            //Message to send is determined case by case
                        }

                        if (send400Code)
                        {
                            this.setMessage400();
                        }

                    }
                    catch (JsonParseException e)
                    {
                        OperatorConsole.printMessageFiltered("Post was not Json", true, false);
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

    }


    public boolean messageSentContained200Code()
    {
        if (this.messageToSend.contains(this.sC200))
        {
            return true;
        }
        return false;
    }

    public String getCensoredMessageFromClient()
    {
        String postDataSent = this.getPostRequestUnchanged();
        if (postDataSent != null)
        {
            int indexOfPostData = this.messageFromClient.indexOf(postDataSent);
            return this.messageFromClient.substring(0, indexOfPostData) +
                    this.messageFromClient.substring(postDataSent.length());
        }
        else
        {
            return this.messageFromClient;
        }
    }


    public Boolean logMessageFromClient()
    {
        return leaveMessageTheSame;
    }

    public boolean lookUpEmail(String email)
    {
        boolean exists = false;
        if (email.contains("@") && email.indexOf("@") + 1 < email.length())
        {
            String domain = email.substring(email.indexOf("@") + 1);
            Hashtable env = new Hashtable();
            env.put("java.naming.factory.initial",
                    "com.sun.jndi.dns.DnsContextFactory");
            try
            {
                DirContext ictx = new InitialDirContext(env);
                Attributes attrs = ictx.getAttributes
                        (domain, new String[]{"MX"});
                Attribute attr = attrs.get("MX");
                if (attr != null && attrs.size() > 0)
                {
                    exists = true;
                }
            }
            catch (Exception e)
            {
                OperatorConsole.printMessageFiltered("Error Checking Email", false, true);
            }
        }
        return exists;
    }


    public double getCaptchaScore(String captchaCode)
    {
        double score = 0;

        if (ServerControl.getArguments() == null || ServerControl.getArguments().getCaptchaKey() == null)
        {
            return .8;
        }

        try
        {
            String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + ServerControl.getArguments().getCaptchaKey() +
                    "&response=" + captchaCode + "";

            URL address = new URL(url);
            BufferedReader sc = new BufferedReader(new InputStreamReader(address.openStream()));
            String response = "";
            while (sc.ready())
            {
                response += sc.readLine();
            }
            CaptchaResponse captchaResponse = new Gson().fromJson(response, CaptchaResponse.class);
            OperatorConsole.printMessageFiltered("Captcha Score:" + score, true, false);
            return captchaResponse.getScore();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Setting Host", false, true);
        }
        return score;
    }


    public String rewriteHTMLEscapeCharacters(String postData)
    {
        String[][] charsToChange = {{"&", "&amp;"}, {"<", "&lt;"}, {">", "&gt;"}, {"\"", "&quot;"}, {"\'", "&apos;"}};

        String temp = postData;

        for (int i = 0; i < temp.length(); i++)
        {
            for (String[] escapeChar : charsToChange)
            {
                if (temp.substring(i, i + 1).equals(escapeChar[0]))
                {
                    temp = temp.substring(0, i) + escapeChar[1] + temp.substring(i + 1);
                    i += charsToChange[1].length;
                }
            }

        }
        return temp;
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
        this.messageToSend += DataController.getFileContents(DataController.getFileFromGETDirectory("/404.html"));
    }

    public void setMessage501()
    {
        this.messageToSend += this.sC501;
        this.messageToSend += this.closeHeader;
    }


    public boolean hasHostHeader()
    {
        String hostHead = "Host: ";
        if (this.messageFromClient.contains(hostHead))
        {
            return true;
        }
        return false;
    }


    public String getHost()
    {
        if (DataController.getDomains() != null)
        {
            String hostHead = "Host: ";
            for (String host : DataController.getDomains())
            {
                if (this.messageFromClient.contains(hostHead + host) ||
                        this.messageFromClient.contains(hostHead + host.substring(4)))
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
        if (!request.equals("/index.html"))
        {
            if (this.hasHostHeader())
            {
                if (!this.getHost().equals(DataController.host))//If it's a domain the server hosts
                {
                    if (request.equals("/"))
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
        int indexofHTTP = this.messageFromClient.indexOf(HTTP);
        if (indexofHTTP >= 0)
        {
            while (this.messageFromClient.substring(indexofHTTP + HTTP.length()).contains(HTTP))
            {
                indexofHTTP = this.messageFromClient.substring(indexofHTTP + 1).indexOf(HTTP);
            }
        }

        if (indexofGET <= 0 && indexofGET < indexofHTTP)
        {
            return this.messageFromClient.substring(indexofGET + GET.length(), indexofHTTP);
        }
        else
        {
            return null;
        }
    }

    public String getPostRequestUTF8()
    {
        String POST = "POST /";
        String HTTP = " HTTP/1.1";
        int indexofPOST = this.messageFromClient.indexOf(POST);
        int indexofHTTP = this.messageFromClient.indexOf(HTTP);
        if (indexofHTTP >= 0)
        {
            while (this.messageFromClient.substring(indexofHTTP + HTTP.length()).contains(HTTP))
            {
                indexofHTTP = this.messageFromClient.substring(indexofHTTP + 1).indexOf(HTTP);
            }
        }
        if (indexofPOST >= 0 && indexofPOST < indexofHTTP)
        {
            String postJson = this.messageFromClient.substring(indexofPOST + POST.length(), indexofHTTP);
            try
            {
                postJson = URLDecoder.decode(postJson, "UTF-8");
            }
            catch (Exception e)
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
        int indexofHTTP = this.messageFromClient.indexOf(HTTP);
        if (indexofHTTP >= 0)
        {
            while (this.messageFromClient.substring(indexofPOST + HTTP.length()).contains(HTTP))
            {
                indexofHTTP = this.messageFromClient.substring(indexofHTTP + 1).indexOf(HTTP);
            }
        }
        if (indexofPOST >= 0 && indexofPOST < indexofHTTP)
        {
            String postJson = this.messageFromClient.substring(indexofPOST + POST.length(), indexofHTTP);
            return postJson;
        }
        return null;
    }

    public boolean hasByteArray()
    {
        return this.hasByteArray;
    }

    public byte[] getByteArrayToSend()
    {
        return this.byteArrayToSend;
    }


    public String getMessageToSend()
    {
        return this.messageToSend;
    }
}

