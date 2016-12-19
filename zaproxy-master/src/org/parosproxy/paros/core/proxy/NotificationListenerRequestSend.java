package org.parosproxy.paros.core.proxy;

import java.util.List;
import org.parosproxy.paros.network.HttpMessage;

/**
 * Go through each observers to process a request in each observers.
 * The method can be modified in each observers.
 * 
 */

public class NotificationListenerRequestSend extends NotificationHttp {

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
		 if (! proxyListener.onHttpRequestSend(httpMessage)) {
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
