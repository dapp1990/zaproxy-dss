package org.parosproxy.paros.extension;

import org.zaproxy.zap.PersistentConnectionListener;

public class PersistentConnectionLinker extends HookProxyLinker {

	public PersistentConnectionLinker() {
		super(PersistentConnectionListener.class, "PersistentConnection");
	}

}
