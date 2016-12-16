package org.parosproxy.paros.extension;

import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.core.proxy.ConnectRequestProxyListener;

public class ConnectRequestProxyLinker extends HookProxyLinker {
	
	public ConnectRequestProxyLinker(Proxy proxy) {
		super(ConnectRequestProxyListener.class, "ConnectRequestProxy", proxy);
	}
	
}
