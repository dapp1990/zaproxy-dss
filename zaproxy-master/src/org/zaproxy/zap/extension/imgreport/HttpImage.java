package org.zaproxy.zap.extension.imgreport;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.parosproxy.paros.network.HttpMessage;

public class HttpImage {

	private int height;
	private int width;
	private int imageSize;
	private String extension;
	private String url;
	private BufferedImage bufferedImage;

	public HttpImage(HttpMessage msg){//int height, int width, int imageSize, String extension, String url, BufferedImage bufferedImage){
		//initial values in case of error
		this.height = 0;
		this.width = 0;
		this.imageSize = 0;
		this.extension = "";
		this.url = "";
		this.bufferedImage = null;
		
		String typeHeader = msg.getResponseHeader().getHeader("Content-Type");
		String extension = typeHeader.substring(typeHeader.lastIndexOf("/") + 1);
		String url = msg.getRequestHeader().getURI().toString();

		byte imageReference[] = msg.getResponseBody().getBytes().clone();
		ByteArrayInputStream imageValue = new ByteArrayInputStream(imageReference);
		BufferedImage imageInBuffer;

		try {
			if (imageValue != null){
				imageInBuffer = ImageIO.read(imageValue);

				ByteArrayOutputStream tmp = new ByteArrayOutputStream();

				if (imageInBuffer != null && !extension.isEmpty()){
					ImageIO.write(imageInBuffer, extension, tmp);
					tmp.close();
					Integer imageSize = tmp.size();

					this.height = imageInBuffer.getHeight();
					this.width = imageInBuffer.getWidth();
					this.imageSize = imageSize;
					this.extension = extension;
					this.url = url;
					this.bufferedImage = imageInBuffer;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
