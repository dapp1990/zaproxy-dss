package org.parosproxy.paros.core.proxy;

import java.util.List;
import org.parosproxy.paros.network.HttpMessage;

/**
 * Go thru each observers and process the http message in each observers.
 * The msg can be changed by each observers.
 */

public class NotificationListenerResponseReceive extends NotificationHttp {
	
	private boolean returnStatement = true;
	
	@Override
	public boolean notify(ProxyServer proxyServer, HttpMessage httpMessage) {
		if (proxyServer.excludeUrl(httpMessage.getRequestHeader().getURI())) {
			return getReturnStatement();
		}
		
		return super.notify(proxyServer, httpMessage);
	}
	
	@Override
	protected boolean doTryStatement(Object object, HttpMessage httpMessage) {
		ProxyListener proxyListener = (ProxyListener) object;
		 if (! proxyListener.onHttpResponseReceive(httpMessage)) {
			 returnStatement =  false;
		 }
		 return ! returnStatement;
	}

	@Override
	protected List<ProxyListener> getListener(ProxyServer proxyServer) {
		return proxyServer.getListenerList();
	}

	@Override
	protected boolean getReturnStatement() {
		return returnStatement;
	}
}
