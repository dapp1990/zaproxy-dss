package org.parosproxy.paros.extension;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.control.Proxy;

public class HookProxyLinker {
	
	private Class<?> clazz;
	private Method getMethod;
	private Method addMethod;
	private Method removeMethod;

	protected HookProxyLinker(Class<?> clazz, String methodRoot) {
		this.clazz = clazz;
		try {
			this.getMethod = ExtensionHook.class.getMethod("get"+methodRoot+"Listeners");
			this.addMethod = Proxy.class.getMethod("add"+methodRoot+"Listener", clazz);
			this.removeMethod = Proxy.class.getMethod("remove"+methodRoot+"Listener", clazz);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		};
	}
	
	private static final Logger logger = Logger.getLogger(HookProxyLinker.class);		//Slight change functionality

	public void hookListener(Proxy proxy) {
		try {
			List<ExtensionHook> extensionHooks = Control.getSingleton().getExtensionLoader().getExtensionHooks();
			for (ExtensionHook hook : extensionHooks) {
				hookListeners(proxy, (List<?>) getMethod.invoke(hook));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
	}

	private void hookListeners(Proxy proxy, List<?> listeners) {
		for (Object listener : listeners) {
			try {
				if (listener != null) {
					//proxy.addPersistentConnectionListener(listener);
					this.addMethod.invoke(proxy, listener);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public void removeListener(ExtensionHook hook) {
		
		Proxy proxy = Control.getSingleton().getProxy();
		List<?> listenerList;
		try {
			listenerList = (List<?>) getMethod.invoke(hook);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return;
		} 
			for (Object listener : listenerList) {
				try {
					if (listener != null) {
						removeMethod.invoke(proxy, listener);
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
	}

}
