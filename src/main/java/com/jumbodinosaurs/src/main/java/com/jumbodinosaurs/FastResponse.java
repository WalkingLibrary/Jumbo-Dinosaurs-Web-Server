package com.jumbodinosaurs;

public class FastResponse
{
    private String message;
    private byte[] photobytes;
    public FastResponse(String message, byte[] photoBytes)
    {
        this.message = message;
        this.photobytes = photoBytes;
    }

    public byte[] getPhotobytes()
    {
        return this.photobytes;
    }

    public String getMessage()
    {
        return this.message;
    }
}
