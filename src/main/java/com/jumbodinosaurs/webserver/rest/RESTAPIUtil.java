package com.jumbodinosaurs.webserver.rest;

import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;

import java.util.ArrayList;

public class RESTAPIUtil
{

    private static final ArrayList<Class> apiEndPointClasses = ReflectionUtil.getSubClasses(APIEndPoint.class);

    public static ArrayList<APIEndPoint> getAPIEndPoints()
    {
        ArrayList<APIEndPoint> apiEndPoints = new ArrayList<APIEndPoint>();
        for(Class clazz : apiEndPointClasses)
        {
            LogManager.consoleLogger.debug("Clazz: "+ clazz.getName());
            try
            {
                apiEndPoints.add((APIEndPoint) clazz.newInstance());
            }
            catch(IllegalAccessException | InstantiationException error)
            {
                LogManager.consoleLogger.error(error.getMessage(), error);
                throw new IllegalStateException("Reflection Error");
            }
        }
        return apiEndPoints;
    }


}
