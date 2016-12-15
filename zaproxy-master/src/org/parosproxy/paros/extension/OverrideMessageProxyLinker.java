package org.parosproxy.paros.extension;

import org.parosproxy.paros.core.proxy.OverrideMessageProxyListener;

public class OverrideMessageProxyLinker extends HookProxyLinker {

	public OverrideMessageProxyLinker() {
		super(OverrideMessageProxyListener.class, "OverrideMessageProxy");
	}

}
