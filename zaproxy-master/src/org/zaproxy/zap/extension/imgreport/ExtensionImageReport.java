// TODO Modify report.html.xsl file to get the information in HTML reports (you can find the file in zaproxy-dss/zaproxy-master/src/xml)
// TODO Generalize methods/classes if it is possible
// TODO Split responsibilities if it is possible

package org.zaproxy.zap.extension.imgreport;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.model.SiteNode;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpSender;
import org.zaproxy.zap.extension.XmlReporterExtension;
import org.zaproxy.zap.network.HttpSenderListener;

/** TODO The ExtensionImageReport functionality is currently alpha.  */
public class ExtensionImageReport extends ExtensionAdaptor implements XmlReporterExtension, HttpSenderListener {
	
	private static final String NAME = "ExtensionImageReport";
	private List<HttpImage> httpImageList;
	
	//private ResourceBundle messages = null;

    public ExtensionImageReport() {
        super();
        initialize();
    }

    public ExtensionImageReport(String name) {
        super(name);
    }

    private void initialize() {
        this.setName(NAME);
        this.setOrder(2222); // TODO find optimal load order
        
        // Register as Http Sender listener in order to catch the received messages 
     	HttpSender.addListener(this);
     	
     	httpImageList = new ArrayList<HttpImage>();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
    	super.hook(extensionHook);
    }

	@Override
	public String getAuthor() {
		return "Team 8"; // TODO change to Constant.dss
	}
	
	@Override
    public String getDescription() {
        return "Add image details to reports"; // TODO change to Constant.messages.getString(ireport.desc);
    }
	
	/**
	 * No database tables used, so all supported
	 */
	@Override
	public boolean supportsDb(String type) {
    	return true;
    }
	
	@Override
	public String getXml(SiteNode site) {
	
		ArrayList<HttpImage> siteImages = new ArrayList<HttpImage>(httpImageList);
		
		// Filter out images from other sites
		siteImages.removeIf(httpImg -> !httpImg.getUrl().startsWith(site.getNodeName()));
		
		StringBuilder xml = new StringBuilder();
		
		// If there is not images in the site, <ImageStatistics> tag is empty
		
		xml.append("\r\n<imagestatistics>\r\n");
		if (!siteImages.isEmpty()){
			xml.append(getFileSizeStatistics(siteImages));
			xml.append(getFileWidthStatistics(siteImages));
			xml.append(getFileHeightStatistics(siteImages));
			xml.append(getFileTypeStatistics(siteImages));
		}
		
		xml.append("</imagestatistics>\r\n");
		
		return xml.toString();
	}

	private String getFileTypeStatistics(List<HttpImage> siteImages) {
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

	private String getFileHeightStatistics(List<HttpImage> siteImages) {
		sortByFileHeight(siteImages);
		
		StringBuilder xml = new StringBuilder();
		
		int medIndex = siteImages.size()/2;
		double avgFileHeight = getAvgImageHeight(siteImages);
		
		xml.append("  <fileHeight>\r\n");
		
		xml.append("    <min val=\"").append(siteImages.get((siteImages.size()-1)).getHeight());
		xml.append("\" minurl=\"").append(siteImages.get((siteImages.size()-1)).getUrl()).append("\"></min>\r\n");
		
		xml.append("    <max val=\"").append(siteImages.get(0).getHeight());
		xml.append("\" maxurl=\"").append(siteImages.get(0).getUrl()).append("\"></max>\r\n");
		
		xml.append("    <med val=\"").append(siteImages.get(medIndex).getHeight()).append("\"></med>\r\n");
		
		xml.append("    <avg val=\"").append(avgFileHeight).append("\"></avg>\r\n");
		
		xml.append("  </fileHeight>\r\n");
		
		return xml.toString();
	}

	private double getAvgImageHeight(List<HttpImage> siteImages) {
		int counter = 0;
		
		for(HttpImage img: siteImages){
			counter += img.getHeight();
		}
		
		return counter/siteImages.size();
	}

	private void sortByFileHeight(List<HttpImage> siteImages) {
		Collections.sort(httpImageList, new Comparator<HttpImage>() {
            public int compare(HttpImage o1, HttpImage o2) {
                return o1.getHeight() > o2.getHeight() ? -1 : o1.getHeight() == o2.getHeight() ? 0 : 1;
            }
        });
	}

	private String getFileWidthStatistics(List<HttpImage> siteImages) {
		sortByFileWidth(siteImages);
		
		StringBuilder xml = new StringBuilder();
		
		int medIndex = siteImages.size()/2;
		double avgFileWidth = getAvgImageWidth(siteImages);
		
		xml.append("  <fileWidth>\r\n");
		
		xml.append("    <min val=\"").append(siteImages.get((siteImages.size()-1)).getWidth());
		xml.append("\" minurl=\"").append(siteImages.get((siteImages.size()-1)).getUrl()).append("\"></min>\r\n");
		
		xml.append("    <max val=\"").append(siteImages.get(0).getWidth());
		xml.append("\" maxurl=\"").append(siteImages.get(0).getUrl()).append("\"></max>\r\n");
		
		xml.append("    <med val=\"").append(siteImages.get(medIndex).getWidth()).append("\"></med>\r\n");
		
		xml.append("    <avg val=\"").append(avgFileWidth).append("\"></avg>\r\n");
		
		xml.append("  </fileWidth>\r\n");
		
		return xml.toString();
	}

	private double getAvgImageWidth(List<HttpImage> siteImages) {
		int counter = 0;
		
		for(HttpImage img: siteImages){
			counter += img.getWidth();
		}
		
		return counter/siteImages.size();
	}

	private void sortByFileWidth(List<HttpImage> siteImages) {
		Collections.sort(httpImageList, new Comparator<HttpImage>() {
            public int compare(HttpImage o1, HttpImage o2) {
                return o1.getWidth() > o2.getWidth() ? -1 : o1.getWidth() == o2.getWidth() ? 0 : 1;
            }
        });
	}

	private String getFileSizeStatistics(List<HttpImage> siteImages) {
		
		sortByFileSize(siteImages);
		
		StringBuilder xml = new StringBuilder();
		
		int medIndex = siteImages.size()/2;
		double avgFileSize = getAvgImageSize(siteImages);
		
		xml.append("  <filesize>\r\n");
		
		xml.append("    <min val=\"").append(siteImages.get((siteImages.size()-1)).getImageSize());
		xml.append("\" minurl=\"").append(siteImages.get((siteImages.size()-1)).getUrl()).append("\"></min>\r\n");
		
		xml.append("    <min val=\"").append(siteImages.get((siteImages.size()-1)).getImageSize());
		
		xml.append("    <max val=\"").append(siteImages.get(0).getImageSize());
		xml.append("\" maxurl=\"").append(siteImages.get(0).getUrl()).append("\"></max>\r\n");
		
		xml.append("    <med val=\"").append(siteImages.get(medIndex).getImageSize()).append("\"></med>\r\n");
		
		xml.append("    <avg val=\"").append(avgFileSize).append("\"></avg>\r\n");
		
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

	private void sortByFileSize(List<HttpImage> imagePropertiesList) {
		Collections.sort(imagePropertiesList, new Comparator<HttpImage>() {
            public int compare(HttpImage o1, HttpImage o2) {
                return o1.getImageSize() > o2.getImageSize() ? -1 : o1.getImageSize() == o2.getImageSize() ? 0 : 1;
            }
        });
	}

	@Override
	public int getListenerOrder() {
		// This is not a core function 
		return 99999;
	}

	@Override
	public void onHttpRequestSend(HttpMessage msg, int initiator,
			HttpSender sender) {
		// Do nothing
	}

	@Override
	public void onHttpResponseReceive(HttpMessage msg, int initiator,
			HttpSender sender) {
		
		if (msg.getRequestHeader().isImage() && msg.getResponseBody().length() > 0 && !msg.getResponseHeader().isEmpty()) {
			addHttpImage(msg);
		}
	}

	private void addHttpImage(HttpMessage msg) {
		
		String extension = msg.getResponseHeader().getHeader("Content-Type").substring(msg.getResponseHeader().getHeader("Content-Type").lastIndexOf("/") + 1);
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
					
					httpImageList.add(new HttpImage(imageInBuffer.getHeight(), imageInBuffer.getWidth(), imageSize, extension, url, imageInBuffer));
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
