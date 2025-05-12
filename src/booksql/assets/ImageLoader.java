/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package booksql.assets;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author zander
 */
public class ImageLoader {
    public static final String LOGOURL = "src/booksql/assets/logo.png";
    public static final String LIBRARYURL = "src/booksql/assets/ndsulibrary.jpg";
    
    public static BufferedImage loadImage(String url, int width, int height) throws IOException{
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(url));
        } catch(IOException io){
          io.printStackTrace();
          throw io;
        }
        
        return img;
    }
    
    public static Icon loadIcon(String url, int width, int height) throws IOException{
        ImageIcon unscaledIcon = new ImageIcon(url);
        Image img = unscaledIcon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        Icon ico = new ImageIcon(scaledImg);
        return ico;
    }
            
    private static void printDir(){
        File current = new File(".");
        File[] ls = current.listFiles();
        for(File f : ls){
            System.out.println(f);
        }
    }
}
