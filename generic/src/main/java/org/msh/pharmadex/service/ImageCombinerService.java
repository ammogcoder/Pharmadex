package org.msh.pharmadex.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
/**
 *If 2 images then
	 *Connects two images into one.
	 *If the sum of the widths of images is used to the maximum given, then in the resulting image, 
	 *images are displayed from the top down (sorting by width, the first image having the maximum width )
	 *otherwise, the images are displayed from the left to the right (sort by height, the first image having the maximum height)
 *Else if 1 images then
     *return this images.
 */
@Service
public class ImageCombinerService {
	int maxWidth = 800;
	/**
	 * @param files1 - array of byte of the first image
	 * @param files2 - array of byte of the second image
	 * @return array of byte of the result image
	 */
	public byte[]  combiner(byte[] files1,byte[] files2){		
		try {
			if(files1!=null && files2!=null){//two files
			
				//Open input stream
				InputStream is1 = new ByteArrayInputStream(files1);
				InputStream is2 = new ByteArrayInputStream(files2);
				
				BufferedImage img1  = ImageIO.read(is1);
				BufferedImage img2 = ImageIO.read(is2);
				
				int widthImg1 = img1.getWidth();
				int heightImg1 = img1.getHeight();
	
				int widthImg2 = img2.getWidth();
				int heightImg2 = img2.getHeight();
	
				BufferedImage img =  null;
				if(widthImg1+widthImg2>maxWidth){//by height
					img = new BufferedImage(
							Math.max(widthImg1, widthImg2), // Final image will have width and height as
							heightImg1+heightImg2, // addition of widths and heights of the images we already have
							BufferedImage.TYPE_INT_ARGB);//BufferedImage.TYPE_INT_ARGB_PRE
					
					if(widthImg1>widthImg2){
						//Add down img1 to img2
						img.createGraphics().drawImage(img1, 0, 0, null); // 0, 0 are the x and y positions
						img.createGraphics().drawImage(img2, 0, heightImg1, null); // here width is mentioned
					}else{
						//Add down  к img2 to img1
						img.createGraphics().drawImage(img2, 0, 0, null); 
						img.createGraphics().drawImage(img1, 0, heightImg2, null);
					}					
					
				}else{//by width
					img = new BufferedImage(
							widthImg1+widthImg2, // Final image will have width and height as
							Math.max(heightImg1, heightImg2), // addition of widths and heights of the images we already have
							BufferedImage.TYPE_INT_ARGB);//BufferedImage.TYPE_INT_ARGB_PRE
					if(heightImg1>heightImg2){
						//Add to the right img1 to img2											
						img.createGraphics().drawImage(img1, 0, 0, null); 
						img.createGraphics().drawImage(img2, widthImg1, 0, null); 
					}else{
						//Add to the right  img2 to img1											
						img.createGraphics().drawImage(img2, 0, 0, null); 
						img.createGraphics().drawImage(img1, widthImg2, 0, null);
					}						
				}										 
				Graphics2D g = (Graphics2D) img.getGraphics();				 
				// Clear the background with white				
				g.setBackground(Color.WHITE);			
				g.dispose();	
				
				//Open output stream
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(img, "png", baos);//ImageIO.write(img, "jpg", baos);				
				byte[] bytes = baos.toByteArray();
								
				//Close streams
				is1.close();
				is2.close();
				baos.close();
				
				//setImg(img);	//TODO
				return bytes;					
			
			}else{//one file
				
				if(files1!=null || files2!=null){
					//Open input stream
					InputStream is1 = new ByteArrayInputStream(files1!=null?files1:files2);				
					BufferedImage img = ImageIO.read(is1);
					//Open output stream
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(img, "png", baos);
					//ImageIO.write(img, "jpg", baos);
					byte[] bytes = baos.toByteArray();
					//Close streams
					is1.close();
					baos.close();
					
					//setImg(img); //TODO
					return bytes;
				}
			}				
		} catch (IOException e1) {						
			e1.printStackTrace();
		}
		return null;
	}	

}
