package de.ba.coinIdentifier;

import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.vaadin.server.StreamResource.StreamSource;

/**
 * 
 * Notwendige Klasse in Vaadin zum verwalten von InputStreams
 * 
 * Quelle: https://vaadin.com/docs/-/part/framework/application/application-resources.html
 *
 * @author Matthias Jostock
 *
 */
public class MyImageSource
             implements StreamSource {
    ByteArrayOutputStream imagebuffer = null;
    BufferedImage imageInput = null;
    int reloads = 0;

    /* We need to implement this method that returns
     * the resource as a stream. */
    public InputStream getStream () {
        /* Create an image and draw something on it. */
        BufferedImage image = imageInput;


        try {
            /* Write the image to a buffer. */
            imagebuffer = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", imagebuffer);

            /* Return a stream from the buffer. */
            return new ByteArrayInputStream(
                         imagebuffer.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }
    
    public void setImage(BufferedImage input){
    	this.imageInput = input;
    	
    }
}