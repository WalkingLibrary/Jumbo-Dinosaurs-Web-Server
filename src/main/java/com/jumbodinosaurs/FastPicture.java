package com.jumbodinosaurs;

public class FastPicture
{
    private String name;
    private String contents;
    private String length;

    public FastPicture(String name, String contents, String length)
    {
        this.name = name;
        this.contents = contents;
        this.length = length;
    }

    public String getName()
    {
        return this.name;
    }

    public String getContents()
    {
        return this.contents;
    }

    public String getLength()
    {
        return this.length;
    }

    public String[] getAsArray()
    {
        String[] temp = {this.getName(), this.getContents(), this.getLength()};
        return temp;
    }

    public String toString()
    {
        return "Name: " + this.name + "\nContent: " + this.contents + "\nLength: " + this.length;
    }
}
