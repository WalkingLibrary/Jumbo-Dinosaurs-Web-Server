package com.jumbodinosaurs.database.objects;

import com.google.gson.Gson;
import com.jumbodinosaurs.util.ServerUtil;

import java.time.LocalDateTime;


public class MinecraftSign
{
    
    private String text1;
    private String text2;
    private String text3;
    private String text4;
    private LocalDateTime date;
    private int x;
    private int y;
    private int z;
    private int dimension;
    
    
    public MinecraftSign(String text1, String text2, String text3, String text4, LocalDateTime date, int x, int y, int z, int dimension)
    {
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;
        this.date = date;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }
    
    public MinecraftSign()
    {
    }
    
    
    public boolean samePlaceandText(MinecraftSign sign)
    {
        return this.getDateLess().equals(sign.getDateLess());
    }
    
    
    public MinecraftSign clone()
    {
        return new Gson().fromJson(new Gson().toJson(this), this.getClass());
    }
    
    public MinecraftSign getDateLess()
    {
        String thisAsJsonString = new Gson().toJson(this);
        MinecraftSign thisSign = new Gson().fromJson(thisAsJsonString, this.getClass());
        thisSign.setDate(null);
        return thisSign;
    }
    
    public boolean equals(MinecraftSign sign)
    {
        String otherJson = new Gson().toJson(sign);
        String thisJson = new Gson().toJson(this);
        return otherJson.equals(thisJson);
    }
    
    
    public String[] getTexts()
    {
        return new String[]{this.text1, this.text2, this.text3, this.text4};
    }
    
    
    @Override
    public String toString()
    {
        return "Sign\n\r\n\r" +
                       "Text: \n\r\n\r" +
                       this.text1 + "\n\r\n\r" +
                       this.text2 + "\n\r\n\r" +
                       this.text3 + "\n\r\n\r" +
                       this.text4 + "\n\r\n\r" +
                       "This sign was recorded on " + this.date.toString() +
                       " At X: " + this.x + " Z: " + this.z + " Y: " + this.y + "\n\r\n\r" +
                       "In Dimension " + this.dimension;
                  
    }
    
    public String getText1()
    {
        return text1;
    }
    
    public void setText1(String text1)
    {
        this.text1 = text1;
    }
    
    public String getText2()
    {
        return text2;
    }
    
    public void setText2(String text2)
    {
        this.text2 = text2;
    }
    
    public String getText3()
    {
        return text3;
    }
    
    public void setText3(String text3)
    {
        this.text3 = text3;
    }
    
    public String getText4()
    {
        return text4;
    }
    
    public void setText4(String text4)
    {
        this.text4 = text4;
    }
    
    public LocalDateTime getDate()
    {
        return date;
    }
    
    public void setDate(LocalDateTime date)
    {
        this.date = date;
    }
    
    public int getX()
    {
        return x;
    }
    
    public void setX(int x)
    {
        this.x = x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public void setY(int y)
    {
        this.y = y;
    }
    
    public int getZ()
    {
        return z;
    }
    
    public void setZ(int z)
    {
        this.z = z;
    }
    
    public int getDimension()
    {
        return dimension;
    }
    
    public void setDimension(int dimension)
    {
        this.dimension = dimension;
    }
    
    
    public static MinecraftSign getSanitizedSign(MinecraftSign sign)
    {
        MinecraftSign tempSign = new MinecraftSign();
        tempSign.setText1(ServerUtil.rewriteHTMLEscapeCharacters(sign.getText1()));
        tempSign.setText2(ServerUtil.rewriteHTMLEscapeCharacters(sign.getText2()));
        tempSign.setText3(ServerUtil.rewriteHTMLEscapeCharacters(sign.getText3()));
        tempSign.setText4(ServerUtil.rewriteHTMLEscapeCharacters(sign.getText4()));
        tempSign.setX(sign.getX());
        tempSign.setY(sign.getY());
        tempSign.setZ(sign.getZ());
        tempSign.setDate(sign.getDate());
        tempSign.setDimension(sign.getDimension());
        return tempSign;
    }
}

