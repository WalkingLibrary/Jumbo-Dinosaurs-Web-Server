package com.jumbodinosaurs.netty.handler.http.util;

import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.auth.util.AuthUtil;
import com.jumbodinosaurs.auth.util.FailureReasons;
import com.jumbodinosaurs.auth.util.WatchListUtil;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.domain.util.Domain;
import com.jumbodinosaurs.post.PostCommand;
import com.jumbodinosaurs.post.PostCommandUtil;
import com.jumbodinosaurs.post.exceptions.NoSuchPostCommand;
import com.jumbodinosaurs.util.OptionUtil;
import com.jumbodinosaurs.util.ServerUtil;

import java.io.File;

public class HTTPResponseGenerator
{
    public static HTTPResponse generateResponse(HTTPMessage message)
    {
        if(message.getMethod().equals(Method.GET))
        {
            /* Dealing with GET Requests
             * We first need to analyze the file they are requesting and change it if need be
             * We then need to search our GET dir for the specified file
             * If we Don't have the file they are looking for we return a 404 message with the 404 page
             * Next we need to form our headers for the message which depends on the type of file we are sending
             */
            
            
            //We first need to analyze the file they are requesting and change it if need be
            String filePath = message.getPath();
            if(filePath.equals("/"))
            {
                filePath = "/home.html";
            }
            
            
            // We then need to search our GET dir for the specified file
            Domain messageHost = message.getDomain();
            File dirToSearch = ServerUtil.getDirectory;
            
            if(messageHost != null)
            {
                dirToSearch = messageHost.getGetDir();
            }
            
            File fileToServe = ServerUtil.safeSearchDir(dirToSearch, filePath, false);
            
            if(fileToServe == null)
            {
                fileToServe = ServerUtil.safeSearchDir(ServerUtil.getDirectory, filePath, false);
                
                //If we Don't have the file they are looking for we return a 404 message with the 404 page
                if(fileToServe == null)
                {
                    HTTPResponse response = new HTTPResponse();
                    response.setMessage404();
                    return response;
                }
            }
            
            //Next we need to form our headers for the message which depends on the type of file we are sending
            String headers = "";
            String type = GeneralUtil.getType(fileToServe);
            
            
            if(ResponseHeaderUtil.getImageFileTypes().contains(type))
            {
                byte[] photoBytes = ServerUtil.readPhoto(fileToServe);
                headers += ResponseHeaderUtil.contentImageHeader + type;
                headers += ResponseHeaderUtil.contentLengthHeader + photoBytes.length;
                HTTPResponse response = new HTTPResponse();
                response.setMessage200(headers, photoBytes);
                return response;
            }
            
            
            if(ResponseHeaderUtil.getApplicationFileTypes().contains(type))
            {
                byte[] applicationBytes = ServerUtil.readZip(fileToServe);
                headers += ResponseHeaderUtil.contentApplicationHeader + type;
                headers += ResponseHeaderUtil.contentLengthHeader + applicationBytes.length;
                HTTPResponse response = new HTTPResponse();
                response.setMessage200(headers, applicationBytes);
                return response;
            }
            
            
            headers += ResponseHeaderUtil.contentTextHeader + type;
            HTTPResponse response = new HTTPResponse();
            response.setMessage200(headers, GeneralUtil.scanFileContents(fileToServe));
            return response;
        }
        else if(message.getMethod().equals(Method.POST))
        {
            /* Process for dealing with POST requests
             * Check for server's settings for post
             * Verify Post requests integrity
             * Generate Auth Session from Post Request
             * Filter Auth Session failure Codes for shortcut responses
             * Filter AuthSessions and Auth tries via success and IP (Make it so you can't brute force)
             * Get the Post Commands
             * Filter for the Post Requests Command
             * Execute/Return That Commands getResponse Method
             *
             * */
            
            //Check for server's settings for post
            if(!OptionUtil.allowPost())
            {
                HTTPResponse response = new HTTPResponse();
                response.setMessage501();
                return response;
            }
            
            //Verify Post requests integrity
            PostRequest request = message.getPostRequest();
            if(request == null || request.getCommand() == null)
            {
                HTTPResponse response = new HTTPResponse();
                response.setMessage400();
                return response;
            }
            
            //Generate Auth Session from Post Request
            AuthSession authSession = AuthUtil.authenticateUser(request);
            
            
            
            //Filter Auth Session failure Codes for shortcut responses
            //Note: Filter Should allow following post commands to assume a user from auth session
            if(authSession.getFailureCode().equals(FailureReasons.NO_DATABASE))
            {
                HTTPResponse response = new HTTPResponse();
                response.setMessage501();
                return response;
            }
    
            //Filter AuthSessions and Auth tries via success and IP (Make it so you can't easily brute force)
            if(!authSession.isSuccess())
            {
                if(authSession.getFailureCode()
                              .equals(FailureReasons.INCORRECT_PASSWORD) || authSession.getFailureCode()
                                                                                       .equals(FailureReasons.INCORRECT_TOKEN))
                {
                    WatchListUtil.strikeUser(message.getIp());
                    HTTPResponse response = new HTTPResponse();
                    response.setMessage403();
                    return response;
                }
            }
            
            if(!WatchListUtil.shouldAcceptRequest(message.getIp()))
            {
                HTTPResponse response = new HTTPResponse();
                response.setMessage403();
                return response;
            }
            
            
            
            
            
            PostCommand commandToExecute;
    
            try
            {
                commandToExecute = PostCommandUtil.getPostCommand(request.getCommand());
            }
            catch(NoSuchPostCommand error)
            {
                HTTPResponse response = new HTTPResponse();
                response.setMessage400();
                return response;
            }
            
            commandToExecute.setIp(message.getIp());
            
            //If the post command requires a user then shortcut the response to 400
            if(authSession.getUser() == null && commandToExecute.requiresUser())
            {
                HTTPResponse response = new HTTPResponse();
                response.setMessage400();
                return response;
            }
            
            return commandToExecute.getResponse(request, authSession);
        }
        
        HTTPResponse response = new HTTPResponse();
        response.setMessage501();
        return response;
    }
    
    
}
