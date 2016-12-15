package org.parosproxy.paros.extension;

import org.parosproxy.paros.view.SiteMapPanel;

public class SiteMapLinker extends HookProxyLinker {

	public SiteMapLinker() {
		super(SiteMapPanel.class, "SiteMap");
	}

}
