package com.jumbodinosaurs.objects.HTTP;

import com.google.gson.Gson;
import com.jumbodinosaurs.ServerControl;
import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.objects.Domain;
import com.jumbodinosaurs.objects.PostRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class HTTPRequest
{
    private String messageFromClient;
    private boolean encryptedConnection;
    private PostRequest postRequest;
    private String ip;
    
    public HTTPRequest(String messageFromClient,
                       boolean encryptedConnection,
                       String ip)
    {
        this.messageFromClient = messageFromClient;
        this.encryptedConnection = encryptedConnection;
        this.ip = ip;
    }
    
    public boolean hasHostHeader()
    {
        String headers = getClientMessageHeaders();
        if(headers != null)
        {
            String hostHead = "Host: ";
            return headers.contains(hostHead);
        }
        return false;
    }
    
    public String getClientMessageHeaders()
    {
        String headers = null;
        if(this.messageFromClient.contains("HTTP/1.1"))
        {
            headers = this.messageFromClient.substring(this.messageFromClient.lastIndexOf("HTTP/1.1"));
        }
        return headers;
    }
    
    
    public Domain getDomainFromHostHeader()
    {
        if(ServerControl.getArguments() != null)
        {
            ArrayList<Domain> domains = ServerControl.getArguments().getDomains();
            if(domains != null)
            {
                String headers = getClientMessageHeaders();
                String hostHead = "Host: ";
                for(Domain domain : domains)
                {
                    if(headers.contains(hostHead + domain.getDomain()))
                    {
                        return domain;
                    }
                }
            }
        }
        return null;
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
    
    
    public String getGetRequestUTF8()
    {
        String GET = "GET ";
        String HTTP = " HTTP/1.1";
        int indexofGET = this.messageFromClient.indexOf(GET);
        int indexofHTTP = this.messageFromClient.lastIndexOf(HTTP);
        if(indexofGET == 0 && indexofGET < indexofHTTP)
        {
            try
            {
                return URLDecoder.decode(this.messageFromClient.substring(indexofGET + GET.length(), indexofHTTP), "UTF-8");
            }
            catch(UnsupportedEncodingException e)
            {
                OperatorConsole.printMessageFiltered("Error Decoding Get",true, false);
            }
        }
        
        return null;
        
    }
    
    public boolean isEncryptedConnection()
    {
        return encryptedConnection;
    }
    
    public PostRequest getPostRequest()
    {
        return postRequest;
    }
    
    public void setPostRequest(PostRequest postRequest)
    {
        this.postRequest = postRequest;
    }
    
    public String getIp()
    {
        return ip;
    }
    
    
    //should only be called if HTTPHandler has dealt with (this) request
    public String getCensoredMessage()
    {
        if(this.isPost())
        {
            if(this.postRequest != null)
            {
                return "POST /" + new Gson().toJson(this.postRequest.getCensored()) + " HTTP/1.1";
            }
        }
        else if(this.isGet())
        {
            if(this.postRequest != null)
            {
                return "GET /" + new Gson().toJson(this.postRequest.getCensored()) + " HTTP/1.1";
            }
        }
        return messageFromClient;
    }
}
