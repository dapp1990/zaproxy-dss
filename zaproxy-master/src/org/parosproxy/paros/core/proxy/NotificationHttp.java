// Inspiration from strategy
// Inspiration from template

package org.parosproxy.paros.core.proxy;

import java.net.Socket;
import java.util.List;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.ZapGetMethod;
import org.apache.log4j.Logger;

public abstract class NotificationHttp {
	
	private static Logger log = Logger.getLogger(NotificationHttp.class);
	
	public final boolean notify(ProxyServer proxyServer, HttpMessage httpMessage) {
		if (getExcludeStatement(proxyServer, httpMessage)) {
			return getReturnStatement();
		}
		
		List<?> listenerList = getListener(proxyServer);
				
		for (int i = 0; i < listenerList.size(); i++) {
			try {
				if(doTryStatement(listenerList.get(i), httpMessage))
					break;
			} catch (Exception e) {
				log.error("An error occurred while notifying listener:", e);
			}
		}
		
		return getReturnStatement();
	}
	
	public final boolean notify(ProxyServer proxyServer, HttpMessage httpMessage, Socket inSocket, ZapGetMethod method) {
		
		List<?> listenerList = getListener(proxyServer);
		
		for (int i=0; i<listenerList.size(); i++) {
			try {
				if(doTryStatement(listenerList.get(i), httpMessage, inSocket, method))
					break;
			} catch (Exception e) {
				log.error("An error occurred while notifying listener:", e);
			}
		}
		return getReturnStatement();
	}

	protected boolean doTryStatement(Object object, HttpMessage httpMessage, Socket inSocket, ZapGetMethod method) {
		return false;
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

	protected boolean getExcludeStatement(ProxyServer proxyServer, HttpMessage httpMessage) {
		return false;
	}

}
