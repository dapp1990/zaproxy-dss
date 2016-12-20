package org.zaproxy.zap.extension.imgreport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class ImageDimensionStatistics extends ImageStatistics {
	
	private String xmlTag;
	private Method dimensionMethod;

	public ImageDimensionStatistics(String xmlTag, String methodName){
		this.xmlTag = xmlTag;
		try {
			  this.dimensionMethod = HttpImage.class.getMethod(methodName);
			} catch (SecurityException e) {  }
			  catch (NoSuchMethodException e) { }
	}

	@Override
	public String getXML(List<HttpImage> siteImages) {
		List<HttpImage> sortedSiteImages = sortByFileDimension(siteImages);

		StringBuilder xml = new StringBuilder();

		int medIndex = sortedSiteImages.size()/2;
		double avgFileDimension = getAvgImageDimension(sortedSiteImages);

		xml.append("  <"+xmlTag+">\r\n");

		xml.append("    <min>").append(getImageDimension(sortedSiteImages.get((sortedSiteImages.size()-1)))).append("</min>\r\n");
		xml.append("	<minurl><![CDATA[").append(sortedSiteImages.get((sortedSiteImages.size()-1)).getUrl()).append("]]></minurl>\r\n");

		xml.append("    <max>").append(getImageDimension(sortedSiteImages.get(0))).append("</max>\r\n");
		xml.append("	<maxurl><![CDATA[").append(sortedSiteImages.get(0).getUrl()).append("]]></maxurl>\r\n");

		xml.append("	<med>").append(getImageDimension(sortedSiteImages.get(medIndex))).append("</med>\r\n");

		xml.append("	<avg>").append(avgFileDimension).append("</avg>\r\n");

		xml.append("  </"+xmlTag+">\r\n");

		return xml.toString();
	}

	private double getAvgImageDimension(List<HttpImage> siteImages) {
		int counter = 0;
		for(HttpImage img: siteImages){
			counter += getImageDimension(img);
		}
		return counter/siteImages.size();
	}

	private List<HttpImage> sortByFileDimension(List<HttpImage> imagePropertiesList) {
		Collections.sort(imagePropertiesList, new Comparator<HttpImage>() {
			public int compare(HttpImage o1, HttpImage o2) {
				return getImageDimension(o1) > getImageDimension(o2) ? -1 : getImageDimension(o1) == getImageDimension(o2) ? 0 : 1;
			}
		});
		return imagePropertiesList;
	}
	
	private int getImageDimension(HttpImage img) {
		try {
			  return (int) dimensionMethod.invoke(img);
			} catch (IllegalArgumentException e) {  }
			  catch (IllegalAccessException e) { }
			  catch (InvocationTargetException e) { }
		return -1;
	}

}
