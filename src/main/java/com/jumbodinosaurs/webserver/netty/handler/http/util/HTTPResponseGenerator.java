package com.jumbodinosaurs.webserver.netty.handler.http.util;

import com.jumbodinosaurs.devlib.email.Email;
import com.jumbodinosaurs.devlib.email.EmailManager;
import com.jumbodinosaurs.devlib.email.NoSuchEmailException;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.auth.util.AuthUtil;
import com.jumbodinosaurs.webserver.auth.util.FailureReasons;
import com.jumbodinosaurs.webserver.auth.util.WatchListUtil;
import com.jumbodinosaurs.webserver.domain.util.Domain;
import com.jumbodinosaurs.webserver.log.Session;
import com.jumbodinosaurs.webserver.netty.handler.http.exceptions.NoSuchHeaderException;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.ClientHeaderPatterns;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.ContentTypeUtil;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.HTTPHeader;
import com.jumbodinosaurs.webserver.netty.handler.http.util.header.HeaderUtil;
import com.jumbodinosaurs.webserver.netty.handler.websocket.WebSocketHandler;
import com.jumbodinosaurs.webserver.netty.handler.websocket.WebSocketUtil;
import com.jumbodinosaurs.webserver.post.PostCommand;
import com.jumbodinosaurs.webserver.post.PostCommandUtil;
import com.jumbodinosaurs.webserver.post.exceptions.NoSuchPostCommand;
import com.jumbodinosaurs.webserver.util.OptionUtil;
import com.jumbodinosaurs.webserver.util.ServerUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.websocketx.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;

public class HTTPResponseGenerator
{
    private final Session sessionContext;
    
    public HTTPResponseGenerator(Session sessionContext)
    {
        this.sessionContext = sessionContext;
    }
    
    public HTTPResponse generateResponse(HTTPMessage message)
    {
    
    
        if(message.getMethod().equals(Method.GET))
        {
            /* Dealing with GET Requests
             * We First need to check for upgrade requests
             * Then We need to analyze the file they are requesting and change it if need be
             * We then need to search our GET dir for the specified file
             * If we Don't have the file they are looking for we return a 404 message with the 404 page
             * Next we need to form our headers for the message which depends on the type of file we are sending
             */
    
    
            //We First need to check for upgrade requests
            //Detect Upgrade Requests
            try
            {
                HTTPHeader upgradeHeader = HeaderUtil.extractHeader(message, ClientHeaderPatterns.UPGRADE_HEADER);
    
                //We check to see if the server options allow Web Socket Connections
                if(!OptionUtil.allowWebSocketConnections())
                {
                    HTTPResponse response = new HTTPResponse();
                    response.setMessage409();
                    return response;
                }
    
                /*
                 * Process For Starting Websocket Session
                 * Parse Request info
                 * Create response Key
                 * Edit Pipeline
                 * Create Handshake Response
                 *
                 *  */
                if(upgradeHeader.getValue().equals("websocket"))
                {
                    /* Process for Parsing Request Info
                     * Get Client Key
                     * Get Web Socket Version Default is 00
                     *
                     * */
                    //Get Client Key
                    String clientKey;
                    try
                    {
                        HTTPHeader clientKeyHeader = HeaderUtil.extractHeader(message,
                                                                              ClientHeaderPatterns.WEB_SOCKET_KEY);
                        clientKey = clientKeyHeader.getValue();
                    }
                    catch(NoSuchHeaderException e)
                    {
                        HTTPResponse response = new HTTPResponse();
                        response.setMessage400();
                        return response;
                    }
        
                    /* Get Web Socket Version Default is 00
                     * Netty Supported Versions
                     * 00
                     * 07
                     * 08
                     * 13
                     *  */
        
                    int defaultMaxFrameSize = 1000;
                    boolean expectMaskedFrames = true;
                    boolean allowExtensions = true;
                    boolean maskPayload = false;
                    ChannelHandler webSocketDecoder, webSocketEncoder;
        
                    webSocketDecoder = new WebSocket00FrameDecoder(defaultMaxFrameSize);
                    webSocketEncoder = new WebSocket00FrameEncoder();
        
                    try
                    {
                        HTTPHeader clientWebSocketVersionHeader = HeaderUtil.extractHeader(message,
                                                                                           ClientHeaderPatterns.WEB_SOCKET_VERSION);
            
                        int version = Integer.parseInt(clientWebSocketVersionHeader.getValue());
                        System.out.println("Version: " + version);
                        switch(version)
                        {
                            case 7:
                                webSocketDecoder = new WebSocket07FrameDecoder(expectMaskedFrames,
                                                                               allowExtensions,
                                                                               defaultMaxFrameSize);
                                webSocketEncoder = new WebSocket07FrameEncoder(maskPayload);
                            case 8:
                                webSocketDecoder = new WebSocket08FrameDecoder(expectMaskedFrames,
                                                                               allowExtensions,
                                                                               defaultMaxFrameSize);
                                webSocketEncoder = new WebSocket08FrameEncoder(maskPayload);
                            case 13:
                                webSocketDecoder = new WebSocket13FrameDecoder(expectMaskedFrames,
                                                                               allowExtensions,
                                                                               defaultMaxFrameSize);
                                webSocketEncoder = new WebSocket13FrameEncoder(maskPayload);
                        }
            
                    }
                    catch(NoSuchHeaderException | NumberFormatException ignored)
                    {
            
                    }
        
        
                    //Create response Key
                    String webSocketUUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
                    byte[] sha1Key = WebSocketUtil.sha1((clientKey.trim() + webSocketUUID).getBytes());
                    String serverKey = Base64.getEncoder().encodeToString(sha1Key);
    
    
                    //Edit Pipeline
                    ChannelPipeline pipeline = sessionContext.getChannel().pipeline();
                    pipeline.remove("sessionDecoder");
                    pipeline.remove("framer");
                    pipeline.remove("handler");
    
                    //Note: When changing the order in how pipeline is add be aware of the SSL Handler for secure pipes
                    pipeline.addLast("websocketDecoder", webSocketDecoder);
                    pipeline.addLast("websocketEncoder", webSocketEncoder);
                    pipeline.addLast("webSocketHandler", new WebSocketHandler());
                    //Create Handshake Response
                    HTTPResponse response = new HTTPResponse();
                    response.setMessage101();
                    response.setKeepConnectionAlive(true);
    
                    ArrayList<HTTPHeader> headers = new ArrayList<HTTPHeader>();
                    headers.add(HeaderUtil.upgradeHeader.setValue("websocket"));
                    headers.add(HeaderUtil.secWebSocketAcceptHeader.setValue(serverKey));
                    response.addHeaders(headers);
                    return response;
                }
    
            }
            catch(NoSuchHeaderException ignored)
            {
            
            }
    
    
            //We then need to analyze the file they are requesting and change it if need be
            String filePath = message.getPath();
            if(filePath.equals("/"))
            {
                filePath = "/home.html";
            }
    
    
            /* Search Order
             * First we search the Domain Folder and return that if it find the file that was request
             * if not then we search the Listed GET Dirs and return that if we find the request file.
             * */
            File fileToServe = null;
    
    
            /*Domain Specific Search*/
            Domain messageHost = message.getDomain();
            if(messageHost != null)
            {
                if(messageHost.getGetDir() != null)
                {
                    fileToServe = ServerUtil.safeSearchDir(messageHost.getGetDir(), filePath, false);
                }
            }
    
            /*Search all the Get Dirs for the request file if it's null*/
            if(fileToServe == null)
            {
                for(String getDir : OptionUtil.getGETDirPaths())
                {
                    fileToServe = ServerUtil.safeSearchDir(new File(getDir), filePath, false);
                    if(fileToServe != null)
                    {
                        break;
                    }
                }
            }
    
    
            //If we Don't have the file they are looking for we return a 404 message with the 404 page
            if(fileToServe == null)
            {
                HTTPResponse response = new HTTPResponse();
                response.setMessage404();
                return response;
            }
    
    
            ArrayList<HTTPHeader> headers = new ArrayList<HTTPHeader>();
            String type = GeneralUtil.getType(fileToServe);
            
            byte[] fileBytes = ServerUtil.scanFile(fileToServe);
            headers.add(HeaderUtil.contentTypeHeader.setValue(ContentTypeUtil.getContentType(type)));
            HTTPResponse response = new HTTPResponse();
            response.setMessage200();
            response.addHeaders(headers);
            response.setBytesOut(fileBytes);
            return response;
        }
        else if(message.getMethod().equals(Method.POST))
        {
            /* Process for dealing with POST requests
             * Check for server's settings for post
             *  - Check Allow Post
             *  - Check Server's Email Settings
             * Verify Post requests integrity
             * Check with watch list to see if we should accept the request
             * Generate Auth Session from Post Request
             * Filter Auth Session failure Codes for shortcut responses
             * Filter AuthSessions and Auth tries via success and IP (Make it so you can't brute force)
             * Note: Filter Should allow following post commands to assume a user from auth session
             * Get the command to execute
             * Filter for the Post Requests Command
             * Filter by Command and AuthSession
             *  - Return 403 for fail Auths on commands that need auth
             *  - Return 403 for token Auths on commands that need password auth
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
    
            //Check Server's Email Settings
            Email defaultEmail;
            try
            {
                defaultEmail = EmailManager.getEmail(OptionUtil.getDefaultEmail());
            }
            catch(NoSuchEmailException e)
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
    
            //Check with watch list to see if we should accept the request
            if(!WatchListUtil.shouldAcceptRequest(message.getIp()))
            {
                HTTPResponse response = new HTTPResponse();
                response.setMessage403();
                return response;
            }
    
            //Generate Auth Session from Post Request
            AuthSession authSession = AuthUtil.authenticateUser(request);
            authSession.setDomain(message.getDomain());
    
    
            if(!authSession.isSuccess())
            {
    
                //Filter Auth Session failure Codes for shortcut responses
                //Note: Filter Should allow following post commands to assume a user from auth session
                if(authSession.getFailureCode().equals(FailureReasons.NO_DATABASE))
                {
                    HTTPResponse response = new HTTPResponse();
                    response.setMessage501();
                    return response;
                }
    
    
                //Filter AuthSessions and Auth tries via success and IP (Make it so you can't easily brute force)
                if(authSession.getFailureCode().equals(FailureReasons.INCORRECT_PASSWORD) ||
                   authSession.getFailureCode().equals(FailureReasons.INCORRECT_TOKEN))
                {
    
                    WatchListUtil.strikeUser(message.getIp());
                    HTTPResponse response = new HTTPResponse();
                    response.setMessage403();
                    return response;
    
                }
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
            commandToExecute.setServersEmail(defaultEmail);
            
            //Note: Filter Should allow following post commands to assume a user from auth session
            //If the post command requires a user then shortcut the response to 400
            if(authSession.getUser() == null && commandToExecute.requiresUser())
            {
                HTTPResponse response = new HTTPResponse();
                response.setMessage400();
                return response;
            }
    
            /*Filter by Command and AuthSession
             *
             * - Return 403 for failed Auths on commands that need auth
             * - Return 403 for token Auths on commands that need password auth
             */
            
            //- Return 403 for fail Auths on commands that need auth
            if(!authSession.isSuccess() && commandToExecute.requiresSuccessfulAuth())
            {
                HTTPResponse response = new HTTPResponse();
                response.setMessage403();
                return response;
            }
            
            //- Return 403 for token Auths on commands that need password auth
            if(!authSession.isPasswordAuth() && commandToExecute.requiresPasswordAuth())
            {
                HTTPResponse response = new HTTPResponse();
                response.setMessage403();
                return response;
            }
            
            return commandToExecute.getResponse(request, authSession);
        }
        
        HTTPResponse response = new HTTPResponse();
        response.setMessage501();
        return response;
    }
    
    
}
