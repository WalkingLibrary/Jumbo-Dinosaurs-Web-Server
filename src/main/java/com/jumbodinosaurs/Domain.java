package com.jumbodinosaurs;

public class Domain
{
    private String domain;
    private String username;
    private String password;

    public Domain(String domain, String username, String password)
    {
        this.domain = domain;
        this.username = username;
        this.password = password;
    }

    public String getDomain()
    {
        return domain;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }
}
