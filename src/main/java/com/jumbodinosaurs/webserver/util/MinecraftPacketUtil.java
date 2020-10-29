package com.jumbodinosaurs.webserver.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MinecraftPacketUtil
{

    
    
    public static void writeVarInt(DataOutputStream output, int input) throws IOException
    {
        while((input & -128) != 0)
        {
            output.writeByte(input & 127 | 128);
            input >>>= 7;
        }
        output.writeByte(input);
    }
    
    
    public static void writeString(DataOutputStream output, String message)throws IOException
    {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        writeVarInt(output, messageBytes.length);
        output.write(messageBytes);
     
    }
    
    
    public static void writeByteArray(DataOutputStream output, byte[] bytes) throws IOException
    {
        output.writeByte(bytes.length);
        output.write(bytes);
    }
   
}
