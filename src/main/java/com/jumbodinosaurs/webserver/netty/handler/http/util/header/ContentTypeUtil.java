package com.jumbodinosaurs.webserver.netty.handler.http.util.header;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.devlib.log.LogManager;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.webserver.util.ServerUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

public class ContentTypeUtil
{
    private static final File definedContentTypesFile = GeneralUtil.checkFor(ServerUtil.serverDataDir,
                                                                             "contentMapping.json");
    private static ConcurrentHashMap<String, String> fileTypeToContentTypeMap = new ConcurrentHashMap<String, String>();
    
    public static void loadMappings()
    {
        String mappingFileContents = GeneralUtil.scanFileContents(definedContentTypesFile);
        
        /* Default Mappings
         * When there is no Contents in the Mapping contents we set the default mapping
         *
         *
         *
         * */
        
        if(mappingFileContents.equals(""))
        {
            mappingFileContents = getDefaultMappings();
        }
        
        try
        {
            fileTypeToContentTypeMap = new Gson().fromJson(mappingFileContents,
                                                           new TypeToken<ConcurrentHashMap<String, String>>() {}.getType());
            saveMappings();
        }
        catch(JsonParseException e)
        {
            LogManager.consoleLogger.error("Error Parsing Content Mapping");
            String backupFileName = "Error-ContentMappingJson-" +
                                    LocalDateTime.now().toString().replaceAll(":", "-") +
                                    ".json";
            File errorFile = GeneralUtil.checkFor(ServerUtil.serverDataDir, backupFileName);
            LogManager.consoleLogger.error("Saving error Mapping contents to " +
                                           backupFileName +
                                           " and Loading " +
                                           "Defaults");
            GeneralUtil.writeContents(errorFile, mappingFileContents, false);
        }
    }
    
    private static String getDefaultMappings()
    {
        ConcurrentHashMap<String, String> defaultMappings = new ConcurrentHashMap<String, String>();
        defaultMappings.put("txt", "text/text");
        defaultMappings.put("html", "text/%s");
        defaultMappings.put("css", "text/%s");
        defaultMappings.put("png", "image/%s");
        defaultMappings.put("jpeg", "image/%s");
        defaultMappings.put("JPG", "image/%s");
        defaultMappings.put("jpg", "image/%s");
        defaultMappings.put("ico", "image/%s");
        defaultMappings.put("gif", "image/%s");
        defaultMappings.put("zip", "application/%s");
        defaultMappings.put("pdf", "application/%s");
        defaultMappings.put("js", "application/javascript");
        defaultMappings.put("jar", "application/java-archive");
        defaultMappings.put("lua", "application/%s");
        defaultMappings.put("json", "application/%s");
        defaultMappings.put("map", "application/json");
        defaultMappings.put("glb", "application/%s");
        defaultMappings.put("obj", "application/%s");
        defaultMappings.put("babylon", "application/%s");
        return new Gson().toJson(defaultMappings);
    }
    
    public static void saveMappings()
    {
        String newMapping = new Gson().toJson(fileTypeToContentTypeMap);
        GeneralUtil.writeContents(definedContentTypesFile, newMapping, false);
    }
    
    public synchronized static void setMapping(String fileType, String contentType)
    {
        fileTypeToContentTypeMap.put(fileType, contentType);
        saveMappings();
    }
    
    public synchronized static String getContentType(String fileType)
    {
        String defaultContentType = "text/text";
        if(ContentTypeUtil.fileTypeToContentTypeMap.containsKey(fileType))
        {
            return String.format(ContentTypeUtil.fileTypeToContentTypeMap.get(fileType), fileType);
        }
        return defaultContentType;
    }
    
}
