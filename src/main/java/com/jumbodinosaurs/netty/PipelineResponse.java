package com.jumbodinosaurs.netty;

public class PipelineResponse
{
    private String message;
    private byte[] bytesOut;
    public PipelineResponse(String message, byte[] bytesOut)
    {
        this.message = message;
        this.bytesOut = bytesOut;
    }

    public byte[] getBytesOut()
    {
        return this.bytesOut;
    }

    public String getMessage()
    {
        return this.message;
    }
}
