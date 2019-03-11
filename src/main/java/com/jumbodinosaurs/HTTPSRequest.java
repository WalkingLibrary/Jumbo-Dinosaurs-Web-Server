package com.jumbodinosaurs;

import com.google.gson.Gson;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private Boolean logMessageFromClient = true;


    public HTTPSRequest(String messageFromClient, String ip)
    {
        this.messageFromClient = messageFromClient;
        this.ip = ip;
        this.messageToSend = "";
    }

    public static String desanitizeDoubleQuote(String str)
    {
        String temp = str;
        String doubleQuote = "%22";
        while (temp.contains(doubleQuote))
        {
            temp = temp.substring(0, temp.indexOf(doubleQuote)) + "\"" + temp.substring(temp.indexOf(doubleQuote) + doubleQuote.length());
        }
        return temp;
    }

    public static String desanitizeLeftBracket(String str)
    {
        String temp = str;
        String leftBracket = "%7B";
        while (temp.contains(leftBracket))
        {
            temp = temp.substring(0, temp.indexOf(leftBracket)) + "{" + temp.substring(temp.indexOf(leftBracket) + leftBracket.length());
        }
        return temp;
    }

    public static String desanitizeRightBracket(String str)
    {
        String temp = str;
        String rightBracket = "%7D";
        while (temp.contains(rightBracket))
        {
            temp = temp.substring(0, temp.indexOf(rightBracket)) + "}" + temp.substring(temp.indexOf(rightBracket) + rightBracket.length());
        }
        return temp;
    }

    public static String desanitizeToken(String token)
    {
        return desanitizeDoubleQuote(desanitizeLeftBracket(desanitizeRightBracket(token)));
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
           /*Post Message Examples
           POST /{"username":"joe", "password":"password", "command":"postBook", "content":"BlahBlah(BookJson)"} HTTP/1.1

           //See Post Game Plan Diagram for more Post Insight
           */
            String POST = "POST /";
            String HTTP = "HTTP/1.1";
            int indexofPOST = this.messageFromClient.indexOf(POST);
            int indexofHTTP = this.messageFromClient.indexOf(HTTP);

            if (indexofPOST >= 0 && indexofPOST < indexofHTTP)
            {
                String postJson = this.messageFromClient.substring(this.messageFromClient.indexOf(POST) + POST.length(),
                        this.messageFromClient.indexOf(HTTP));

                postJson = desanitizeDoubleQuote(postJson);
                postJson = desanitizeLeftBracket(postJson);
                postJson = desanitizeRightBracket(postJson);

                //System.out.println("Message From Client: " + this.messageFromClient);
                System.out.println(postJson);
                try
                {
                    PostRequest postRequest = new Gson().fromJson(postJson, PostRequest.class);
                    this.logMessageFromClient = false;
                    WritablePost post = null;
                    boolean send400Code = true;
                    String command = postRequest.getCommand();

                    if (command != null)
                    {
                        System.out.println(postRequest.toString());
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
                                if (DataController.usernameAvailable(postRequest.getUsername()))
                                {
                                    this.messageToSend += sC200;
                                    this.messageToSend += closeHeader;
                                    send400Code = false;
                                }
                            }
                        }
                        else if (command.equals("emailCheck"))
                        {
                            if (postRequest.getEmail() != null && (!DataController.isIPCaptchaLocked(this.ip)))
                            {
                                if (!DataController.emailInUse(postRequest.getEmail()))
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

                                    if (DataController.createUser(postRequest.getUsername(), postRequest.getPassword(), postRequest.getEmail()))
                                    {
                                        send400Code = false;
                                        this.messageToSend += sC200;
                                        this.messageToSend += closeHeader;
                                    }
                                }
                            }
                        }
                        else
                        {
                            User user = null;
                            if (!DataController.isIPCaptchaLocked(this.ip))
                            {
                                if (postRequest.getUsername() != null && postRequest.getPassword() != null)
                                {
                                    user = DataController.loginUsernamePassword(postRequest.getUsername(), postRequest.getPassword());
                                }
                                else if (postRequest.getToken() != null)
                                {
                                    user = DataController.loginToken(postRequest.getToken(), this.ip);
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
                                    user = DataController.loginUsernamePassword(postRequest.getUsername(), postRequest.getPassword());
                                }
                                else if (postRequest.getToken() != null)
                                {
                                    user = DataController.loginToken(postRequest.getToken(), this.ip);
                                }
                            }

                            if (user != null)
                            {


                                String content = postRequest.getContent();
                                LocalDate dateNow = LocalDate.now();
                                LocalTime timeNow = LocalTime.now();
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
                                        this.messageToSend += sC200;
                                        this.messageToSend += closeHeader;
                                        this.messageToSend += DataController.getUserToken(user, this.ip);
                                        send400Code = false;
                                        break;

                                    case "debug":
                                        this.messageToSend += sC200;
                                        this.messageToSend += closeHeader;
                                        this.messageToSend += "bugged";
                                        send400Code = false;
                                        break;

                                    case "emailConfirm":
                                        LocalDate emailCodeSendDate = LocalDate.parse(user.getEmailCodeSentDate());
                                        if (!dateNow.minusDays((long) 1).isAfter(emailCodeSendDate))
                                        {
                                            try
                                            {
                                                String emailCode = this.ip + emailCodeSendDate.toString() + postRequest.getEmailCode();
                                                if (PasswordStorage.verifyPassword(emailCode, user.getEmailCode()))
                                                {
                                                    User userConfirmedEmail = user;
                                                    userConfirmedEmail.setEmailVerified(true);
                                                    if (DataController.modifyUser(user, userConfirmedEmail))
                                                    {
                                                        this.messageToSend += sC200;
                                                        this.messageToSend += closeHeader;
                                                        send400Code = false;
                                                    }
                                                    else
                                                    {
                                                        OperatorConsole.printMessageFiltered("Error Confirming Email", false, true);
                                                    }
                                                }
                                            }
                                            catch (Exception e)
                                            {
                                                OperatorConsole.printMessageFiltered("Error Confirming Email", false, true);
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
                                            LocalTime timeLastCodeWasSent = LocalTime.parse(user.getEmailCodeSentTime());
                                            if (user.getEmailCodesSentPastHour() < 3 || timeNow.minusHours(1).isAfter(timeLastCodeWasSent))
                                            {
                                                String randomEmailCode = User.generateRandomEmailCode();
                                                String emailCode = this.ip + dateNow.toString() + randomEmailCode;
                                                String safeHash = DataController.safeHashPassword(emailCode);
                                                String message = "Code for Verification: " + randomEmailCode;
                                                User newUser = user;
                                                newUser.setEmailCode(safeHash);
                                                newUser.setEmailCodeSentDate(dateNow.toString());
                                                if (DataController.modifyUser(user, newUser))
                                                {
                                                    if (DataController.sendEmail(user.getEmail(), message))
                                                    {

                                                        this.messageToSend += sC200;
                                                        this.messageToSend += closeHeader;
                                                        send400Code = false;
                                                    }
                                                }
                                                if (send400Code)
                                                {
                                                    OperatorConsole.printMessageFiltered("Error Setting Email Code", false, true);
                                                }
                                            }
                                            else
                                            {
                                                send400Code = false;
                                                this.setMessage400();
                                                this.messageToSend += "codeCoolDown";
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }

                    if (post != null)
                    {
                        DataController.writePostData(post);
                        //Message to send is determined case by case
                    }


                    if (send400Code)
                    {
                        this.setMessage400();
                    }

                }
                catch (Exception e)
                {
                    OperatorConsole.printMessageFiltered("Error Reading Post", false, true);
                    e.printStackTrace();
                    this.setMessage400();
                }
            }
            else
            {
                this.setMessage400();
            }
        }
    }

    public Boolean logMessageFromClient()
    {
        return logMessageFromClient;
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
        System.out.println("Exists: " + exists);
        return exists;
    }


    public double getCaptchaScore(String captchaCode)
    {
        double score = 0;

        if (ServerControl.getArguments() == null || ServerControl.getArguments().getCaptchaKey() == null)
        {
            //
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
            System.out.println("Score: " + captchaResponse.getScore());
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
        if (this.messageFromClient.indexOf("GET ") > -1 &&
                this.messageFromClient.indexOf(" HTTP/1.1") > -1 &&
                this.messageFromClient.indexOf("GET") + 4 < this.messageFromClient.indexOf(" HTTP/1.1"))
        {
            return this.messageFromClient.substring(this.messageFromClient.indexOf("GET ") + 4, this.messageFromClient.indexOf(" HTTP/1.1"));
        }
        else
        {
            return null;
        }
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

