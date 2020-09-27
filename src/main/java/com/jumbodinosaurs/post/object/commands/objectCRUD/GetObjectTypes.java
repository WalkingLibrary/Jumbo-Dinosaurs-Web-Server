package com.jumbodinosaurs.post.object.commands.objectCRUD;

import com.google.gson.Gson;
import com.jumbodinosaurs.auth.util.AuthSession;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.netty.handler.http.util.ResponseHeaderUtil;
import com.jumbodinosaurs.post.PostCommand;
import com.jumbodinosaurs.post.object.ObjectTypeList;
import com.jumbodinosaurs.post.object.PostObject;

import java.util.ArrayList;

public class GetObjectTypes extends PostCommand
{
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /*
         * Process for getting the Post-able Object Types
         * Check/Verify CRUDRequest Attributes
         * Get/Create the List of all Post-able ObjectTypes
         * Return the list with the name objectTypes
         *  */
        HTTPResponse response = new HTTPResponse();
        //Check/Verify CRUDRequest Attributes
        
        
        ArrayList<Class> objectClassTypes = ReflectionUtil.getSubClasses(PostObject.class);
        ArrayList<String> objectTypes = new ArrayList<String>();
        for(Class clazz : objectClassTypes)
        {
            objectTypes.add(clazz.getSimpleName());
        }
        
        String listResponse = new Gson().toJson(new ObjectTypeList(objectTypes));
        String jsonApplicationTypeHeader = ResponseHeaderUtil.contentApplicationHeader + "json";
        response.setMessage200(jsonApplicationTypeHeader, listResponse);
        return response;
    }
    
    @Override
    public boolean requiresSuccessfulAuth()
    {
        return false;
    }
    
    @Override
    public boolean requiresPasswordAuth()
    {
        return false;
    }
    
    @Override
    public boolean requiresUser()
    {
        return false;
    }
}
