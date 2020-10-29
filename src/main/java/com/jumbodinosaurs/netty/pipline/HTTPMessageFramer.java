package com.jumbodinosaurs.netty.pipline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class HTTPMessageFramer extends StringDecoder
{
    private String totalMessage = "";
    
    private static void closeConnection(ChannelHandlerContext context)
    {
        //Write the buffer to the pipeline
        ChannelFuture promise = context.close();
        //Add Futures to close the connection when the bytes are sent
        promise.addListener(new CompletionFuture());
        promise.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
    
    
    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> out)
            throws Exception
    {
        
        totalMessage += input.toString(Charset.defaultCharset());
        
        String headersEnd = "\r\n\r\n";
        if(totalMessage.contains(headersEnd))
        {
            int headersEndIndex = totalMessage.indexOf(headersEnd);
            String contentLengthHeader = "Content-Length:";
            
            String messageHeaders = totalMessage.substring(0, headersEndIndex);
            
            if(!messageHeaders.contains(contentLengthHeader))
            {
                out.add(totalMessage);
                return;
            }
            
            int contentLengthHeaderEndIndex = messageHeaders.indexOf(contentLengthHeader);
            
            
            String postContentHeaderMessage = messageHeaders.substring(contentLengthHeaderEndIndex +
                                                                       contentLengthHeader.length());
            
            
            String length = postContentHeaderMessage.substring(0, postContentHeaderMessage.indexOf("\n"));
            length = length.trim();
            
            int lengthNumber;
            try
            {
                lengthNumber = Integer.parseInt(length);
            }
            catch(NumberFormatException e)
            {
                closeConnection(context);
                return;
            }
            
            try
            {
                
                if(totalMessage.substring(headersEndIndex + headersEnd.length()).getBytes().length == lengthNumber)
                {
                    out.add(totalMessage);
                    
                }
            }
            catch(StringIndexOutOfBoundsException e)
            {
                closeConnection(context);
                return;
            }
            
            
        }
        
    }
    
}
