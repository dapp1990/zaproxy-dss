package org.zaproxy.zap.extension.imgreport;

public class FileWidthStatistics extends ImageDimensionStatistics{
	
	public FileWidthStatistics(){
		super("fileheight", "getHeight");
	}
	
//	public FileWidthStatistics(){}
//
//	@Override
//	public String getXML(List<HttpImage> siteImages) {
//		List<HttpImage> sortedSiteImages = sortByFileWidth(siteImages);
//		
//		StringBuilder xml = new StringBuilder();
//		
//		int medIndex = sortedSiteImages.size()/2;
//		double avgFileWidth = getAvgImageWidth(sortedSiteImages);
//		
//		xml.append("  <fileWidth>\r\n");
//		
//		xml.append("    <min>").append(sortedSiteImages.get((sortedSiteImages.size()-1)).getWidth()).append("</min>\r\n");
//		xml.append("	<minurl><![CDATA[").append(sortedSiteImages.get((sortedSiteImages.size()-1)).getUrl()).append("]]></minurl>\r\n");
//		
//		xml.append("    <max>").append(sortedSiteImages.get(0).getUrl()).append("</max>\r\n");
//		xml.append("	<maxurl><![CDATA[").append(sortedSiteImages.get(0).getUrl()).append("]]></maxurl>\r\n");
//		
//		xml.append("	<med>").append(sortedSiteImages.get(medIndex).getWidth()).append("</med>\r\n");
//		
//		xml.append("	<avg>").append(avgFileWidth).append("</avg>\r\n");
//		
//		xml.append("  </fileWidth>\r\n");
//		
//		return xml.toString();
//	}
//
//	private double getAvgImageWidth(List<HttpImage> siteImages) {
//		int counter = 0;
//		
//		for(HttpImage img: siteImages){
//			counter += img.getWidth();
//		}
//		
//		return counter/siteImages.size();
//	}
//
//	private List<HttpImage> sortByFileWidth(List<HttpImage> siteImages) {
//		Collections.sort(siteImages, new Comparator<HttpImage>() {
//            public int compare(HttpImage o1, HttpImage o2) {
//                return o1.getWidth() > o2.getWidth() ? -1 : o1.getWidth() == o2.getWidth() ? 0 : 1;
//            }
//        });
//		return siteImages;
//	}
}
