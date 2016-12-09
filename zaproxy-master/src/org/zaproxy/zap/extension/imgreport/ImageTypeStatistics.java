package org.zaproxy.zap.extension.imgreport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageTypeStatistics extends ImageStatistics{
	
	public ImageTypeStatistics(){
		
	}

	@Override
	public String getXML(List<HttpImage> siteImages) {
		HashMap<String,Integer> fileTypes = new HashMap<String, Integer>();
		
		for (HttpImage imgProperties : siteImages){
			if (fileTypes.containsKey(imgProperties.getExtension())){
				fileTypes.put(imgProperties.getExtension(), fileTypes.get(imgProperties.getExtension())+1);
			} else {
				fileTypes.put(imgProperties.getExtension(), 1);
			}
		}
		
		double totalAmountImages = siteImages.size();
		StringBuilder xml = new StringBuilder();
		xml.append("  <filetypes>\r\n");
		
		for (Map.Entry<String, Integer> entry : fileTypes.entrySet())
		{
			xml.append("    <"+entry.getKey()+">").append(100*entry.getValue()/totalAmountImages).append("</"+entry.getKey()+">\r\n");
		}
		
		xml.append("  </filetypes>\r\n");
		return xml.toString();
	}

}
