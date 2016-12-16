package org.parosproxy.paros.extension;

import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.core.proxy.OverrideMessageProxyListener;

public class OverrideMessageProxyLinker extends HookProxyLinker {

	public OverrideMessageProxyLinker(Proxy proxy) {
		super(OverrideMessageProxyListener.class, "OverrideMessageProxy", proxy);
	}

}
