package org.parosproxy.paros.extension.filter;

import java.awt.image.BufferedImage;

public abstract class SimpleImageFilter {

	public abstract BufferedImage applyFilter (BufferedImage inputImage);
	
}
