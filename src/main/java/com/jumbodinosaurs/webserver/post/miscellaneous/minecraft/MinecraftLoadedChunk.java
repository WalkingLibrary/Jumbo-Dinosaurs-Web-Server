package com.jumbodinosaurs.webserver.post.miscellaneous.minecraft;

public class MinecraftLoadedChunk extends CoordinateBased
{
    @Override
    public boolean isValidObject()
    {
        if(!super.isValidObject())
        {
            return false;
        }
        System.out.println("X: " + this.getLocation().getX());
        System.out.println("Z: " + this.getLocation().getZ());
        
        /*Set the Coordinate to the Chunk Coordinate*/
        this.getLocation().setX((int) (this.getLocation().getX() * 16));
        this.getLocation().setZ((int) (this.getLocation().getZ() * 16));
        System.out.println("");
        System.out.println("X: " + this.getLocation().getX());
        System.out.println("Z: " + this.getLocation().getZ());
        
        return true;
    }
}
