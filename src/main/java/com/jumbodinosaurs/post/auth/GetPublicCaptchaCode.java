package com.jumbodinosaurs.post.auth;

import com.google.gson.JsonObject;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.Domain;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.post.PostCommand;

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
            
            System.out.println("content");
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
        System.out.println("HOST NOW:  " + host);
        
        Domain domainSpecified = DomainManager.getDomain(host);
        
        if(domainSpecified == null)
        {
            response.setMessage400();
            
            System.out.println("domain");
            return response;
        }
        
        if(domainSpecified.getCaptchaKey() == null)
        {
            response.setMessage400();
            
            System.out.println("key");
            return response;
        }
        System.out.println("");
        response.setMessage200();
        JsonObject captchaCodeResponse = new JsonObject();
        captchaCodeResponse.addProperty("publicCaptchaCode", domainSpecified.getCaptchaKey().getSiteKey());
        response.addPayload(captchaCodeResponse.toString());
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
