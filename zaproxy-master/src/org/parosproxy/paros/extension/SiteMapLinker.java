package org.parosproxy.paros.extension;

import org.parosproxy.paros.view.SiteMapPanel;
import org.zaproxy.zap.view.SiteMapListener;

public class SiteMapLinker extends HookProxyLinker {

	public SiteMapLinker(SiteMapPanel siteMapPanel) {
		super(SiteMapListener.class, "SiteMap", siteMapPanel);
	}

}
