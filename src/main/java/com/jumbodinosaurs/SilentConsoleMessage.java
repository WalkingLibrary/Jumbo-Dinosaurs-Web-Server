package com.jumbodinosaurs;

public class SilentConsoleMessage
{
    private String message;
    private String dateTime;

    public SilentConsoleMessage(String message, String dateTime)
    {
        this.message = message;
        this.dateTime = dateTime;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }
}
