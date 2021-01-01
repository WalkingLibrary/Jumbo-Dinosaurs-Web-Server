package com.jumbodinosaur.webserver.post.miscellaneous.img;


import com.google.gson.Gson;
import com.jumbodinosaurs.devlib.util.GeneralUtil;
import com.jumbodinosaurs.devlib.util.objects.PostRequest;
import com.jumbodinosaurs.webserver.auth.util.AuthSession;
import com.jumbodinosaurs.webserver.netty.handler.http.util.HTTPResponse;
import com.jumbodinosaurs.webserver.post.PostCommand;
import com.jumbodinosaurs.webserver.post.miscellaneous.img.Image;
import com.jumbodinosaurs.webserver.util.ServerUtil;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ImageToTextTessaract extends PostCommand
{
    public static BufferedImage deNoise(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                Color color = new Color(image.getRGB(j, i));
                int threshold = 195;
                if(color.getRed() > threshold && color.getGreen() > threshold && color.getBlue() > threshold)
                {
                    image.setRGB(j, i, Color.BLACK.getRGB());
                }
                else if(color.getRed() <= threshold && color.getGreen() <= threshold && color.getBlue() <= threshold)
                {
                    image.setRGB(j, i, Color.WHITE.getRGB());
                }
            }
            
            
            File output = GeneralUtil.checkFor(ServerUtil.serverDataDir, "DeNoise.png");
            try
            {
                ImageIO.write(image, "png", output);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            
        }
        return image;
    }
    
    public static BufferedImage removeAlpha(BufferedImage img)
    {
        BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = copy.createGraphics();
        g2d.setColor(Color.WHITE); // Or what ever fill color you want...
        g2d.fillRect(0, 0, copy.getWidth(), copy.getHeight());
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return copy;
    }
    
    public static BufferedImage resize(BufferedImage img, int newW, int newH)
    {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        File output = GeneralUtil.checkFor(ServerUtil.serverDataDir, "bigphoto.png");
        try
        {
            ImageIO.write(dimg, "png", output);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return dimg;
    }
    
    // Detects text in the specified image.
    public static void detectText(BufferedImage image)
            throws TesseractException
    {
        ITesseract tesseral = new Tesseract();
        File tessDataFolder = LoadLibs.extractTessResources("tessdata"); // Maven build bundles English data
        tesseral.setDatapath(tessDataFolder.getPath());
        tesseral.setLanguage("eng");
        tesseral.setTessVariable("user_defined_dpi", "300");
        String result = tesseral.doOCR(image);
        System.out.println(result);
        
    }
    
    @Override
    public HTTPResponse getResponse(PostRequest request, AuthSession authSession)
    {
        /* Image To Text Process
         * Parse the image
         * Validate the image
         * Run thru Google's API
         * Return Text from image
         *
         *  */
        
        HTTPResponse response = new HTTPResponse();
        
        //Parse the image
        if(request.getContent() == null)
        {
            response.setMessage400();
            return response;
        }
        
        
        Image image = new Gson().fromJson(request.getContent(), Image.class);
        
        if(!image.isValidObject())
        {
            response.setMessage400();
            return response;
        }
        
        
        //Validate the image
        try
        {
            byte[] imageBytes = Base64.getDecoder().decode(image.getBase64ImageContents());
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            
            File output = GeneralUtil.checkFor(ServerUtil.serverDataDir, "HelloWorld." + image.getFileType());
            try
            {
                ImageIO.write(bufferedImage, image.getFileType(), output);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            
            //bufferedImage = removeAlpha(bufferedImage);
            bufferedImage = grayScale(bufferedImage);
            bufferedImage = deNoise(bufferedImage);
            double scale = 1.3;
            bufferedImage = resize(bufferedImage, (int) (width * scale), (int) (height * scale));
            
            
            detectText(bufferedImage);
            response.setMessage200();
            return response;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            response.setMessage400();
            return response;
        }
        
        
    }
    
    public BufferedImage grayScale(BufferedImage image)
    {
        
        int width = image.getWidth();
        int height = image.getHeight();
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                Color c = new Color(image.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);
                image.setRGB(j, i, newColor.getRGB());
            }
            
            
            File output = GeneralUtil.checkFor(ServerUtil.serverDataDir, "GrayScale.png");
            try
            {
                ImageIO.write(image, "png", output);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return image;
        
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
