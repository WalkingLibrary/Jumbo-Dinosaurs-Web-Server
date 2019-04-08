package com.jumbodinosaurs.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public class ByteBuffer extends ByteBuf
{
    
    private ByteBuf buffer;
    
    public ByteBuffer(ByteBuf buffer)
    {
        this.buffer = buffer;
    }
    
    
    public int capacity() {
        return this.buffer.capacity();
    }
    
    @Override
    public ByteBuf capacity(int p_capacity_1_) {
        return this.buffer.capacity(p_capacity_1_);
    }
    
    public int maxCapacity() {
        return this.buffer.maxCapacity();
    }
    
    public ByteBufAllocator alloc() {
        return this.buffer.alloc();
    }
    
    public ByteOrder order() {
        return this.buffer.order();
    }
    
    public ByteBuf order(ByteOrder p_order_1_) {
        return this.buffer.order(p_order_1_);
    }
    
    public ByteBuf unwrap() {
        return this.buffer.unwrap();
    }
    
    public boolean isDirect() {
        return this.buffer.isDirect();
    }
    
    public boolean isReadOnly() {
        return this.buffer.isReadOnly();
    }
    
    public ByteBuf asReadOnly() {
        return this.buffer.asReadOnly();
    }
    
    public int readerIndex() {
        return this.buffer.readerIndex();
    }
    
    public ByteBuf readerIndex(int p_readerIndex_1_) {
        return this.buffer.readerIndex(p_readerIndex_1_);
    }
    
    public int writerIndex() {
        return this.buffer.writerIndex();
    }
    
    public ByteBuf writerIndex(int p_writerIndex_1_) {
        return this.buffer.writerIndex(p_writerIndex_1_);
    }
    
    public ByteBuf setIndex(int p_setIndex_1_, int p_setIndex_2_) {
        return this.buffer.setIndex(p_setIndex_1_, p_setIndex_2_);
    }
    
    public int readableBytes() {
        return this.buffer.readableBytes();
    }
    
    public int writableBytes() {
        return this.buffer.writableBytes();
    }
    
    public int maxWritableBytes() {
        return this.buffer.maxWritableBytes();
    }
    
    public boolean isReadable() {
        return this.buffer.isReadable();
    }
    
    public boolean isReadable(int p_isReadable_1_) {
        return this.buffer.isReadable(p_isReadable_1_);
    }
    
    public boolean isWritable() {
        return this.buffer.isWritable();
    }
    
    public boolean isWritable(int p_isWritable_1_) {
        return this.buffer.isWritable(p_isWritable_1_);
    }
    
    public ByteBuf clear() {
        return this.buffer.clear();
    }
    
    public ByteBuf markReaderIndex() {
        return this.buffer.markReaderIndex();
    }
    
    public ByteBuf resetReaderIndex() {
        return this.buffer.resetReaderIndex();
    }
    
    public ByteBuf markWriterIndex() {
        return this.buffer.markWriterIndex();
    }
    
    public ByteBuf resetWriterIndex() {
        return this.buffer.resetWriterIndex();
    }
    
    public ByteBuf discardReadBytes() {
        return this.buffer.discardReadBytes();
    }
    
    public ByteBuf discardSomeReadBytes() {
        return this.buffer.discardSomeReadBytes();
    }
    
    public ByteBuf ensureWritable(int p_ensureWritable_1_) {
        return this.buffer.ensureWritable(p_ensureWritable_1_);
    }
    
    public int ensureWritable(int p_ensureWritable_1_, boolean p_ensureWritable_2_) {
        return this.buffer.ensureWritable(p_ensureWritable_1_, p_ensureWritable_2_);
    }
    
    public boolean getBoolean(int p_getBoolean_1_) {
        return this.buffer.getBoolean(p_getBoolean_1_);
    }
    
    public byte getByte(int p_getByte_1_) {
        return this.buffer.getByte(p_getByte_1_);
    }
    
    public short getUnsignedByte(int p_getUnsignedByte_1_) {
        return this.buffer.getUnsignedByte(p_getUnsignedByte_1_);
    }
    
    public short getShort(int p_getShort_1_) {
        return this.buffer.getShort(p_getShort_1_);
    }
    
    public short getShortLE(int p_getShortLE_1_) {
        return this.buffer.getShortLE(p_getShortLE_1_);
    }
    
    public int getUnsignedShort(int p_getUnsignedShort_1_) {
        return this.buffer.getUnsignedShort(p_getUnsignedShort_1_);
    }
    
    public int getUnsignedShortLE(int p_getUnsignedShortLE_1_) {
        return this.buffer.getUnsignedShortLE(p_getUnsignedShortLE_1_);
    }
    
    public int getMedium(int p_getMedium_1_) {
        return this.buffer.getMedium(p_getMedium_1_);
    }
    
    public int getMediumLE(int p_getMediumLE_1_) {
        return this.buffer.getMediumLE(p_getMediumLE_1_);
    }
    
    public int getUnsignedMedium(int p_getUnsignedMedium_1_) {
        return this.buffer.getUnsignedMedium(p_getUnsignedMedium_1_);
    }
    
    public int getUnsignedMediumLE(int p_getUnsignedMediumLE_1_) {
        return this.buffer.getUnsignedMediumLE(p_getUnsignedMediumLE_1_);
    }
    
    public int getInt(int p_getInt_1_) {
        return this.buffer.getInt(p_getInt_1_);
    }
    
    public int getIntLE(int p_getIntLE_1_) {
        return this.buffer.getIntLE(p_getIntLE_1_);
    }
    
    public long getUnsignedInt(int p_getUnsignedInt_1_) {
        return this.buffer.getUnsignedInt(p_getUnsignedInt_1_);
    }
    
    public long getUnsignedIntLE(int p_getUnsignedIntLE_1_) {
        return this.buffer.getUnsignedIntLE(p_getUnsignedIntLE_1_);
    }
    
    public long getLong(int p_getLong_1_) {
        return this.buffer.getLong(p_getLong_1_);
    }
    
    public long getLongLE(int p_getLongLE_1_) {
        return this.buffer.getLongLE(p_getLongLE_1_);
    }
    
    public char getChar(int p_getChar_1_) {
        return this.buffer.getChar(p_getChar_1_);
    }
    
    public float getFloat(int p_getFloat_1_) {
        return this.buffer.getFloat(p_getFloat_1_);
    }
    
    public double getDouble(int p_getDouble_1_) {
        return this.buffer.getDouble(p_getDouble_1_);
    }
    
    public ByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_) {
        return this.buffer.getBytes(p_getBytes_1_, p_getBytes_2_);
    }
    
    public ByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_) {
        return this.buffer.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
    }
    
    public ByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_) {
        return this.buffer.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_4_);
    }
    
    public ByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_) {
        return this.buffer.getBytes(p_getBytes_1_, p_getBytes_2_);
    }
    
    public ByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_) {
        return this.buffer.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_4_);
    }
    
    public ByteBuf getBytes(int p_getBytes_1_, java.nio.ByteBuffer p_getBytes_2_) {
        return this.buffer.getBytes(p_getBytes_1_, p_getBytes_2_);
    }
    
    public ByteBuf getBytes(int p_getBytes_1_, OutputStream p_getBytes_2_, int p_getBytes_3_) throws IOException
    {
        return this.buffer.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
    }
    
    public int getBytes(int p_getBytes_1_, GatheringByteChannel p_getBytes_2_, int p_getBytes_3_) throws IOException {
        return this.buffer.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
    }
    
    public int getBytes(int p_getBytes_1_, FileChannel p_getBytes_2_, long p_getBytes_3_, int p_getBytes_5_) throws IOException {
        return this.buffer.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_5_);
    }
    
    public CharSequence getCharSequence(int p_getCharSequence_1_, int p_getCharSequence_2_, Charset p_getCharSequence_3_) {
        return this.buffer.getCharSequence(p_getCharSequence_1_, p_getCharSequence_2_, p_getCharSequence_3_);
    }
    
    public ByteBuf setBoolean(int p_setBoolean_1_, boolean p_setBoolean_2_) {
        return this.buffer.setBoolean(p_setBoolean_1_, p_setBoolean_2_);
    }
    
    public ByteBuf setByte(int p_setByte_1_, int p_setByte_2_) {
        return this.buffer.setByte(p_setByte_1_, p_setByte_2_);
    }
    
    public ByteBuf setShort(int p_setShort_1_, int p_setShort_2_) {
        return this.buffer.setShort(p_setShort_1_, p_setShort_2_);
    }
    
    public ByteBuf setShortLE(int p_setShortLE_1_, int p_setShortLE_2_) {
        return this.buffer.setShortLE(p_setShortLE_1_, p_setShortLE_2_);
    }
    
    public ByteBuf setMedium(int p_setMedium_1_, int p_setMedium_2_) {
        return this.buffer.setMedium(p_setMedium_1_, p_setMedium_2_);
    }
    
    public ByteBuf setMediumLE(int p_setMediumLE_1_, int p_setMediumLE_2_) {
        return this.buffer.setMediumLE(p_setMediumLE_1_, p_setMediumLE_2_);
    }
    
    public ByteBuf setInt(int p_setInt_1_, int p_setInt_2_) {
        return this.buffer.setInt(p_setInt_1_, p_setInt_2_);
    }
    
    public ByteBuf setIntLE(int p_setIntLE_1_, int p_setIntLE_2_) {
        return this.buffer.setIntLE(p_setIntLE_1_, p_setIntLE_2_);
    }
    
    public ByteBuf setLong(int p_setLong_1_, long p_setLong_2_) {
        return this.buffer.setLong(p_setLong_1_, p_setLong_2_);
    }
    
    public ByteBuf setLongLE(int p_setLongLE_1_, long p_setLongLE_2_) {
        return this.buffer.setLongLE(p_setLongLE_1_, p_setLongLE_2_);
    }
    
    public ByteBuf setChar(int p_setChar_1_, int p_setChar_2_) {
        return this.buffer.setChar(p_setChar_1_, p_setChar_2_);
    }
    
    public ByteBuf setFloat(int p_setFloat_1_, float p_setFloat_2_) {
        return this.buffer.setFloat(p_setFloat_1_, p_setFloat_2_);
    }
    
    public ByteBuf setDouble(int p_setDouble_1_, double p_setDouble_2_) {
        return this.buffer.setDouble(p_setDouble_1_, p_setDouble_2_);
    }
    
    public ByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_) {
        return this.buffer.setBytes(p_setBytes_1_, p_setBytes_2_);
    }
    
    public ByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_) {
        return this.buffer.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }
    
    public ByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_) {
        return this.buffer.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_4_);
    }
    
    public ByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_) {
        return this.buffer.setBytes(p_setBytes_1_, p_setBytes_2_);
    }
    
    public ByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_) {
        return this.buffer.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_4_);
    }
    
    public ByteBuf setBytes(int p_setBytes_1_, java.nio.ByteBuffer p_setBytes_2_) {
        return this.buffer.setBytes(p_setBytes_1_, p_setBytes_2_);
    }
    
    public int setBytes(int p_setBytes_1_, InputStream p_setBytes_2_, int p_setBytes_3_) throws IOException {
        return this.buffer.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }
    
    public int setBytes(int p_setBytes_1_, ScatteringByteChannel p_setBytes_2_, int p_setBytes_3_) throws IOException {
        return this.buffer.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }
    
    public int setBytes(int p_setBytes_1_, FileChannel p_setBytes_2_, long p_setBytes_3_, int p_setBytes_5_) throws IOException {
        return this.buffer.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_5_);
    }
    
    public ByteBuf setZero(int p_setZero_1_, int p_setZero_2_) {
        return this.buffer.setZero(p_setZero_1_, p_setZero_2_);
    }
    
    public int setCharSequence(int p_setCharSequence_1_, CharSequence p_setCharSequence_2_, Charset p_setCharSequence_3_) {
        return this.buffer.setCharSequence(p_setCharSequence_1_, p_setCharSequence_2_, p_setCharSequence_3_);
    }
    
    public boolean readBoolean() {
        return this.buffer.readBoolean();
    }
    
    public byte readByte() {
        return this.buffer.readByte();
    }
    
    public short readUnsignedByte() {
        return this.buffer.readUnsignedByte();
    }
    
    public short readShort() {
        return this.buffer.readShort();
    }
    
    public short readShortLE() {
        return this.buffer.readShortLE();
    }
    
    public int readUnsignedShort() {
        return this.buffer.readUnsignedShort();
    }
    
    public int readUnsignedShortLE() {
        return this.buffer.readUnsignedShortLE();
    }
    
    public int readMedium() {
        return this.buffer.readMedium();
    }
    
    public int readMediumLE() {
        return this.buffer.readMediumLE();
    }
    
    public int readUnsignedMedium() {
        return this.buffer.readUnsignedMedium();
    }
    
    public int readUnsignedMediumLE() {
        return this.buffer.readUnsignedMediumLE();
    }
    
    public int readInt() {
        return this.buffer.readInt();
    }
    
    public int readIntLE() {
        return this.buffer.readIntLE();
    }
    
    public long readUnsignedInt() {
        return this.buffer.readUnsignedInt();
    }
    
    public long readUnsignedIntLE() {
        return this.buffer.readUnsignedIntLE();
    }
    
    public long readLong() {
        return this.buffer.readLong();
    }
    
    public long readLongLE() {
        return this.buffer.readLongLE();
    }
    
    public char readChar() {
        return this.buffer.readChar();
    }
    
    public float readFloat() {
        return this.buffer.readFloat();
    }
    
    public double readDouble() {
        return this.buffer.readDouble();
    }
    
    public ByteBuf readBytes(int p_readBytes_1_) {
        return this.buffer.readBytes(p_readBytes_1_);
    }
    
    public ByteBuf readSlice(int p_readSlice_1_) {
        return this.buffer.readSlice(p_readSlice_1_);
    }
    
    public ByteBuf readRetainedSlice(int p_readRetainedSlice_1_) {
        return this.buffer.readRetainedSlice(p_readRetainedSlice_1_);
    }
    
    public ByteBuf readBytes(ByteBuf p_readBytes_1_) {
        return this.buffer.readBytes(p_readBytes_1_);
    }
    
    public ByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_) {
        return this.buffer.readBytes(p_readBytes_1_, p_readBytes_2_);
    }
    
    public ByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_) {
        return this.buffer.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_3_);
    }
    
    public ByteBuf readBytes(byte[] p_readBytes_1_) {
        return this.buffer.readBytes(p_readBytes_1_);
    }
    
    public ByteBuf readBytes(byte[] p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_) {
        return this.buffer.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_3_);
    }
    
    public ByteBuf readBytes(java.nio.ByteBuffer p_readBytes_1_) {
        return this.buffer.readBytes(p_readBytes_1_);
    }
    
    public ByteBuf readBytes(OutputStream p_readBytes_1_, int p_readBytes_2_) throws IOException {
        return this.buffer.readBytes(p_readBytes_1_, p_readBytes_2_);
    }
    
    public int readBytes(GatheringByteChannel p_readBytes_1_, int p_readBytes_2_) throws IOException {
        return this.buffer.readBytes(p_readBytes_1_, p_readBytes_2_);
    }
    
    public CharSequence readCharSequence(int p_readCharSequence_1_, Charset p_readCharSequence_2_) {
        return this.buffer.readCharSequence(p_readCharSequence_1_, p_readCharSequence_2_);
    }
    
    public int readBytes(FileChannel p_readBytes_1_, long p_readBytes_2_, int p_readBytes_4_) throws IOException {
        return this.buffer.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_4_);
    }
    
    public ByteBuf skipBytes(int p_skipBytes_1_) {
        return this.buffer.skipBytes(p_skipBytes_1_);
    }
    
    public ByteBuf writeBoolean(boolean p_writeBoolean_1_) {
        return this.buffer.writeBoolean(p_writeBoolean_1_);
    }
    
    public ByteBuf writeByte(int p_writeByte_1_) {
        return this.buffer.writeByte(p_writeByte_1_);
    }
    
    public ByteBuf writeShort(int p_writeShort_1_) {
        return this.buffer.writeShort(p_writeShort_1_);
    }
    
    public ByteBuf writeShortLE(int p_writeShortLE_1_) {
        return this.buffer.writeShortLE(p_writeShortLE_1_);
    }
    
    public ByteBuf writeMedium(int p_writeMedium_1_) {
        return this.buffer.writeMedium(p_writeMedium_1_);
    }
    
    public ByteBuf writeMediumLE(int p_writeMediumLE_1_) {
        return this.buffer.writeMediumLE(p_writeMediumLE_1_);
    }
    
    public ByteBuf writeInt(int p_writeInt_1_) {
        return this.buffer.writeInt(p_writeInt_1_);
    }
    
    public ByteBuf writeIntLE(int p_writeIntLE_1_) {
        return this.buffer.writeIntLE(p_writeIntLE_1_);
    }
    
    public ByteBuf writeLong(long p_writeLong_1_) {
        return this.buffer.writeLong(p_writeLong_1_);
    }
    
    public ByteBuf writeLongLE(long p_writeLongLE_1_) {
        return this.buffer.writeLongLE(p_writeLongLE_1_);
    }
    
    public ByteBuf writeChar(int p_writeChar_1_) {
        return this.buffer.writeChar(p_writeChar_1_);
    }
    
    public ByteBuf writeFloat(float p_writeFloat_1_) {
        return this.buffer.writeFloat(p_writeFloat_1_);
    }
    
    public ByteBuf writeDouble(double p_writeDouble_1_) {
        return this.buffer.writeDouble(p_writeDouble_1_);
    }
    
    public ByteBuf writeBytes(ByteBuf p_writeBytes_1_) {
        return this.buffer.writeBytes(p_writeBytes_1_);
    }
    
    public ByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_) {
        return this.buffer.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }
    
    public ByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_) {
        return this.buffer.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_3_);
    }
    
    public ByteBuf writeBytes(byte[] p_writeBytes_1_) {
        return this.buffer.writeBytes(p_writeBytes_1_);
    }
    
    public ByteBuf writeBytes(byte[] p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_) {
        return this.buffer.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_3_);
    }
    
    public ByteBuf writeBytes(java.nio.ByteBuffer p_writeBytes_1_) {
        return this.buffer.writeBytes(p_writeBytes_1_);
    }
    
    public int writeBytes(InputStream p_writeBytes_1_, int p_writeBytes_2_) throws IOException {
        return this.buffer.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }
    
    public int writeBytes(ScatteringByteChannel p_writeBytes_1_, int p_writeBytes_2_) throws IOException {
        return this.buffer.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }
    
    public int writeBytes(FileChannel p_writeBytes_1_, long p_writeBytes_2_, int p_writeBytes_4_) throws IOException {
        return this.buffer.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_4_);
    }
    
    public ByteBuf writeZero(int p_writeZero_1_) {
        return this.buffer.writeZero(p_writeZero_1_);
    }
    
    public int writeCharSequence(CharSequence p_writeCharSequence_1_, Charset p_writeCharSequence_2_) {
        return this.buffer.writeCharSequence(p_writeCharSequence_1_, p_writeCharSequence_2_);
    }
    
    public int indexOf(int p_indexOf_1_, int p_indexOf_2_, byte p_indexOf_3_) {
        return this.buffer.indexOf(p_indexOf_1_, p_indexOf_2_, p_indexOf_3_);
    }
    
    public int bytesBefore(byte p_bytesBefore_1_) {
        return this.buffer.bytesBefore(p_bytesBefore_1_);
    }
    
    public int bytesBefore(int p_bytesBefore_1_, byte p_bytesBefore_2_) {
        return this.buffer.bytesBefore(p_bytesBefore_1_, p_bytesBefore_2_);
    }
    
    public int bytesBefore(int p_bytesBefore_1_, int p_bytesBefore_2_, byte p_bytesBefore_3_) {
        return this.buffer.bytesBefore(p_bytesBefore_1_, p_bytesBefore_2_, p_bytesBefore_3_);
    }
    
    public int forEachByte(ByteProcessor p_forEachByte_1_) {
        return this.buffer.forEachByte(p_forEachByte_1_);
    }
    
    public int forEachByte(int p_forEachByte_1_, int p_forEachByte_2_, ByteProcessor p_forEachByte_3_) {
        return this.buffer.forEachByte(p_forEachByte_1_, p_forEachByte_2_, p_forEachByte_3_);
    }
    
    public int forEachByteDesc(ByteProcessor p_forEachByteDesc_1_) {
        return this.buffer.forEachByteDesc(p_forEachByteDesc_1_);
    }
    
    public int forEachByteDesc(int p_forEachByteDesc_1_, int p_forEachByteDesc_2_, ByteProcessor p_forEachByteDesc_3_) {
        return this.buffer.forEachByteDesc(p_forEachByteDesc_1_, p_forEachByteDesc_2_, p_forEachByteDesc_3_);
    }
    
    public ByteBuf copy() {
        return this.buffer.copy();
    }
    
    public ByteBuf copy(int p_copy_1_, int p_copy_2_) {
        return this.buffer.copy(p_copy_1_, p_copy_2_);
    }
    
    public ByteBuf slice() {
        return this.buffer.slice();
    }
    
    public ByteBuf retainedSlice() {
        return this.buffer.retainedSlice();
    }
    
    public ByteBuf slice(int p_slice_1_, int p_slice_2_) {
        return this.buffer.slice(p_slice_1_, p_slice_2_);
    }
    
    public ByteBuf retainedSlice(int p_retainedSlice_1_, int p_retainedSlice_2_) {
        return this.buffer.retainedSlice(p_retainedSlice_1_, p_retainedSlice_2_);
    }
    
    public ByteBuf duplicate() {
        return this.buffer.duplicate();
    }
    
    public ByteBuf retainedDuplicate() {
        return this.buffer.retainedDuplicate();
    }
    
    public int nioBufferCount() {
        return this.buffer.nioBufferCount();
    }
    
    public java.nio.ByteBuffer nioBuffer() {
        return this.buffer.nioBuffer();
    }
    
    public java.nio.ByteBuffer nioBuffer(int p_nioBuffer_1_, int p_nioBuffer_2_) {
        return this.buffer.nioBuffer(p_nioBuffer_1_, p_nioBuffer_2_);
    }
    
    public java.nio.ByteBuffer internalNioBuffer(int p_internalNioBuffer_1_, int p_internalNioBuffer_2_) {
        return this.buffer.internalNioBuffer(p_internalNioBuffer_1_, p_internalNioBuffer_2_);
    }
    
    public java.nio.ByteBuffer[] nioBuffers() {
        return this.buffer.nioBuffers();
    }
    
    public java.nio.ByteBuffer[] nioBuffers(int p_nioBuffers_1_, int p_nioBuffers_2_) {
        return this.buffer.nioBuffers(p_nioBuffers_1_, p_nioBuffers_2_);
    }
    
    public boolean hasArray() {
        return this.buffer.hasArray();
    }
    
    public byte[] array() {
        return this.buffer.array();
    }
    
    public int arrayOffset() {
        return this.buffer.arrayOffset();
    }
    
    public boolean hasMemoryAddress() {
        return this.buffer.hasMemoryAddress();
    }
    
    public long memoryAddress() {
        return this.buffer.memoryAddress();
    }
    
    public String toString(Charset p_toString_1_) {
        return this.buffer.toString(p_toString_1_);
    }
    
    public String toString(int p_toString_1_, int p_toString_2_, Charset p_toString_3_) {
        return this.buffer.toString(p_toString_1_, p_toString_2_, p_toString_3_);
    }
    
    public int hashCode() {
        return this.buffer.hashCode();
    }
    
    public boolean equals(Object p_equals_1_) {
        return this.buffer.equals(p_equals_1_);
    }
    
    public int compareTo(ByteBuf p_compareTo_1_) {
        return this.buffer.compareTo(p_compareTo_1_);
    }
    
    public String toString() {
        return this.buffer.toString();
    }
    
    public ByteBuf retain(int p_retain_1_) {
        return this.buffer.retain(p_retain_1_);
    }
    
    public ByteBuf retain() {
        return this.buffer.retain();
    }
    
    public ByteBuf touch() {
        return this.buffer.touch();
    }
    
    public ByteBuf touch(Object p_touch_1_) {
        return this.buffer.touch(p_touch_1_);
    }
    
    public int refCnt() {
        return this.buffer.refCnt();
    }
    
    public boolean release() {
        return this.buffer.release();
    }
    
    public boolean release(int p_release_1_) {
        return this.buffer.release(p_release_1_);
    }
}
