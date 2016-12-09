package org.zaproxy.zap.extension.imgreport;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileSizeStatistics extends ImageStatistics{
	
	public FileSizeStatistics(){
		
	}

	@Override
	public String getXML(List<HttpImage> siteImages) {
		List<HttpImage> sortedSiteImages = sortByFileSize(siteImages);
		
		StringBuilder xml = new StringBuilder();
		
		int medIndex = sortedSiteImages.size()/2;
		double avgFileSize = getAvgImageSize(sortedSiteImages);
		
		xml.append("  <filesize>\r\n");
		
		xml.append("    <min>").append(sortedSiteImages.get((sortedSiteImages.size()-1)).getImageSize()).append("</min>\r\n");
		xml.append("	<minurl><![CDATA[").append(sortedSiteImages.get((sortedSiteImages.size()-1)).getUrl()).append("]]></minurl>\r\n");
		
		xml.append("    <max>").append(sortedSiteImages.get(0).getImageSize()).append("</max>\r\n");
		xml.append("	<maxurl><![CDATA[").append(sortedSiteImages.get(0).getUrl()).append("]]></maxurl>\r\n");
		
		xml.append("	<med>").append(sortedSiteImages.get(medIndex).getImageSize()).append("</med>\r\n");
		
		xml.append("	<avg>").append(avgFileSize).append("</avg>\r\n");
		
		xml.append("  </filesize>\r\n");
		
		return xml.toString();
	}

	private double getAvgImageSize(List<HttpImage> siteImages) {
		int counter = 0;
		
		for(HttpImage img: siteImages){
			counter += img.getImageSize();
		}
		
		return counter/siteImages.size();
	}

	private List<HttpImage> sortByFileSize(List<HttpImage> imagePropertiesList) {
		Collections.sort(imagePropertiesList, new Comparator<HttpImage>() {
            public int compare(HttpImage o1, HttpImage o2) {
                return o1.getImageSize() > o2.getImageSize() ? -1 : o1.getImageSize() == o2.getImageSize() ? 0 : 1;
            }
        });
		return imagePropertiesList;
	}
}
