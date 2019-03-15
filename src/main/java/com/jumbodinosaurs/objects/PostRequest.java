package com.jumbodinosaurs.objects;

public class PostRequest
{
    private String username;
    private String password;
    private String email;
    private String token;
    private String path;
    private String command;
    private String content;
    private String captchaCode;
    private String emailCode;
    
    
    public PostRequest(String username, String password, String email, String token, String path, String command, String content, String captchaCode, String emailCode)
    {
        this.username = username;
        this.password = password;
        this.email = email;
        this.token = token;
        this.path = path;
        this.command = command;
        this.content = content;
        this.captchaCode = captchaCode;
        this.emailCode = emailCode;
    }
    
   
    
    public PostRequest(String email, String command)
    {
        this.email = email;
        this.command = command;
    }
    
    
    public String getEmail()
    {
        return email;
    }
    
    public String getCaptchaCode()
    {
        return captchaCode;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public String getPath()
    {
        return path;
    }
    
    public String getCommand()
    {
        return command;
    }
    
    public String getContent()
    {
        return content;
    }
    
    
    public String getEmailCode()
    {
        return emailCode;
    }
    
    @Override
    public String toString()
    {
        return "PostRequest{" + "username='" + username + '\'' + ", password='" + password + '\'' + ", email='" + email + '\'' + ", token='" + token + '\'' + ", path='" + path + '\'' + ", command='" + command + '\'' + ", content='" + content + '\'' + ", captchaCode='" + captchaCode + '\'' + ", emailCode='" + emailCode + '\'' + '}';
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
}
