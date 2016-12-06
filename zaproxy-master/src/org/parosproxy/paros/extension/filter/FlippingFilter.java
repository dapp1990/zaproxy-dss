package org.parosproxy.paros.extension.filter;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class FlippingFilter extends SimpleImageFilter {
	
	public BufferedImage applyFilter(BufferedImage inputImage) {
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -inputImage.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		inputImage = op.filter(inputImage, null);
		return inputImage;
	}
	
}
