package org.parosproxy.paros.extension.filter.content;

import org.parosproxy.paros.network.HttpMessage;

public class PageStringContent extends PageContent<String> {

	public PageStringContent(HttpMessage httpMessage) {
		super(httpMessage);
	}
	
	@Override
	public String getContent() {
		return this.body.toString();
	}
}
