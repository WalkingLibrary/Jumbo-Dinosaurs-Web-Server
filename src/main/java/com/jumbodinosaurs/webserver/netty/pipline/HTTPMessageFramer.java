package com.jumbodinosaurs.webserver.netty.pipline;

import com.jumbodinosaurs.devlib.util.OperatorConsole;
import com.jumbodinosaurs.webserver.util.OptionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;

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
    
        if(OptionUtil.isInDebugMode())
        {
            System.out.println(OperatorConsole.ANSI_CYAN +
                               "Framer Message Received:" +
                               OperatorConsole.ANSI_RESET +
                               "\n" +
                               totalMessage);
        }
    
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
        
            /*Parsing the Length from the length header*/
            int postContentLength;
        
            try
            {
                int contentLengthHeaderEndIndex = messageHeaders.indexOf(contentLengthHeader);
    
    
                String lengthFromLengthHeader = messageHeaders.substring(contentLengthHeaderEndIndex +
                                                                         contentLengthHeader.length());
                Scanner lengthFinder = new Scanner(lengthFromLengthHeader);
    
                try
                {
                    postContentLength = lengthFinder.nextInt();
                }
                catch(Exception e)
                {
                    closeConnection(context);
                    return;
                }
            }
            catch(IndexOutOfBoundsException e)
            {
                closeConnection(context);
                return;
            }
        
        
            try
            {
    
                if(totalMessage.substring(headersEndIndex + headersEnd.length()).getBytes().length == postContentLength)
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
