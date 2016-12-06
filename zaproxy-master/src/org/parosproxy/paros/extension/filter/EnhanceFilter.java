package org.parosproxy.paros.extension.filter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class EnhanceFilter extends SimpleImageFilter {

	public BufferedImage applyFilter(BufferedImage inputImage) {
		ByteArrayOutputStream tmp = new ByteArrayOutputStream();
		BufferedImage outputImage;
		try {
			ImageIO.write(inputImage, "png", tmp);	//probably inaccurate with hardcoded extension
			tmp.close();
			Integer contentLength = tmp.size();


			if(contentLength>100000){
				//Get the RGB value of the pixel.
				//Find the average of RGB i.e., Avg = (R+G+B)/3
				// Replace the R, G and B value of the pixel with average (Avg) calculated in step 2.

				BufferedImage imgGray = new BufferedImage(inputImage.getWidth(),inputImage.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
				Graphics g = imgGray.getGraphics();
				g.drawImage(inputImage, 0, 0, null);
				g.dispose();
				outputImage = imgGray;
			} else {
				outputImage = inputImage;
			}
		} catch (IOException e) {
			e.printStackTrace();
			outputImage = inputImage;
		}
		return outputImage;
	}

}
