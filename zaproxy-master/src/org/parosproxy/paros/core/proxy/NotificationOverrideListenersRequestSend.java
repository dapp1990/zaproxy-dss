package org.parosproxy.paros.core.proxy;

import java.util.List;
import org.parosproxy.paros.network.HttpMessage;

public class NotificationOverrideListenersRequestSend extends NotificationHttp {

	private boolean returnStatement = false;
	
	@Override
	protected boolean doTryStatement(Object object, HttpMessage httpMessage) {
		OverrideMessageProxyListener overrideMessageProxyListener = (OverrideMessageProxyListener) object;
		 if (overrideMessageProxyListener.onHttpRequestSend(httpMessage)) {
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

	@Override
	protected boolean getExcludeStatement(ProxyServer proxyServer, HttpMessage httpMessage) {
		return false;
	}
}
