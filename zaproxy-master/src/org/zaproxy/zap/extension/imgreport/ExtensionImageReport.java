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

import javax.imageio.ImageIO;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.extension.report.ReportGenerator;
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
    	//private Map<String, Integer> imageFileSizeStatistics;
    	//private Map<String, Integer> imageWidthStatistics;
    	//private Map<String, Integer> imageHeightStatistics;
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
		StringBuilder result = new StringBuilder();
		
		if (!imagePropertiesList.isEmpty()){
		int medIndex = imagePropertiesList.size()/2;
		
		//int avgFile = getAvgImageSize();
		
		sortByFileSize(imagePropertiesList);
		
		result.append("<FileSize>");
	    result.append("<min val=\"").append(imagePropertiesList.get((imagePropertiesList.size()-1)).getImageSize());
	    result.append("\" minurl=\"").append(imagePropertiesList.get((imagePropertiesList.size()-1)).getUrl()).append("\">\r\n");
	    
	    result.append("<max val=\"").append(imagePropertiesList.get(0).getImageSize());
	    result.append("\" maxurl=\"").append(imagePropertiesList.get(0).getUrl()).append("\">\r\n");
	    
	    result.append("<med val=\"").append(imagePropertiesList.get(medIndex).getImageSize()).append("\">\r\n");
	    
	    result.append("<avg val=\"").append(imagePropertiesList.get(medIndex).getImageSize()).append("\">\r\n");
	    
	    result.append("</FileSize>");
	    
		}
		
		return result.toString();
	}

	private void sortByFileSize(List<ImageProperties> imagePropertiesList) {
		Collections.sort(imagePropertiesList, new Comparator<ImageProperties>() {
            public int compare(ImageProperties o1, ImageProperties o2) {
                return o1.getImageSize() > o2.getImageSize() ? -1 : o1.getImageSize() == o2.getImageSize() ? 0 : 1;
            }
        });
	}

	private int getAvgImageSize() {
		// TODO Auto-generated method stub
		return 0;
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
			
			String url = msg.getResponseHeader().getHeader("From");
					
			byte imageReference[] = msg.getResponseBody().getBytes().clone();
			ByteArrayInputStream imageValue = new ByteArrayInputStream(imageReference);
			BufferedImage imageInBuffer;
			
			try {
				imageInBuffer = ImageIO.read(imageValue);
				
				ByteArrayOutputStream tmp = new ByteArrayOutputStream();
				ImageIO.write(imageInBuffer, "png", tmp);	//probably inaccurate with hardcoded extension
				tmp.close();
				Integer imageSize = tmp.size();
				
				System.out.println("ExtensionImageReport -> Process images here");
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
