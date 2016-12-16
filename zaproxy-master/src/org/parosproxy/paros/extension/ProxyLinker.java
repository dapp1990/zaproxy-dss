package org.parosproxy.paros.extension;

import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.core.proxy.ProxyListener;

public class ProxyLinker extends HookProxyLinker {

	public ProxyLinker(Proxy proxy) {
		super(ProxyListener.class, "Proxy", proxy);
	}
	
}
