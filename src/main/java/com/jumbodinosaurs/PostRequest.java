package com.jumbodinosaurs;

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

    public PostRequest(String username, String password, String email, String token, String path, String command, String information, String captchaCode)
    {
        this.username = username;
        this.password = password;
        this.email = email;
        this.token = token;
        this.path = path;
        this.command = command;
        this.content = information;
        this.captchaCode = captchaCode;
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
}
