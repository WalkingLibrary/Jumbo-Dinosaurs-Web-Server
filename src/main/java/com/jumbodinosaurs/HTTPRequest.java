package com.jumbodinosaurs;

import com.google.gson.Gson;

import java.io.File;

public class HTTPRequest
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

    private String messageFromClient;
    private String messageToSend;
    private byte[] byteArrayToSend;
    private Boolean hasByteArray = false;
    private String contentTextHeader = "\r\nContent-Type: text/";
    private String contentImageHeader = "\r\nContent-Type: image/";
    private String contentZipHeader = "\r\nContent-Type: application/";
    private String contentLengthHeader = "\r\nContent-Length: "; //[length in bytes of the image]\r\n

    public HTTPRequest(String messageFromClient)
    {
        this.messageFromClient = messageFromClient;
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
                if ((fileRequested = DataController.getFileFromAllowedDirectory(fileToGet)) != null)
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
                        this.messageToSend += this.contentZipHeader + fileType;
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
            this.tryToRedirectToHTTPS();
        }

    }


    public void tryToRedirectToHTTPS()
    {
        boolean redirect = false;
        for (String host : DataController.getDomains())
        {
            if (this.getHost().equals(host))
            {
                this.messageToSend += this.sC301;//Redirect Header

                //Need To Craft Location Header From Host header.
                //if no host header then server should redirect to current ip
                this.messageToSend += this.locationHeader + " https://" + host + this.getGetRequest();

                this.messageToSend += this.closeHeader;
                redirect = true;
                break;
            }
        }

        if (!redirect)
        {
            generateMessage();
        }

    }


    //Sets the message to send as 404
    public void setMessage404()
    {
        this.messageToSend += this.sC404;
        this.messageToSend += this.closeHeader;
        this.messageToSend += DataController.getFileContents(DataController.getFileFromAllowedDirectory("/404.html"));
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
