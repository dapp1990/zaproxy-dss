package org.zaproxy.zap.extension.imgreport;

public class ImageHeightStatistics extends ImageDimensionStatistics {
	
	public ImageHeightStatistics(){
		super("fileheight", "getHeight");
	}
	
//	public FileHeightStatistics(){}
//
//	@Override
//	public String getXML(List<HttpImage> siteImages) {
//		List<HttpImage> sortedSiteImages = sortByFileHeight(siteImages);
//		
//		StringBuilder xml = new StringBuilder();
//		
//		int medIndex = sortedSiteImages.size()/2;
//		double avgFileHeight = getAvgImageHeight(sortedSiteImages);
//		
//		xml.append("  <fileHeight>\r\n");
//		
//		xml.append("    <min>").append(sortedSiteImages.get((sortedSiteImages.size()-1)).getHeight()).append("</min>\r\n");
//		xml.append("	<minurl><![CDATA[").append(sortedSiteImages.get((sortedSiteImages.size()-1)).getUrl()).append("]]></minurl>\r\n");
//		
//		xml.append("    <max>").append(sortedSiteImages.get(0).getHeight()).append("</max>\r\n");
//		xml.append("	<maxurl><![CDATA[").append(sortedSiteImages.get(0).getUrl()).append("]]></maxurl>\r\n");
//		
//		xml.append("	<med>").append(sortedSiteImages.get(medIndex).getHeight()).append("</med>\r\n");
//		
//		xml.append("	<avg>").append(avgFileHeight).append("</avg>\r\n");
//		
//		xml.append("  </fileHeight>\r\n");
//		
//		return xml.toString();
//	}
//
//	private double getAvgImageHeight(List<HttpImage> siteImages) {
//		int counter = 0;
//		
//		for(HttpImage img: siteImages){
//			counter += img.getHeight();
//		}
//		
//		return counter/siteImages.size();
//	}
//
//	private List<HttpImage> sortByFileHeight(List<HttpImage> siteImages) {
//		Collections.sort(siteImages, new Comparator<HttpImage>() {
//            public int compare(HttpImage o1, HttpImage o2) {
//                return o1.getHeight() > o2.getHeight() ? -1 : o1.getHeight() == o2.getHeight() ? 0 : 1;
//            }
//        });
//		return siteImages;
//	}

}
