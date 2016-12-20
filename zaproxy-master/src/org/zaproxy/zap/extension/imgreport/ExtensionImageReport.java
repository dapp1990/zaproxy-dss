package org.zaproxy.zap.extension.imgreport;

import java.util.ArrayList;
import java.util.List;

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
	private List<ImageStatistics> usedImageStatistics;

    public ExtensionImageReport() {
        super();
        initialize();
    }

    public ExtensionImageReport(String name) {
        super(name);
    }

    private void initialize() {
        this.setName(NAME);
        this.setOrder(2222);
        
        // Register as Http Sender listener in order to catch the received messages 
     	HttpSender.addListener(this);
     	
     	httpImageList = new ArrayList<HttpImage>();
     	usedImageStatistics = new ImageStatisticsFactory().getAllStatistics();
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
        return "Add image details to reports"; 
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
		
		StringBuilder xml = new StringBuilder();
		
		if(!httpImageList.isEmpty()){
			ArrayList<HttpImage> siteImages = new ArrayList<HttpImage>(httpImageList);
			
			// Filter out images from other sites
			siteImages.removeIf(httpImg -> !httpImg.getUrl().startsWith(site.getNodeName()));
	
			// If there is not images in the site, <ImageStatistics> tag is not added
			if (!siteImages.isEmpty()){
				xml.append("\r\n<imagestatistics>\r\n");
				xml.append("\r\n<site>").append(site.getNodeName()).append("</site>\r\n");
				for(ImageStatistics statMaker : usedImageStatistics) {
					xml.append(statMaker.getXML(siteImages));
				}
				xml.append("</imagestatistics>\r\n");
			}
		
		}
		return xml.toString();
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
			httpImageList.add(new HttpImage(msg));
		}
	}

}
