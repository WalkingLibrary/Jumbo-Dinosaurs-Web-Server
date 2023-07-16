package com.jumbodinosaurs.webserver.rest;

import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.reflection.ReflectionUtil;
import com.jumbodinosaurs.webserver.post.PostCommand;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class RESTAPIUtil
{

    private static final ArrayList<Class> apiEndPointClasses = getSubClasses(APIEndPoint.class);

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

    public static ArrayList<Class> getSubClasses(Class classTypePattern)
    {
        /* Getting an ArrayList of the SubClass Type of a given Class
         * Scan the Runtime Environment
         * Get all SubClass Instances of the Given classTypePattern
         * Filter this list for Abstract and Local Instances
         */

        //Scan the Runtime Environment
        try(ScanResult scanResult = new ClassGraph().enableAllInfo().scan())
        {
            //Get all SubClass Instances of the Given classTypePattern
            ClassInfoList controlClasses = scanResult.getSubclasses(classTypePattern.getCanonicalName());
            List<Class<?>> controlClassRefs = controlClasses.loadClasses();

            //Filter this list for Abstract and Local Instances
            ArrayList<Class> classes = new ArrayList<Class>();
            for(Class classType : controlClassRefs)
            {
                try
                {
                    //Local Instances
                    if(classType.getCanonicalName() != null)
                    {

                        LogManager.consoleLogger.debug("Found Class: " + classType.getCanonicalName());
                        //Abstract Instances
                        if(!Modifier.isAbstract(classType.getModifiers()))
                        {
                            classes.add(Class.forName(classType.getCanonicalName()));

                        }
                    }
                }
                catch(ClassNotFoundException e)
                {
                    System.out.println("Could Not find the Class " + classType.getSimpleName());
                }
            }
            return classes;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new ArrayList<Class>();
    }
}
