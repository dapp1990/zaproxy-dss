package org.parosproxy.paros.extension;

import org.parosproxy.paros.core.proxy.ConnectRequestProxyListener;

public class ConnectRequestProxyLinker extends HookProxyLinker {
	
	public ConnectRequestProxyLinker() {
		super(ConnectRequestProxyListener.class, "ConnectRequestProxy");
	}
	
}
