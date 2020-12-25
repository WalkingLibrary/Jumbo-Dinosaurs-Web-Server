package com.jumbodinosaurs.webserver.post.miscellaneous.minecraft;

import com.jumbodinosaurs.devlib.util.objects.Point3D;

public abstract class CoordinateBased extends MinecraftObject
{
    private Point3D location;
    
    public Point3D getLocation()
    {
        return location;
    }
    
    public void setLocation(Point3D location)
    {
        this.location = location;
    }
    
    @Override
    public boolean isValidObject()
    {
        int vanillaWorldSize = 30000000;
        int vanillaWorldHeight = 256;
        
        if(this.location.getZ() > vanillaWorldSize || this.location.getZ() < -vanillaWorldSize)
        {
            return false;
        }
        if(this.location.getX() > vanillaWorldSize || this.location.getX() < -vanillaWorldSize)
        {
            return false;
        }
        
        if(this.location.getY() > vanillaWorldHeight || this.location.getY() < 0)
        {
            return false;
        }
        
        return super.isValidObject();
    }
}
