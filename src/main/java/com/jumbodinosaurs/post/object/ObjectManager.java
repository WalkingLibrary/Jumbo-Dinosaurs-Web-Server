package com.jumbodinosaurs.post.object;

import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.post.object.exceptions.NoSuchPostObject;

public class ObjectManager
{
    public static <E> TypeToken<E> getTypeToken(String objectName)
            throws NoSuchPostObject
    {
        for(Class postObject : ReflectionUtil.getSubClasses(PostObject.class))
        {
            if(postObject.getSimpleName().equals(objectName))
            {
                return TypeToken.get(postObject);
            }
        }
        throw new NoSuchPostObject("No Post Object found with the name " + objectName);
    }
}
