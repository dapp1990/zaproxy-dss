package org.parosproxy.paros.core.proxy.notification;

import java.util.List;

import org.parosproxy.paros.core.proxy.ConnectRequestProxyListener;
import org.parosproxy.paros.core.proxy.OverrideMessageProxyListener;
import org.parosproxy.paros.core.proxy.ProxyServer;
import org.parosproxy.paros.network.HttpMessage;

/**
 * Notifies the {@code ConnectRequestProxyListener}s that a HTTP CONNECT request was received from a client.
 * 
 */

public class NotificationConnectMessage extends ProxyListenerNotifier {
	
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
}
