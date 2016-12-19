package org.parosproxy.paros.extension.filter;

import java.util.ArrayList;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.filter.algorithm.FilterApplyer;
import org.parosproxy.paros.extension.filter.content.PageStringContent;
import org.parosproxy.paros.extension.filter.formatter.FormatFileToFilterInfo;
import org.parosproxy.paros.extension.filter.formatter.InappropriateElement;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpResponseHeader;
import org.zaproxy.zap.network.HttpResponseBody;

import javafx.util.Pair;

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
		PageStringContent pageContent = new PageStringContent(httpMessage);
		String content = pageContent.getContent();
		FormatFileToFilterInfo filterInfoParser = new FormatFileToFilterInfo("resources/contentFormat.txt");
		Pair<Integer, ArrayList<InappropriateElement<String>>> parsedFormatFile = filterInfoParser.getFilterParameters();
		FilterApplyer filterApplyer =new FilterApplyer();
		String filterResult = filterApplyer.applyBasicStringFilter(content, parsedFormatFile);
		httpMessage.setResponseBody(filterResult);
        
	}
}
