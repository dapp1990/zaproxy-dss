package org.parosproxy.paros.extension.filter.content;

import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpResponseHeader;
import org.zaproxy.zap.network.HttpResponseBody;

public abstract class PageContent<T> {

	protected HttpResponseHeader header;
	protected HttpResponseBody body;
	private String url;

	public PageContent(HttpMessage httpMessage) {
		this.header = httpMessage.getResponseHeader();
		this.body = httpMessage.getResponseBody();
		this.url = httpMessage.getRequestHeader().getURI().toString();
	}

	public byte[] getByteContent() {
		return body.getBytes();
	}
	
	public abstract T getContent();
	
	public String getUrl() {
		return this.url;
	}

	public String getExtension() {
		String typeHeader = header.getHeader("Content-Type");
		return typeHeader.substring(typeHeader.lastIndexOf("/") + 1);
	}
}
