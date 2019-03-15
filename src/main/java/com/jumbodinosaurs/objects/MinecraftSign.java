package com.jumbodinosaurs.objects;
public class MinecraftSign
{

    private String text1;
    private String text2;
    private String text3;
    private String text4;
    private String date;
    private int x;
    private int y;
    private int z;
    private int dimension;

    public MinecraftSign(int x, int y, int z, String text1, String text2, String text3, String text4, String date, int dimension)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;
        this.date = date;
        this.dimension = dimension;
    }

    public String getText1()
    {
        return this.text1;
    }
    public String getText2()
    {
        return this.text2;
    }
    public String getText3()
    {
        return this.text3;
    }
    public String getText4()
    {
        return this.text4;
    }
    public String getDate()
    {
        return this.date;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getZ()
    {
        return this.z;
    }

    public int getDimension()
    {
        return this.dimension;
    }

    public boolean equals(MinecraftSign sign)
    {
        if(this.x == sign.getX() &&
                this.y == sign.getY() &&
                this.z == sign.getZ() &&
                this.text1.equals(sign.getText1()) &&
                this.text2.equals(sign.getText2()) &&
                this.text3.equals(sign.getText3()) &&
                this.text4.equals(sign.getText4()) &&
                this.dimension == sign.getDimension())
        {
            return true;
        }
        return false;
    }

    public boolean hasNulls()
    {
        if(!(this.y <= 0))
        {
           if(this.x != 0)
           {
               if(this.z != 0)
               {
                   if(this.text1 != null)
                   {
                       if(this.text2 != null)
                       {
                           if(this.text3 != null)
                           {
                               if(this.text4 != null)
                               {
                                   if(this.date != null)
                                   {
                                       if(this.dimension >= -1 && this.dimension <= 1)
                                       {
                                           return false;
                                       }
                                   }
                               }
                           }
                       }
                   }
               }
           }
        }
        return true;
    }

    public boolean isGoodPost()
    {
        if(!hasNulls())
        {
            return true;
        }
        return false;
    }



    public String toString()
    {
        String signFormated = "\r\n " + this.text1 + "\r\n " + this.text2 + "\r\n " + this.text3 + "\r\n " + this.text4 + "\r\n Recorded: " +
                this.date + "\r\n At: X: " + this.x + " Y: " + this.y + " Z: " + this.z + " In Dimension: " + dimension + "\r\n";
        return signFormated;
    }
}

