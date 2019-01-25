package com.jumbodinosaurs;

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
    private String message, messageToSend;
    private String pictureContents = "";
    private Boolean pictureRequest = false;
    private String contentTextHeader = "\r\nContent-Type: text/";
    private String contentImageHeader = "\r\nContent-Type: image/";
    private String contentLengthHeader = "\r\nContent-Length: "; //[length in bytes of the image]\r\n

    public HTTPRequest(String message)
    {
        this.message = message;
        this.messageToSend = "";
    }

    public boolean isGet()
    {
        return this.message.indexOf("GET") > -1;
    }

    public boolean isHTTP()
    {
        return this.message.indexOf(" HTTP/1.1") > -1;
    }

    public void generateMessage(DataController dataIO)
    {
        //If Get Request
        if (isGet())
        {
            //Clean Name from get Request for dataIO
            String requestCheck = this.getGetRequest();
            if (requestCheck != null)
            {
                String fileToGet = this.mendPageRequest(requestCheck, dataIO);

                File fileRequested;

                //If if have file
                System.out.println("File To Get: " + fileToGet);
                if ((fileRequested = dataIO.getFileSafe(fileToGet)) != null)
                {
                    //add Good Code


                    if (dataIO.getType(fileRequested).contains("png") ||
                            dataIO.getType(fileRequested).contains("jpeg") ||
                            dataIO.getType(fileRequested).contains("jpg") ||
                            dataIO.getType(fileRequested).contains("ico"))
                    {

                        if (!dataIO.getPictureContents(fileRequested.getAbsolutePath()).equals(""))
                        {
                            this.messageToSend += this.sC200;
                            this.messageToSend += this.contentImageHeader + dataIO.getType(fileRequested);
                            //this.messageToSend += this.contentLengthHeader + dataIO.getPictureLength(fileRequested.getName());
                            this.messageToSend += this.closeHeader;
                            this.pictureRequest = true;
                            this.pictureContents = dataIO.getPictureContents(fileRequested.getName());

                        }
                        else
                        {
                            this.setMessage404(dataIO);
                        }


                    }
                    else
                    {
                        this.messageToSend += this.sC200;
                        this.messageToSend += this.contentTextHeader + dataIO.getType(fileRequested);
                        this.messageToSend += this.closeHeader;
                        this.messageToSend += dataIO.getFileContents(fileRequested);

                    }
                }
                else//Send 404 not found
                {
                    this.setMessage404(dataIO);
                }
            }
            else
            {
                this.setMessage501(dataIO);
            }
        }

    }

    //Sets the message to send as 404
    public void setMessage404(DataController dataIO)
    {
        this.messageToSend += this.sC404;
        this.messageToSend += this.closeHeader;
        this.messageToSend += dataIO.getFileContents(dataIO.getFile("404.html"));
    }

    public void setMessage501(DataController dataIO)
    {
        this.messageToSend += this.sC501;
        this.messageToSend += this.closeHeader;
    }


    public boolean hasHostHeader()
    {
        String hostHead = "Host: ";
        if (this.message.contains(hostHead))
        {
            return true;
        }
        return false;
    }


    //Returns the path for the home page of the hosted domains
    //Example if we host www.spawnmasons.com this should return /spawnmasons/home.html
    public String getHostedDomainPathHomePage(DataController dataIO)
    {
        String hostHead = "Host: ";
        if (dataIO.getDomains() != null)
        {
            for (String host : dataIO.getDomains())
            {
                if (this.message.contains(hostHead + host))
                {
                    //TO BE MENDED IN THE FUTURE. All Hosted domains at this time are www.something.com
                    //indexOF(".") safe cause it's operator input
                    return "/" + host.substring(host.indexOf(".") + 1, host.lastIndexOf(".")) + "/home.html";
                }
            }
        }
        return null;
    }

    //If there is a host header server will try and limit file search to host file
    public String addDomainPathfromHost(DataController dataIO, String fileRequested)
    {
        String hostHead = "Host: ";
        if (dataIO.getDomains() != null)
        {
            for (String host : dataIO.getDomains())
            {
                if (this.message.contains(hostHead + host))
                {
                    //TO BE MENDED IN THE FUTURE. All Hosted domains at this time are www.something.com
                    //indexOF(".") safe cause it's operator input
                    return "/" + host.substring(host.indexOf(".") + 1, host.lastIndexOf(".")) + fileRequested;
                }
            }
        }
        return fileRequested;
    }


    //For Polishing of Get Requests File Name
    //Example If  I request "/" The server will return /domainnamehere/home.html, Site index
    // if no host header and the request if not "/"
    public String mendPageRequest(String request, DataController dataIO)
    {
        if (!request.contains("index.html"))
        {
            if (request.equals("/"))
            {
                if (this.hasHostHeader())
                {
                    String temp = this.getHostedDomainPathHomePage(dataIO);
                    if (temp != null)
                    {
                        return temp;
                    }
                    else
                    {
                        return "index.html";
                    }
                }

            }
            else if (this.hasHostHeader())
            {
                return this.addDomainPathfromHost(dataIO, request);
            }
        }
        return request;
    }

    public String getGetRequest()
    {
        if (this.message.indexOf("GET ") > -1 &&
                this.message.indexOf(" HTTP/1.1") > -1 &&
                this.message.indexOf("GET") + 4 < this.message.indexOf(" HTTP/1.1"))
        {
            return this.message.substring(this.message.indexOf("GET ") + 4, this.message.indexOf(" HTTP/1.1"));
        }
        else
        {
            return null;
        }
    }

    public boolean isPictureRequest()
    {
        return this.pictureRequest;
    }

    public String getPictureContents()
    {
        return this.pictureContents;
    }


    public String getMessageToSend()
    {
        return this.messageToSend;
    }
}
