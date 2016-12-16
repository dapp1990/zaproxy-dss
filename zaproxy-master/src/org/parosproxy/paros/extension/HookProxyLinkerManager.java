package org.parosproxy.paros.extension;

import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.view.SiteMapPanel;

public class HookProxyLinkerManager {
	
	private PersistentConnectionLinker persistentConnectionLinker;
	private OverrideMessageProxyLinker overrideMessageProxyLinker;
	private ConnectRequestProxyLinker connectRequestProxyLinker;
	private SiteMapLinker siteMapLinker;
	private ProxyLinker proxyLinker;
	
	public HookProxyLinkerManager (Proxy proxy){
		
		persistentConnectionLinker = new PersistentConnectionLinker(proxy);
		overrideMessageProxyLinker = new OverrideMessageProxyLinker(proxy);
		connectRequestProxyLinker = new ConnectRequestProxyLinker(proxy);
		proxyLinker = new ProxyLinker(proxy);
		//siteMapLinker = new SiteMapLinker(siteMapPanel);
		
	}

	public void hookProxyListener() {
		proxyLinker.hookListener();
		
	}

	public void hookPersistentConnectionListener() {
		persistentConnectionLinker.hookListener();
		
	}

	public void hookConnectRequestProxyListener() {
		connectRequestProxyLinker.hookListener();
		
	}

	public void hookSiteMapListener(SiteMapPanel siteMapPanel) {
		if(siteMapLinker == null)
			siteMapLinker = new SiteMapLinker(siteMapPanel);
		siteMapLinker.hookListener();
	}
	
	public void hookOverrideMessageProxyListener() {
		overrideMessageProxyLinker.hookListener();
	}
	
	public void removeProxyListener(ExtensionHook hook) {
		proxyLinker.removeListener(hook);
		
	}

	public void removePersistentConnectionListener(ExtensionHook hook) {
		persistentConnectionLinker.removeListener(hook);
		
	}

	public void removeConnectRequestProxyListener(ExtensionHook hook) {
		connectRequestProxyLinker.removeListener(hook);
		
	}

	public void removeSiteMapListener(ExtensionHook hook) {
		try {
			siteMapLinker.removeListener(hook);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
	}
	
	public void removeOverrideMessageProxyListener(ExtensionHook hook) {
		overrideMessageProxyLinker.removeListener(hook);
	}
	
	public void removeAllListeners(ExtensionHook hook){
		this.removeConnectRequestProxyListener(hook);
		this.removeOverrideMessageProxyListener(hook);
		this.removePersistentConnectionListener(hook);
		this.removeProxyListener(hook);
		this.removeSiteMapListener(hook);
	}
}
