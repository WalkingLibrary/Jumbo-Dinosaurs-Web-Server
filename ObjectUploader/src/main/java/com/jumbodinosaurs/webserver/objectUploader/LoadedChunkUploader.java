package com.jumbodinosaurs.webserver.objectUploader;

import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.devlib.json.GsonUtil;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.devlib.util.objects.Point2D;
import com.jumbodinosaurs.devlib.util.objects.Point3D;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.post.miscellaneous.minecraft.MinecraftLoadedChunk;

import java.io.File;
import java.util.ArrayList;

public class LoadedChunkUploader
{
    public static void uploadLoadedChunkDir(String loadedChunkDir)
    {
        /* Process for uploading all the Loaded Chunk Data
         *
         * Get a list of all Jsons in the Dir Given
         *
         * Load the list of chunks from each json
         *
         * Send list of chunks to Server
         *
         *
         *  */
        
        //Get a list of all Jsons in the Dir Given
        String jsonExtension = "json";
        File dirToSearch = new File(loadedChunkDir);
        File[] files = GeneralUtil.listFilesRecursive(dirToSearch);
        ArrayList<File> filesToExtract = new ArrayList<File>();
        for(File file : files)
        {
            if(GeneralUtil.getType(file).equals(jsonExtension))
            {
                filesToExtract.add(file);
            }
        }
        
        
        // Load the list of chunks from each json
        ArrayList<MinecraftLoadedChunk> minecraftLoadedChunks = new ArrayList<MinecraftLoadedChunk>();
        for(File file : filesToExtract)
        {
            String fileContents = GeneralUtil.scanFileContents(file);
            ArrayList<Point2D> currentPointExtractList = GsonUtil.readList(file,
                                                                           Point2D.class,
                                                                           new TypeToken<ArrayList<Point2D>>() {},
                                                                           false);
            ArrayList<MinecraftLoadedChunk> currentLoadedChunkExtractList = new ArrayList<MinecraftLoadedChunk>();
            for(Point2D loadedChunk : currentPointExtractList)
            {
                //Create and valid the chunks loaded from the file
                MinecraftLoadedChunk minecraftLoadedChunk = new MinecraftLoadedChunk();
                minecraftLoadedChunk.setServer("Server");
                double x, y, z;
                x = loadedChunk.getX();
                z = loadedChunk.getZ();
                y = 0;
                minecraftLoadedChunk.setLocation(new Point3D(x, y, z));
                if(minecraftLoadedChunk.isValidObject())
                {
                    minecraftLoadedChunks.add(minecraftLoadedChunk);
                }
            }
            
        }
        // Send list of chunks to Server
        
        for(MinecraftLoadedChunk loadedChunk : minecraftLoadedChunks)
        {
            PostRequest sendChunkRequest = new PostRequest();
        }
    }
}
