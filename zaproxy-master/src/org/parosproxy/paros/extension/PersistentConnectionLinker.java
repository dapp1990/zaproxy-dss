package org.parosproxy.paros.extension;

import org.parosproxy.paros.control.Proxy;
import org.zaproxy.zap.PersistentConnectionListener;

public class PersistentConnectionLinker extends HookProxyLinker {

	public PersistentConnectionLinker(Proxy proxy) {
		super(PersistentConnectionListener.class, "PersistentConnection", proxy);
	}

}
