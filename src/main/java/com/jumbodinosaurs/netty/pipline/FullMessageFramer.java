package com.jumbodinosaurs.netty.pipline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FullMessageFramer extends ByteToMessageDecoder
{
    private boolean awaitingReadComplete = false;
    private boolean readComplete = false;
    private ByteBuf tempIn;
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception
    {
        super.channelReadComplete(ctx);
        /* If the readable bytes was the same size of the ByteBuffer but the read is complete we need to fire
         *  a Channel read sending the in Buffer down the pipeline */
        if(awaitingReadComplete)
        {
            ctx.fireChannelRead(tempIn);
        }
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception
    {
        
        //Currently 1MB
        int maxAmountOfBytes = 100000;
        
        /* Process for Framing a HTTP Message
         *
         * Check the amount of readable bytes and ensure it's under maxAmountOfBytes
         *
         * Check that the amount of readable bytes is the same as the ByteBuffer IN and set the flag
         * to Send the copy of IN down the pipeline after a completed read
         *
         * Add IN to OUT if There are less bytes to read than the size of IN
         * */
        
        /* When reading a message the default size of the ByteBuf in is 1024 bytes
         * This creates a problem that when decoded by A String Decoder It will chop off some of the message
         * To ensure all readable bytes are passed down the pipeline
         *  We wait until the read is complete and fire a channel read with a copy of IN
         * If IN's capacity is less than the amount of readable bytes
         * If the actualReadableBytes's size is greater than IN than we allow netty to call the decode method again
         * We also make sure that actualReadableBytes is not over maxAmountOfBytes
         */
       
       
      /*
        System.out.println("Read");
        System.out.println("Readable Bytes: " + in.readableBytes());
        System.out.println("Capacity: "  + in.capacity());
        System.out.println("Actual Readable Bytes: " + actualReadableBytes());
       */
        
        //Check the amount of readable bytes and ensure it's under maxAmountOfBytes
        //This will hopefully stop someone from building the size of IN to a ridicules amount
        if(actualReadableBytes() > maxAmountOfBytes)
        {
            ctx.close();
            return;
        }
        
        
        // There is a chance of the actualReadableBytes returning a value greater than IN.capacity()
        // if we do nothing to OUT or IN Netty will call the decode method again
        if(actualReadableBytes() > in.capacity())
        {
            return;
        }
        
        
        // Check that the amount of readable bytes is the same as the ByteBuffer IN and set the flag
        // to Send the copy of IN down the pipeline after a completed read
        if(actualReadableBytes() == in.capacity())
        {
            awaitingReadComplete = true;
            tempIn = in;
            return;
        }
        
        
        //Add IN to OUT if There are less bytes to read than the size of IN
        //We also need to set the awaiting read complete flag to false
        awaitingReadComplete = false;
        out.add(in);
        
        
    }
    
}
