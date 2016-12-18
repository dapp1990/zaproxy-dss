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

	@Override
	protected boolean getExcludeStatement(ProxyServer proxyServer, HttpMessage httpMessage) {
		return proxyServer.excludeUrl(httpMessage.getRequestHeader().getURI());
	}
}
