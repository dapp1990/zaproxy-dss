package org.parosproxy.paros.core.proxy;

import java.util.List;
import org.parosproxy.paros.network.HttpMessage;

/**
 * Notifies the {@code ConnectRequestProxyListener}s that a HTTP CONNECT request was received from a client.
 * 
 */

public class NotificationConnectMessage extends NotificationHttp {
	
	@Override
	protected boolean doTryStatement(Object object, HttpMessage httpMessage) {
		ConnectRequestProxyListener connectRequestProxyListener = (ConnectRequestProxyListener) object;
		connectRequestProxyListener.receivedConnectRequest(httpMessage);
		return false;
	}

	@Override
	protected List<OverrideMessageProxyListener> getListener(ProxyServer proxyServer) {
		return proxyServer.getOverrideMessageProxyListeners();
	}

	@Override
	protected boolean getReturnStatement() {
		return true;
	}

	@Override
	protected boolean getExcludeStatement(ProxyServer proxyServer, HttpMessage httpMessage) {
		return false;
	}
}
