package org.parosproxy.paros.extension.filter;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.filter.algorithm.StringFilterApplyer;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpResponseHeader;
import org.zaproxy.zap.network.HttpResponseBody;

public class FilterHttpContent extends FilterAdaptor {

	@Override
	public int getId() {
		return 2345671;
	}

	@Override
	public String getName() {
		// Supported languages: English and Spanish
		return Constant.messages.getString("filter.contentfilter.name");
	}

	@Override
	public void onHttpRequestSend(HttpMessage httpMessage) {
		
	}

	@Override
	public void onHttpResponseReceive(HttpMessage httpMessage) {
		HttpResponseHeader header = httpMessage.getResponseHeader();
		HttpResponseBody body = httpMessage.getResponseBody();
		if (header.isEmpty() || header.isImage() || body.length() == 0) {
			return;		//Do nothing with the message if there is no content to filter.
		}
		StringFilterApplyer filterApplyer =new StringFilterApplyer();
		String filterResult = filterApplyer.executeFiltering(httpMessage, "resources/contentFormat.txt");
		httpMessage.setResponseBody(filterResult);
	}
}
