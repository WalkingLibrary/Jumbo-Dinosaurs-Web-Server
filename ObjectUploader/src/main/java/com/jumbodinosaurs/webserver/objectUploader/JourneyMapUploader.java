package com.jumbodinosaurs.webserver.objectUploader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.devlib.util.WebUtil;
import com.jumbodinosaurs.devlib.util.objects.HttpResponse;
import com.jumbodinosaurs.devlib.util.objects.Point2D;
import com.jumbodinosaurs.devlib.util.objects.Point3D;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.commands.OperatorConsole;
import com.jumbodinosaurs.webserver.post.miscellaneous.img.Image;
import com.jumbodinosaurs.webserver.post.miscellaneous.minecraft.MinecraftJourneyMapChunkImage;
import com.jumbodinosaurs.webserver.post.object.CRUDRequest;
import com.jumbodinosaurs.webserver.post.object.Permission;
import com.jumbodinosaurs.webserver.post.object.Table;
import com.jumbodinosaurs.webserver.util.ServerUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class JourneyMapUploader
{
    public void uploadJourneyMapDir(String dirPath)
    {
        
        
        String journeyMapDirPath = dirPath;
        System.out.println(journeyMapDirPath);
        File journeyMapDir = new File(journeyMapDirPath);
        
        ArrayList<File> journeyMapImages = new ArrayList<File>();
        journeyMapImages.addAll(Arrays.asList(journeyMapDir.listFiles()));
        
        for(File journeyMapImage : journeyMapImages)
        {
            /*
             * Process For Chunking the Journey Map Images
             * Validate the Image
             * Create the Chunks
             * Send them to the Server
             *
             *  */
            
            System.out.println("Enter Your JD Username:");
            String username = OperatorConsole.getEnsuredAnswer();
            System.out.println("Enter Your JD Password:");
            String password = OperatorConsole.getEnsuredAnswer();
            
            PostRequest getTablesRequest = new PostRequest();
            getTablesRequest.setUsername(username);
            getTablesRequest.setPassword(password);
            getTablesRequest.setCommand("GetTables");
            CRUDRequest crudRequest = new CRUDRequest();
            getTablesRequest.setContent(new Gson().toJson(crudRequest));
            Table tableToUploadTo;
            try
            {
                
                HttpResponse response = WebUtil.sendLocalHostPostRequest(getTablesRequest);
                Gson transientIgnorableGson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.VOLATILE).create();
                Type tableType = new TypeToken<ArrayList<Table>>() {}.getType();
                
                ArrayList<Table> tables = transientIgnorableGson.fromJson(response.getResponse(), tableType);
                if(tables.size() <= 1)
                {
                    System.out.println("You Have No Tables To Upload To");
                    System.exit(1);
                }
                System.out.println("Choose A Table: ");
                int count = 1;
                for(Table table : tables)
                {
                    
                    Permission currentTablePermissions = table.getPermissions(username);
                    if(currentTablePermissions.hasAdminPerms() || currentTablePermissions.canAdd())
                    {
                        System.out.println(count + ". " + table.getName() + "\nID: " + table.getId());
                    }
                    count++;
                }
                String tableNumber = OperatorConsole.getEnsuredAnswer();
                
                int tableIndex = Integer.parseInt(tableNumber);
                tableIndex--;
                tableToUploadTo = tables.get(tableIndex);
                
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.exit(1);
                return;
            }
            
            try
            {
                byte[] imageBytes = ServerUtil.readPhoto(journeyMapImage);
                BufferedImage journeyMapPNG = ImageIO.read(new ByteArrayInputStream(imageBytes));
                int width = journeyMapPNG.getWidth();
                int height = journeyMapPNG.getHeight();
                
                int journeyMapRegionImageSize = 512;
                
                String acceptedFileType = "png";
                boolean isAcceptedFileType = GeneralUtil.getType(journeyMapImage).equals(acceptedFileType);
                //Validate the Image
                //Journey Map Images are Region Sized 32 by 32 Chunks
                // Chunks are 16 by 16
                if(width == journeyMapRegionImageSize && height == journeyMapRegionImageSize && isAcceptedFileType)
                {
                    //Create the Chunks
                    int regionX, regionZ;
                    String fileName = journeyMapImage.getName();
                    int commaIndex = fileName.indexOf(",");
                    regionX = Integer.parseInt(fileName.substring(0, commaIndex));
                    regionZ = Integer.parseInt(fileName.substring(commaIndex + 1, fileName.indexOf(".")));
                    Point2D regionCoord = new Point2D(regionX, regionZ);
                    HashMap<Point2D, BufferedImage> chunkedImages = chunkJourneyMapImage(journeyMapPNG, regionCoord);
                    
                    File chunkedImagesDir = GeneralUtil.checkFor(journeyMapDir.getParentFile(), "Chunked Images", true);
                    
                    ArrayList<File> journeyMapImagesToSend = new ArrayList<File>();
                    //Send them to the Server
                    for(Point2D chunkCoord : chunkedImages.keySet())
                    {
                        int x, y, z;
                        x = (int) (chunkCoord.getX());
                        y = 0;
                        z = (int) (chunkCoord.getZ());
                        
                        BufferedImage currentImage = chunkedImages.get(chunkCoord);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(currentImage, "png", baos);
                        byte[] currentImageBytes = baos.toByteArray();
                        
                        PostRequest uploadPostRequest = new PostRequest();
                        uploadPostRequest.setCommand("AddObject");
                        uploadPostRequest.setUsername(username);
                        uploadPostRequest.setPassword(password);
                        
                        CRUDRequest uploadCRUDRequest = new CRUDRequest();
                        MinecraftJourneyMapChunkImage journeyMapChunkImage = new MinecraftJourneyMapChunkImage();
                        journeyMapChunkImage.setUser(username);
                        journeyMapChunkImage.setLocation(new Point3D(x, y, z));
                        journeyMapChunkImage.setServer("testServer");
                        
                        Image chunkImage = new Image();
                        chunkImage.setFileType("png");
                        
                        String base64PhotoContents = Base64.getEncoder().encodeToString(currentImageBytes);
                        
                        chunkImage.setBase64ImageContents(base64PhotoContents);
                        
                        journeyMapChunkImage.setImage(chunkImage);
                        
                        uploadCRUDRequest.setObject(new Gson().toJson(journeyMapChunkImage));
                        uploadCRUDRequest.setTableID(tableToUploadTo.getId());
                        uploadCRUDRequest.setObjectType(journeyMapChunkImage.getClass().getSimpleName());
                        uploadPostRequest.setContent(new Gson().toJson(uploadCRUDRequest));
                        HttpResponse response = WebUtil.sendLocalHostPostRequest(uploadPostRequest);
                        if(response.getStatus() != 200)
                        {
                            String newChunkImageFileName = x + "," + z + ".png";
                            File newChunk = GeneralUtil.checkFor(chunkedImagesDir, newChunkImageFileName);
                            System.out.println("There was a problem uploading " + newChunk.getName() + ".");
                            //TODO Write this file and have a mode to upload failed chunks
                            System.exit(1);
                        }
                        
                    }
                    
                    
                }
                else
                {
                    System.out.println("Skipping " + journeyMapImage.getName() + ". Not the right Format");
                }
            }
            catch(IOException e)
            {
                System.out.println("Could Not Read " + journeyMapImage.getName());
            }
            
            
        }
        
    }
    
    public static HashMap<Point2D, BufferedImage> chunkJourneyMapImage(BufferedImage validJourneyMapImage,
                                                                       Point2D regionCoord)
    {
        HashMap<Point2D, BufferedImage> chunksPhotos = new HashMap<Point2D, BufferedImage>();
        
        int journeyMapRegionImageSize = 512;
        int maxChunks = journeyMapRegionImageSize / 16;
        
        int chunkSize = 16;
        
        //These Two Loops Iterate over Chunks
        for(int i = 0; i < maxChunks; i++)
        {
            for(int c = 0; c < maxChunks; c++)
            {
                BufferedImage currentChunkImage = new BufferedImage(chunkSize, chunkSize, BufferedImage.TYPE_INT_ARGB);
                boolean isChunkLoaded = true;
                
                /*These Two Loops Iterate Over Blocks*/
                for(int j = 0; j < chunkSize; j++)
                {
                    for(int k = 0; k < chunkSize; k++)
                    {
                        //check to see if chunk is loaded
                        int x, y;
                        x = i * 16 + j;
                        y = c * 16 + k;
                        Color currentColor = new Color(validJourneyMapImage.getRGB(x, y), true);
                        currentChunkImage.setRGB(j, k, currentColor.getRGB());
                        if(k == 0 && j == 0)
                        {
                            //Need to make Sure Chunk is loaded by checking the alpha level
                            if(currentColor.getAlpha() == 0)
                            {
                                isChunkLoaded = false;
                            }
                        }
                        
                    }
                }
                
                //If its loaded we add it to the hashmap
                if(isChunkLoaded)
                {
                    int chunkCoordX, chunkCoordZ;
                    int amountOfChunksInARegion = 32;
                    chunkCoordX = (int) (regionCoord.getX() * amountOfChunksInARegion) + i;
                    chunkCoordZ = (int) (regionCoord.getZ() * amountOfChunksInARegion) + c;
                    Point2D currentChunkCoord = new Point2D(chunkCoordX, chunkCoordZ);
                    chunksPhotos.put(currentChunkCoord, currentChunkImage);
                }
            }
        }
        return chunksPhotos;
    }
    
}
