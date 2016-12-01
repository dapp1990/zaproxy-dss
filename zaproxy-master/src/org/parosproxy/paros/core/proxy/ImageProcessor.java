package org.parosproxy.paros.core.proxy;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.gif4j.TextPainter;
import com.gif4j.Watermark;

public class ImageProcessor {
	
	public static BufferedImage applyWatermark(BufferedImage imageInBuffer) {
		String watermarkText = "Watermark";
		TextPainter textPainter = new TextPainter(new Font("Verdana", Font.BOLD, imageInBuffer.getWidth()/9));
		textPainter.setOutlinePaint(Color.BLUE);
		//render the specified text outlined
		BufferedImage renderedWatermarkText = textPainter.renderString(watermarkText,true);
		//create new Watermark
		Watermark watermark = new Watermark(renderedWatermarkText, Watermark.LAYOUT_MIDDLE_CENTER);
		//apply watermark to the specified image and return the result
		imageInBuffer = watermark.apply(imageInBuffer);
		return imageInBuffer;
	}

	public static BufferedImage flipImage(BufferedImage imageInBuffer) {
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -imageInBuffer.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		imageInBuffer = op.filter(imageInBuffer, null);
		return imageInBuffer;
	}

	public static BufferedImage enhanceImage(String extension,
		BufferedImage imageInBuffer) throws IOException {
		ByteArrayOutputStream tmp = new ByteArrayOutputStream();
		ImageIO.write(imageInBuffer, extension, tmp);
		tmp.close();
		Integer contentLength = tmp.size();
		
		if(contentLength>100000){
			//Get the RGB value of the pixel.
			//Find the average of RGB i.e., Avg = (R+G+B)/3
			// Replace the R, G and B value of the pixel with average (Avg) calculated in step 2.
			
			BufferedImage imgGray = new BufferedImage(imageInBuffer.getWidth(),imageInBuffer.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
			Graphics g = imgGray.getGraphics();
			g.drawImage(imageInBuffer, 0, 0, null);
			g.dispose();
			imageInBuffer = imgGray;
		}
		return imageInBuffer;
	}
}
