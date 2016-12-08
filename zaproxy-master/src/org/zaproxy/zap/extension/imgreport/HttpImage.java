package org.zaproxy.zap.extension.imgreport;

import java.awt.image.BufferedImage;

public class HttpImage {
	
	private int height;
	private int width;
	private int imageSize;
	private String extension;
	private String url;
	private BufferedImage bufferedImage;

	public HttpImage(int height, int width, int imageSize, String extension, String url, BufferedImage bufferedImage){
		this.setHeight(height);
		this.setWidth(width);
		this.setImageSize(imageSize);
		this.setExtension(extension);
		this.setUrl(url);
		this.setBufferedImage(bufferedImage);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getImageSize() {
		return imageSize;
	}

	public void setImageSize(int imageSize) {
		this.imageSize = imageSize;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}
	
	
}
