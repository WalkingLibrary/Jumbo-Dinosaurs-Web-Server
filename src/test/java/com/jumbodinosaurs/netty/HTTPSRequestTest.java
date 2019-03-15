package com.jumbodinosaurs.netty;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jumbodinosaurs.objects.FloatUser;
import com.jumbodinosaurs.objects.MinecraftSign;
import com.jumbodinosaurs.objects.MinecraftWrittenBook;
import com.jumbodinosaurs.util.CredentialsManager;
import com.jumbodinosaurs.util.DataController;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class HTTPSRequestTest
{
    private HTTPSRequest request;
    private ArrayList<HTTPSRequest> requests;
    private final String sC400 = "HTTP/1.1 400 Bad";
    private final String sC501 = "HTTP/1.1 501 Not Implemented";
    private final String closeHeader = " \r\nConnection: Close\r\n\r\n";
    private String message400, message501, dumyIP, GET, POST, HTTP;
    
    @Before
    public void setUp() throws Exception
    {
        this.message400 = this.sC400 + this.closeHeader;
        this.message501 = this.sC501 + this.closeHeader;
        this.GET = "GET /";
        this.POST = "POST /";
        this.HTTP = " HTTP/1.1";
        this.request = new HTTPSRequest(GET + POST, "/199.0.102.12");
        DataController controller = new DataController(false);
    }
    
    @Ignore("Testing Real HTTP Requests")
    @Test
    public void isNotHTTP() throws Exception
    {
        assertEquals(false, request.isHTTP());
    }
    
    @Ignore("Testing Real HTTP Requests")
    @Test
    public void test501Code() throws Exception
    {
        request.generateMessage();
        assertEquals(this.message501, this.request.getMessageToSend());
    }
    
    
    @Test
    public void testFloatUserLoginLimits() throws Exception
    {
        DataController controller = new DataController(false);
        JsonObject dumyRequest = new JsonObject();
        dumyRequest.addProperty("username", "johhnny");
        dumyRequest.addProperty("command", "getToken");
        dumyRequest.addProperty("password", "eddied1001");
        dumyIP = "/200.200.200.200";
        requests = new ArrayList<HTTPSRequest>();
        String dumyRequestMessage = POST + dumyRequest.toString() + HTTP;
        
        for(int i = 0; i < 20; i++)
        {
            requests.add(new HTTPSRequest(dumyRequestMessage, dumyIP));
        }
        for(HTTPSRequest reg: requests)
        {
            reg.generateMessage();
        }
        assertTrue(CredentialsManager.isIPCaptchaLocked(dumyIP));
    }
    
    
    @Test
    public void testFloatUserEmailLimits() throws Exception
    {
        DataController controller = new DataController(false);
        JsonObject dumyRequest = new JsonObject();
        dumyRequest.addProperty("email", "nerf.warren@gmail.com");
        dumyRequest.addProperty("command", "emailCheck");
        dumyIP = "/400.400.400.400";
        requests = new ArrayList<HTTPSRequest>();
        
        String dumyRequestMessage = POST + dumyRequest.toString() + HTTP;
        
        for(int i = 0; i < 20; i++)
        {
            requests.add(new HTTPSRequest(dumyRequestMessage, dumyIP));
        }
        
        
        for(HTTPSRequest reg: requests)
        {
            reg.generateMessage();
        }
        assertTrue(CredentialsManager.isIPEmailCheckLocked(dumyIP));
    }
    
    @Test
    public void testIPChange()
    {
        CredentialsManager.setWatchList(new ArrayList<FloatUser>());
        
        //Get Token
        JsonObject getTokenDumyRequest = new JsonObject();
        getTokenDumyRequest.addProperty("username", "jums");
        getTokenDumyRequest.addProperty("password", "jeffjeffr");
        getTokenDumyRequest.addProperty("command", "getToken");
        
        String getTokenRequestMessage = POST + getTokenDumyRequest.toString() + HTTP;
        
        HTTPSRequest request = new HTTPSRequest(getTokenRequestMessage, "/700.700.700.700");
        
        request.generateMessage();
        
        String token = request.getMessageToSend().substring(request.getMessageToSend().indexOf("\r\n\r\n") + 4);
        
        
        assertTrue(!token.isEmpty());
        System.out.println(token);
        
        //Get Username to make sure token is good
        JsonObject getUsernameDumyRequest = new JsonObject();
        getUsernameDumyRequest.addProperty("token", token);
        getUsernameDumyRequest.addProperty("command", "getUsername");
        
        System.out.println(getUsernameDumyRequest.toString());
        String getUsername = POST + getUsernameDumyRequest.toString() + HTTP;
        
        HTTPSRequest usernameRequest = new HTTPSRequest(getUsername, "/700.700.700.700");
        
        usernameRequest.generateMessage();
        
        assertTrue(!CredentialsManager.isIPCaptchaLocked("/700.700.700.700"));
        assertTrue(usernameRequest.isHTTP());
        System.out.println(usernameRequest.getMessageToSend());
        assertTrue(usernameRequest.messageSentContained200Code());
        
        
        
        
        //Try different ip with token
        HTTPSRequest usernameRequestDifferentIP = new HTTPSRequest(getUsername, "/700.700.700.000");
        
        usernameRequestDifferentIP.generateMessage();
        
        assertTrue(!usernameRequestDifferentIP.messageSentContained200Code());
    }
    
    @Ignore("To Long")
    @Test
    public void SpamSize()
    {
        int i = 0;
        while(i < 10000)
        {
            i++;
            JsonObject getTokenDumyRequest = new JsonObject();
            getTokenDumyRequest.addProperty("username", "jums");
            getTokenDumyRequest.addProperty("password", "jeffjeffr");
            getTokenDumyRequest.addProperty("command", "postBook");
    
            MinecraftWrittenBook book = new MinecraftWrittenBook("jungalo", "hello world", "0", "2", new ArrayList<String>());
            getTokenDumyRequest.addProperty("content", new Gson().toJson(book));
    
            String getTokenRequestMessage = POST + getTokenDumyRequest.toString() + HTTP;
    
            HTTPSRequest request = new HTTPSRequest(getTokenRequestMessage, "/700.700.700.700");
    
            request.generateMessage();
    
    
            assertTrue(request.messageSentContained200Code());
        }
    }
    
    
    @Test
    public void testEmailCheck()
    {
        String dumyEmail = "gafgaafafwf";
        String dumyEmail2 = "!{#}${@$%{!}%$!{!#%}%{#%!#%##";
        String dumyEmail3 = "fafjinasfjas@gmail.com";
        
        assertTrue(!HTTPSRequest.lookUpEmail(dumyEmail));
        assertTrue(!HTTPSRequest.lookUpEmail(dumyEmail2));
        assertTrue(HTTPSRequest.lookUpEmail(dumyEmail3));
    }
    
    @Test
    public void testBookPost() throws Exception
    {
        JsonObject getTokenDumyRequest = new JsonObject();
        getTokenDumyRequest.addProperty("username", "jungalo");
        getTokenDumyRequest.addProperty("password", "killerkiller");
        getTokenDumyRequest.addProperty("command", "postBook");
        
        MinecraftWrittenBook book = new MinecraftWrittenBook("jungalo", "hello world", "0", "2", new ArrayList<String>());
        getTokenDumyRequest.addProperty("content", new Gson().toJson(book) + "@!#FD@QEF@#F@F}{");
        
        String getTokenRequestMessage = POST + getTokenDumyRequest.toString() + HTTP;
        
        HTTPSRequest request = new HTTPSRequest(getTokenRequestMessage, "/700.700.700.700");
        
        request.generateMessage();
        
        
        
        assertFalse(request.messageSentContained200Code());
    }
    
    
    @Test
    public void testSignPost()
    {
        JsonObject getTokenDumyRequest = new JsonObject();
        getTokenDumyRequest.addProperty("username", "jungalo");
        getTokenDumyRequest.addProperty("password", "killerkiller");
        getTokenDumyRequest.addProperty("command", "postSign");
        MinecraftSign sign = new MinecraftSign(5,4,3,"","","","","", 0);
        
        getTokenDumyRequest.addProperty("content", new Gson().toJson(sign) + "@!#FD@QEF@#F@F}{");
        
        String getTokenRequestMessage = POST + getTokenDumyRequest.toString() + HTTP;
        
        HTTPSRequest request = new HTTPSRequest(getTokenRequestMessage, "/700.700.700.700");
        
        request.generateMessage();
        
        
        assertFalse(request.messageSentContained200Code());
        
    }
    
}