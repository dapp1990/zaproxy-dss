package org.parosproxy.paros.extension;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.control.Proxy;

public class HookProxyLinker {
	
	private static final Logger logger = Logger.getLogger(HookProxyLinker.class);		//Slight change functionality
	
	private Class<?> clazz;
	private Method getMethod;
	private Method addMethod;
	private Method removeMethod;
	private Object proxy;

	protected HookProxyLinker(Class<?> clazz, String methodRoot, Object proxy) {
		this.clazz = clazz;
		this.proxy = proxy;
		try {
			this.getMethod = ExtensionHook.class.getMethod("get"+methodRoot+"Listeners");
			this.addMethod = proxy.getClass().getMethod("add"+methodRoot+"Listener", clazz);
			this.removeMethod = proxy.getClass().getMethod("remove"+methodRoot+"Listener", clazz);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		};
	}

	public void hookListener() {
		try {
			List<ExtensionHook> extensionHooks = Control.getSingleton().getExtensionLoader().getExtensionHooks();
			for (ExtensionHook hook : extensionHooks) {
				hookListeners((List<?>) getMethod.invoke(hook));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} 
	}

	private void hookListeners(List<?> listeners) {
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
