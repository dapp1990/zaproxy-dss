package org.parosproxy.paros.extension.filter;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import com.gif4j.TextPainter;
import com.gif4j.Watermark;

public class WatermarkFilter extends SimpleImageFilter {

	public BufferedImage applyFilter(BufferedImage inputImage) {
		String watermarkText = "Watermark";
		TextPainter textPainter = new TextPainter(new Font("Verdana", Font.BOLD, inputImage.getWidth()/9));
		textPainter.setOutlinePaint(Color.BLUE);
		//render the specified text outlined
		BufferedImage renderedWatermarkText = textPainter.renderString(watermarkText,true);
		//create new Watermark
		Watermark watermark = new Watermark(renderedWatermarkText, Watermark.LAYOUT_MIDDLE_CENTER);
		//apply watermark to the specified image and return the result
		inputImage = watermark.apply(inputImage);
		return inputImage;
	}
	
}
