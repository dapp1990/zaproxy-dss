package org.parosproxy.paros.extension.filter;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.classifier.ClassifierHttpNaive;

public class FilterHttpContent extends FilterAdaptor {

	@Override
	public int getId() {
		return 2345671;
	}

	@Override
	public String getName() {
		// Supported languages: English and Spanish
		// return Constant.messages.getString("filter.imagefilter.name"); -> label for FilterReplaceImage
		return Constant.messages.getString("filter.contentfilter.name");
	}

	@Override
	public void onHttpRequestSend(HttpMessage httpMessage) {
		
	}

	@Override
	public void onHttpResponseReceive(HttpMessage httpMessage) {
		
		String inappropriateTags = (new ClassifierHttpNaive()).getClassification(httpMessage);
		
		if (! inappropriateTags.isEmpty()) {
			httpMessage.setResponseBody("PAGE WAS BLOCKED - The content of this page is " + inappropriateTags);
        }
        
	}
}
