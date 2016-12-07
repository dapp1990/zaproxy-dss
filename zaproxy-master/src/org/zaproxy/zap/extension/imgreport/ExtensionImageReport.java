// TODO Implements image type statistics
// TODO Implements image width statistics
// TODO Implements image height statistics
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
import java.util.List;

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
	private List<ImageProperties> imagePropertiesList;
	
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
     	
     	imagePropertiesList = new ArrayList<ImageProperties>();
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
	
		List<ImageProperties> siteImages = imagePropertiesList;

		// Filter out images from other sites
		siteImages.removeIf(s -> !s.getUrl().startsWith(site.getNodeName()));
		
		StringBuilder xml = new StringBuilder();
		
		// If there is not images in the site, <ImageStatistics> tag is empty
		
		xml.append("\r\n<imagestatistics>\r\n");
		if (!siteImages.isEmpty()){
			xml.append(getFileSizeStatistics(siteImages));
			//Append other statistics
		}
		
		xml.append("</imagestatistics>\r\n");
		
		return xml.toString();
	}

	private String getFileSizeStatistics(List<ImageProperties> siteImages) {
		
		sortByFileSize(siteImages);
		
		StringBuilder xml = new StringBuilder();
		
		int medIndex = siteImages.size()/2;
		double avgFile = getAvgImageSize(siteImages);
		
		xml.append("  <filesize>\r\n");
		
		xml.append("    <min val=\"").append(siteImages.get((imagePropertiesList.size()-1)).getImageSize());
		xml.append("\" minurl=\"").append(siteImages.get((imagePropertiesList.size()-1)).getUrl()).append("\">\r\n");
		
		xml.append("    <max val=\"").append(siteImages.get(0).getImageSize());
		xml.append("\" maxurl=\"").append(siteImages.get(0).getUrl()).append("\">\r\n");
		
		xml.append("    <med val=\"").append(siteImages.get(medIndex).getImageSize()).append("\">\r\n");
		
		xml.append("    <avg val=\"").append(avgFile).append("\">\r\n");
		
		xml.append("  </filesize>\r\n");
		
		return xml.toString();
	}

	private double getAvgImageSize(List<ImageProperties> siteImages) {
		int counter = 0;
		
		for(ImageProperties img: siteImages){
			counter += img.getImageSize();
		}
		
		return counter/siteImages.size();
	}

	private void sortByFileSize(List<ImageProperties> imagePropertiesList) {
		Collections.sort(imagePropertiesList, new Comparator<ImageProperties>() {
            public int compare(ImageProperties o1, ImageProperties o2) {
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
		if (msg.getRequestHeader().isImage()) {
			
			String extension = msg.getResponseHeader().getHeader("Content-Type").substring(msg.getResponseHeader().getHeader("Content-Type").lastIndexOf("/") + 1);
			
			String url = msg.getRequestHeader().getHeader("Referer");
					
			byte imageReference[] = msg.getResponseBody().getBytes().clone();
			ByteArrayInputStream imageValue = new ByteArrayInputStream(imageReference);
			BufferedImage imageInBuffer;
			
			try {
				imageInBuffer = ImageIO.read(imageValue);
				
				ByteArrayOutputStream tmp = new ByteArrayOutputStream();
				ImageIO.write(imageInBuffer, extension, tmp);
				tmp.close();
				Integer imageSize = tmp.size();
				
				this.storeImage(new ImageProperties(imageInBuffer.getHeight(), imageInBuffer.getWidth(), imageSize, extension, url));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void storeImage(ImageProperties imgProperties ) {
		imagePropertiesList.add(imgProperties);
	}
	
}
