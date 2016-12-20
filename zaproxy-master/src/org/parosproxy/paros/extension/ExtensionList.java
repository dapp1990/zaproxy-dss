package org.parosproxy.paros.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.parosproxy.paros.db.Database;

public class ExtensionList {
	private final List<Extension> extensionList = new ArrayList<>();
	private final Map<Class<? extends Extension>, Extension> extensionsMap = new HashMap<>();
	private static final Logger logger = Logger.getLogger(ExtensionList.class);
	private Consumer<Double> splashScreenMethod;

	public void add(Extension extension) {
		this.extensionList.add(extension);
	}

	public void put(Class<? extends Extension> class1, Extension extension) {
		extensionsMap.put(extension.getClass(), extension);
	}

	public Extension get(int i) {
		return extensionList.get(i);
	}

	public void destroyAllExtensions() {
		for (Extension ext : extensionList) {
			try {
				ext.destroy();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public Extension getExtension(String name) {
		if (name != null) {
			return findExtension(ext -> ext.getName().equalsIgnoreCase(name));
		}
		return null;
	}

	public Extension getExtensionByClassName(String name) {
		if (name != null) {
			return findExtension(ext -> ext.getClass().getName().equals(name));
		}
		return null;
	}

	private Extension findExtension(Function<Extension, Boolean> checker) {
		for (Extension ext : extensionList) {
			if (checker.apply(ext)) {
				return ext;
			}
		}
		return null;
	}

	/**
	 * Gets the {@code Extension} with the given class.
	 *
	 * @param clazz the class of the {@code Extension}
	 * @return the {@code Extension} or {@code null} if not found.
	 */
	public <T extends Extension> T getExtension(Class<T> clazz) {
		if (clazz != null) {
			Extension extension = extensionsMap.get(clazz);
			if (extension != null) {
				return clazz.cast(extension);
			}
		}
		return null;
	}

	public int getExtensionCount() {
		return extensionList.size();
	}

	public List<Extension> getExtensions() {
		return new ArrayList<Extension>(this.extensionList);
	}

	// This method refuses to work with a Consumer closure/lambda. Even when adding try/catch here.
	// This is probably a minor bug in Java 8.
	public void databaseOpen(Database db) {
		for (Extension ext : extensionList) {
			try {
				ext.databaseOpen(db);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		// executeForAllExtensions(ext -> ext.databaseOpen(db));
	}

	public void stopAllExtension() {
		executeForAllExtensions(ext -> ext.stop());
	}
	
	private void executeForAllExtensions(Consumer<Extension> action) {
		for (Extension ext : extensionList) {
			try {
				action.accept(ext);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private List<String> getExtensionProperties(Function<Extension, List<String>> propertiesGetter) {
		List<String> list = new ArrayList<>();
		List<String> l;
		for (Extension ext : extensionList) {
			l = propertiesGetter.apply(ext);
			if (l != null) {
				list.addAll(l);
			}
		}
		return list;
	}
	
	/**
	 * Gets the names of all active actions of all the extensions.
	 *
	 * @return a {@code List} containing all the active actions of all add-ons, never {@code null}
	 * @since 2.4.0
	 * @see Extension#getActiveActions()
	 */
	public List<String> getActiveActions() {
		return getExtensionProperties(ext -> ext.getActiveActions());
	}
	
	/**
	 * Gets the names of all unsaved resources of all the extensions.
	 *
	 * @return a {@code List} containing all the unsaved resources of all add-ons, never {@code null}
	 * @see Extension#getActiveActions()
	 */
	public List<String> getUnsavedResources() {
		return getExtensionProperties(ext -> ext.getUnsavedResources());
	}

	public void remove(Extension extension) {
		extensionList.remove(extension);
		extensionsMap.remove(extension.getClass());
	}
	
	public void setSplashScreenMethod(Consumer<Double> method) {
		this.splashScreenMethod = method;
	}
	
	public void initExtensions(Consumer<Extension> extensionAction, double progressFactor) {
		double factorPerc = progressFactor / getExtensionCount();

		for (Extension ext : extensionList) {
			try {
				extensionAction.accept(ext);
				this.splashScreenMethod.accept(factorPerc);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	
	
	
}
