package org.parosproxy.paros.core.proxy.notification;

import java.util.List;

import org.parosproxy.paros.core.proxy.OverrideMessageProxyListener;
import org.parosproxy.paros.core.proxy.ProxyServer;
import org.parosproxy.paros.network.HttpMessage;

public class NotificationOverrideListenersResponseReceived extends ProxyListenerNotifier {
	
	private boolean returnStatement = false;
	
	@Override
	protected boolean doTryStatement(Object object, HttpMessage httpMessage) {
		OverrideMessageProxyListener overrideMessageProxyListener = (OverrideMessageProxyListener) object;
		 if (overrideMessageProxyListener.onHttpResponseReceived(httpMessage)) {
			 returnStatement =  true;
		 }
		 return returnStatement;
	}
	
	@Override
	protected List<OverrideMessageProxyListener> getListener(ProxyServer proxyServer) {
		return proxyServer.getOverrideMessageProxyListeners();
	}

	@Override
	protected boolean getReturnStatement() {
		return returnStatement;
	}
}
