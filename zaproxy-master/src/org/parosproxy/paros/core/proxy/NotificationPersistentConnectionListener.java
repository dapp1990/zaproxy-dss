package org.parosproxy.paros.core.proxy;

import java.net.Socket;
import java.util.List;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.PersistentConnectionListener;
import org.zaproxy.zap.ZapGetMethod;

/**
 * Go thru each listener and offer him to take over the connection. The
 * first observer that returns true gets exclusive rights.
 *
 */

public class NotificationPersistentConnectionListener extends NotificationHttp {

	protected boolean keepSocketOpen = false;
	
	@Override
	protected boolean doTryStatement(Object object, HttpMessage httpMessage, Socket inSocket, ZapGetMethod method) {
		PersistentConnectionListener persistentConnectionListener = (PersistentConnectionListener) object;
		 if (persistentConnectionListener.onHandshakeResponse(httpMessage, inSocket, method)) {
		    	keepSocketOpen = true;
		 }
		 
		return keepSocketOpen;
	}
	
	@Override
	protected List<PersistentConnectionListener> getListener(ProxyServer proxyServer) {
		return proxyServer.getPersistentConnectionListenerList();
	}
	
	@Override
	protected boolean getReturnStatement() {
		return keepSocketOpen;
	}
}
