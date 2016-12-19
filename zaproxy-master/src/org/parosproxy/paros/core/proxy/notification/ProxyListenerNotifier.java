
package org.parosproxy.paros.core.proxy.notification;

import java.util.List;
import java.util.function.Function;

import org.parosproxy.paros.core.proxy.ProxyServer;
import org.parosproxy.paros.network.HttpMessage;
import org.apache.log4j.Logger;

public abstract class ProxyListenerNotifier {
	
	private static Logger log = Logger.getLogger(ProxyListenerNotifier.class);
	
	public boolean notify(ProxyServer proxyServer, HttpMessage httpMessage) {
		
		return (applyNotificationFunction(proxyServer, pxs -> doTryStatement(pxs, httpMessage)));
	}
	
	public boolean applyNotificationFunction(ProxyServer proxyServer, Function<Object,Boolean> function){
		List<?> listenerList = getListener(proxyServer);
		
		for (int i=0; i<listenerList.size(); i++) {
			try {
				//if(doTryStatement(listenerList.get(i), httpMessage, inSocket, method))
				if(function.apply(listenerList.get(i)))
					break;
			} catch (Exception e) {
				log.error("An error occurred while notifying listener:", e);
			}
		}
		return getReturnStatement();
	}

	protected boolean doTryStatement(Object object, HttpMessage httpMessage) {
		return false;
	}

	protected List<?> getListener(ProxyServer proxyServer) {
		return null;
	}

	protected boolean getReturnStatement() {
		return false;
	}

}
