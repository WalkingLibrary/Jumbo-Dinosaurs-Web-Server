package com.jumbodinosaur.webserver.post.miscellaneous.img;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.post.PostCommand;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class ImageToTextGoogleAPI extends PostCommand
{
    private transient static final String APPLICATION_NAME = "";
    private transient static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private transient static final List<String> SCOPES = Arrays.asList(VisionScopes.CLOUD_VISION);
    private transient static String credentialsFilePath = "";
    
    
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        HTTPResponse response = new HTTPResponse();
        
        if(request.getContent() == null)
        {
            response.setMessage400();
            return response;
        }
        
        try
        {
            com.jumbodinosaurs.webserver.post.miscellaneous.img.Image image = new Gson().fromJson(request.getContent(),
                                                                                                  com.jumbodinosaurs.webserver.post.miscellaneous.img.Image.class);
            if(!image.isValidObject())
            {
                response.setMessage400();
                return response;
            }
            
            byte[] imageBytes = Base64.getDecoder().decode(image.getBase64ImageContents());
            
            detectText(imageBytes);
            
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            response.setMessage400();
            return response;
        }
        
        response.setMessage200();
        return response;
    }
    
    // Detects text in the specified image.
    public void detectText(byte[] imageBytes)
            throws IOException, GeneralSecurityException
    {
        
        
        File credentialsFile = new File(credentialsFilePath);
        
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new FileInputStream(credentialsFile))
                                                               .createScoped(Lists.newArrayList(
                                                                       "https://www.googleapis.com/auth/cloud-platform"));
        List<AnnotateImageRequest> requests = new ArrayList<>();
        
        ByteString imgBytes = ByteString.readFrom(new ByteArrayInputStream(imageBytes));
        
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);
        
        
        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        ImageAnnotatorSettings imageAnnotatorSettings = ImageAnnotatorSettings.newBuilder()
                                                                              .setCredentialsProvider(
                                                                                      FixedCredentialsProvider.create(
                                                                                              googleCredentials))
                                                                              .build();
        try(ImageAnnotatorClient client = ImageAnnotatorClient.create(imageAnnotatorSettings))
        {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            
            for(AnnotateImageResponse res : responses)
            {
                if(res.hasError())
                {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return;
                }
                
                // For full list of available annotations, see http://g.co/cloud/vision/docs
                for(EntityAnnotation annotation : res.getTextAnnotationsList())
                {
                    System.out.format("Text: %s%n", annotation.getDescription());
                    System.out.format("Position : %s%n", annotation.getBoundingPoly());
                }
            }
        }
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
