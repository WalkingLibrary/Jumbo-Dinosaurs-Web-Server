package com.jumbodinosaurs.webserver.post.auth;

import com.google.gson.JsonObject;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.domain.DomainManager;
import com.jumbodinosaurs.webserver.domain.util.Domain;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.HTTPHeader;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.HeaderUtil;
import com.jumbodinosaurs.webserver.post.PostCommand;

public class GetPublicCaptchaCode extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /*
         * Process for sending a new Activation Email
         *
         * Check/Verify PostRequest Attributes
         *
         * Return Public Captcha Code if available
         *
         */
        HTTPResponse response = new HTTPResponse();
        
        //Check/Verify PostRequest Attributes
        //Note: the host should be in the content attribute
        if(request.getContent() == null)
        {
            response.setMessage400();
    
    
            return response;
        }
        
        
        String host = request.getContent();
        
        //We need to clean the domain given to us as the domains are stored as www.example.com not as
        // http://www.example.com/
        //Here we chop off the http part
        if(host.startsWith("http"))
        {
            host = host.substring(4);
            
            if(host.startsWith("s://"))
            {
                host = host.substring(1);
            }
            
            host = host.substring(3);
        }
        
        //here we chop of the / at the end
        if(host.endsWith("/"))
        {
            host = host.substring(0, host.length() - 1);
        }
    
    
        Domain domainSpecified = DomainManager.getDomain(host);
        
        if(domainSpecified == null)
        {
            response.setMessage400();
    
    
            return response;
        }
    
        if(domainSpecified.getCaptchaKey() == null)
        {
            response.setMessage400();
        
        
            return response;
        }
        HTTPHeader jsonApplicationTypeHeader = HeaderUtil.contentTypeHeader.setValue("json");
        JsonObject captchaCodeResponse = new JsonObject();
        captchaCodeResponse.addProperty("publicCaptchaCode", domainSpecified.getCaptchaKey().getSiteKey());
        response.setMessage200();
        response.addHeader(jsonApplicationTypeHeader);
        response.setBytesOut(captchaCodeResponse.toString().getBytes());
        return response;
    }
    
    @Override
    public boolean requiresSuccessfulAuth()
    {
        return false;
    }
    
    @Override
    public boolean requiresPasswordAuth()
    {
        return false;
    }
    
    @Override
    public boolean requiresUser()
    {
        return false;
    }
}
