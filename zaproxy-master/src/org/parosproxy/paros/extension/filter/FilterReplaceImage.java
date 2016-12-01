package org.parosproxy.paros.extension.filter;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.network.HttpMessage;

public class FilterReplaceImage extends FilterAdaptor {

	@Override
	public int getId() {
		return 2345678;
	}

	@Override
	public String getName() {
		// TODO We should use the Constant.messages.getString() method to be coherent with zaproxy
		return "Filter Image";
	}

	@Override
	public void onHttpRequestSend(HttpMessage httpMessage) {
		//Array 
		// TODO Auto-generated method stub

	}

//	@Override
//	public void onHttpResponseReceive(HttpMessage httpMessage) {
//		if (httpMessage.getResponseHeader().getHeader("Content-Type").startsWith("image/")){
//			String extension = httpMessage.getResponseHeader().getHeader("Content-Type").substring(httpMessage.getResponseHeader().getHeader("Content-Type").lastIndexOf("/") + 1);
//
//			try {
//				//convert byte[] to BufferedImage
//				byte imageReference[] = httpMessage.getResponseBody().getBytes().clone();
//				ByteArrayInputStream imageValue = new ByteArrayInputStream(imageReference);
//				BufferedImage imageInBuffer = ImageIO.read(imageValue);
//
//				FileReader fr = new FileReader("resources/config.txt");
//				BufferedReader textReader = new BufferedReader(fr);
//
//				while(textReader.ready()){
//					SimpleImageFilter filter;
//					switch (textReader.readLine()){
//					case "watermark": filter = new WatermarkFilter();break;//ImageProcessor.applyWatermark(imageInBuffer); break;
//					case "enhance": filter = new EnhanceFilter();; break;
//					case "flip": filter = new FlippingFilter();; break;
//					default: throw new IllegalArgumentException();
//					}
//					imageInBuffer = filter.applyFilter(imageInBuffer);
//				}
//				textReader.close();
//
//				//convert BufferedImage to byte[]
//				ByteArrayOutputStream ouput = new ByteArrayOutputStream();
//				ImageIO.write(imageInBuffer, extension, ouput );
//				ouput.flush();
//				byte[] imageInByte = ouput.toByteArray();
//				ouput.close();
//
//				//set the filtered image to the body
//				httpMessage.setResponseBody(imageInByte);
//			} catch (IOException ioe){
//
//			}
//
//		}
	
	@Override
	public void onHttpResponseReceive(HttpMessage httpMessage) {
		if (httpMessage.getResponseHeader().getHeader("Content-Type").startsWith("image/")){
			String extension = httpMessage.getResponseHeader().getHeader("Content-Type").substring(httpMessage.getResponseHeader().getHeader("Content-Type").lastIndexOf("/") + 1);

			try {
				//convert byte[] to BufferedImage
				byte imageReference[] = httpMessage.getResponseBody().getBytes().clone();
				ByteArrayInputStream imageValue = new ByteArrayInputStream(imageReference);
				BufferedImage imageInBuffer = ImageIO.read(imageValue);

//				FileReader fr = new FileReader("resources/config.txt");
//				BufferedReader textReader = new BufferedReader(fr);
//
//				while(textReader.ready()){
//					SimpleImageFilter filter;
//					switch (textReader.readLine()){
//					case "watermark": filter = new WatermarkFilter();break;//ImageProcessor.applyWatermark(imageInBuffer); break;
//					case "enhance": filter = new EnhanceFilter();; break;
//					case "flip": filter = new FlippingFilter();; break;
//					default: throw new IllegalArgumentException();
//					}
//					imageInBuffer = filter.applyFilter(imageInBuffer);
//				}
//				textReader.close();
				
				ConfigurationReader<SimpleImageFilter> cr = new ConfigurationReader<SimpleImageFilter>("resources/config.txt", "org.parosproxy.paros.extension.filter");
				ArrayList<SimpleImageFilter> filters = cr.getInstances();
				for(SimpleImageFilter sif : filters)
					imageInBuffer = sif.applyFilter(imageInBuffer);

				//convert BufferedImage to byte[]
				ByteArrayOutputStream ouput = new ByteArrayOutputStream();
				ImageIO.write(imageInBuffer, extension, ouput );
				ouput.flush();
				byte[] imageInByte = ouput.toByteArray();
				ouput.close();

				//set the filtered image to the body
				httpMessage.setResponseBody(imageInByte);
			} catch (IOException ioe){

			}

		}
	}

}
