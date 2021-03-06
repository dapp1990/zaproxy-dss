/*
 *
 * Paros and its related class files.
 * 
 * Paros is an HTTP/HTTPS proxy for assessing web application security.
 * Copyright (C) 2003-2004 Chinotec Technologies Company
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Clarified Artistic License
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Clarified Artistic License for more details.
 * 
 * You should have received a copy of the Clarified Artistic License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
// ZAP: 2011/12/14 Support for extension dependencies
// ZAP: 2012/02/18 Rationalised session handling
// ZAP: 2012/03/15 Reflected the change in the name of the method optionsChanged of
// the class OptionsChangedListener. Changed the method destroyAllExtension() to
// save the configurations of the main http panels and save the configuration file.
// ZAP: 2012/04/23 Reverted the changes of the method destroyAllExtension(),
// now the configurations of the main http panels and the configuration file
// are saved in the method Control.shutdown(boolean).
// ZAP: 2012/04/24 Changed the method destroyAllExtension to catch exceptions.
// ZAP: 2012/04/25 Added the type argument and removed unnecessary cast.
// ZAP: 2012/07/23 Removed parameter from View.getSessionDialog call.
// ZAP: 2012/07/29 Issue 43: added sessionScopeChanged event
// ZAP: 2012/08/01 Issue 332: added support for Modes
// ZAP: 2012/11/30 Issue 425: Added tab index to support quick start tab 
// ZAP: 2012/12/27 Added hookPersistentConnectionListener() method.
// ZAP: 2013/01/16 Issue 453: Dynamic loading and unloading of add-ons
// ZAP: 2013/01/25 Added removeExtension(...) method and further helper methods
// to remove listeners, menu items, etc.
// ZAP: 2013/01/25 Refactored hookMenu(). Resolved some Checkstyle issues.
// ZAP: 2013/01/29 Catch Errors thrown by out of date extensions as well as Exceptions
// ZAP: 2013/07/23 Issue 738: Options to hide tabs
// ZAP: 2013/11/16 Issue 807: Error while loading ZAP when Quick Start Tab is closed
// ZAP: 2013/11/16 Issue 845: AbstractPanel added twice to TabbedPanel2 in ExtensionLoader#addTabPanel
// ZAP: 2013/12/03 Issue 934: Handle files on the command line via extension
// ZAP: 2013/12/13 Added support for Full Layout DISPLAY_OPTION_TOP_FULL in the hookView function.
// ZAP: 2014/03/23 Issue 1022: Proxy - Allow to override a proxied message
// ZAP: 2014/03/23 Issue 1090: Do not add pop up menus if target extension is not enabled
// ZAP: 2014/05/20 Issue 1202: Issue with loading addons that did not initialize correctly
// ZAP: 2014/08/14 Catch Exceptions thrown by extensions when stopping them
// ZAP: 2014/08/14 Issue 1309: NullPointerExceptions during a failed uninstallation of an add-on
// ZAP: 2014/10/07 Issue 1357: Hide unused tabs
// ZAP: 2014/10/09 Issue 1359: Added info logging for splash screen
// ZAP: 2014/10/25 Issue 1062: Added scannerhook to be loaded by an active scanner.
// ZAP: 2014/11/11 Issue 1406: Move online menu items to an add-on
// ZAP: 2014/11/21 Reviewed foreach loops and commented startup process for splash screen progress bar
// ZAP: 2015/01/04 Issue 1379: Not all extension's listeners are hooked during add-on installation
// ZAP: 2015/01/19 Remove online menus when removeMenu(View, ExtensionHook) is called.
// ZAP: 2015/01/19 Issue 1510: New Extension.postInit() method to be called once all extensions loaded
// ZAP: 2015/02/09 Issue 1525: Introduce a database interface layer to allow for alternative implementations
// ZAP: 2015/02/10 Issue 1208: Search classes/resources in add-ons declared as dependencies
// ZAP: 2015/04/09 Generify Extension.getExtension(Class) to avoid unnecessary casts
// ZAP: 2015/09/07 Start GUI on EDT
// ZAP: 2016/04/06 Fix layouts' issues
// ZAP: 2016/04/08 Hook ContextDataFactory/ContextPanelFactory 
// ZAP: 2016/05/30 Notification of installation status of the add-ons
// ZAP: 2016/05/30 Issue 2494: ZAP Proxy is not showing the HTTP CONNECT Request in history tab
// ZAP: 2016/08/18 Hook ApiImplementor

package org.parosproxy.paros.extension;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.parosproxy.paros.CommandLine;
import org.parosproxy.paros.common.AbstractParam;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.control.Control.Mode;
import org.parosproxy.paros.core.scanner.Scanner;
import org.parosproxy.paros.core.scanner.ScannerHook;
import org.parosproxy.paros.db.Database;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.DatabaseUnsupportedException;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.model.OptionsParam;
import org.parosproxy.paros.model.Session;
import org.parosproxy.paros.view.AbstractParamDialog;
import org.parosproxy.paros.view.AbstractParamPanel;
import org.parosproxy.paros.view.MainMenuBar;
import org.parosproxy.paros.view.View;
import org.parosproxy.paros.view.WorkbenchPanel;
import org.zaproxy.zap.control.AddOn;
import org.zaproxy.zap.extension.AddonFilesChangedListener;
import org.zaproxy.zap.extension.api.API;
import org.zaproxy.zap.extension.api.ApiImplementor;
import org.zaproxy.zap.extension.AddOnInstallationStatusListener;
import org.zaproxy.zap.model.ContextDataFactory;
import org.zaproxy.zap.view.ContextPanelFactory;

public class ExtensionLoader {

	//	private final List<Extension> extensionList = new ArrayList<>();
	//	private final Map<Class<? extends Extension>, Extension> extensionsMap = new HashMap<>();
	private final ExtensionList extensionList = new ExtensionList();
	private final Map<Extension, ExtensionHook> extensionHooks = new HashMap<>();
	private Model model = null;
	private HookProxyLinkerManager hookProxyLinkerManager;

	private View view = null;
	private static final Logger logger = Logger.getLogger(ExtensionLoader.class);

	public ExtensionLoader(Model model, View view) {
		this.model = model;
		this.view = view;
		hookProxyLinkerManager = new HookProxyLinkerManager(Control.getSingleton().getProxy());
	}

	public List<ExtensionHook> getExtensionHooks() {
		return new ArrayList<ExtensionHook>(extensionHooks.values());
	}

	public void addExtension(Extension extension) {
		extensionList.add(extension);
		extensionList.put(extension.getClass(), extension);
	}

	public Extension getExtension(String name) {
		return extensionList.getExtension(name);
	}

	//	public Extension getExtensionByClassName(String name) {
	//		return extensionList.getExtensionByClassName(name);
	//	}

	/**
	 * Tells whether or not an {@code Extension} with the given
	 * {@code extensionName} is enabled.
	 *
	 * @param extensionName the name of the extension
	 * @return {@code true} if the extension is enabled, {@code false}
	 * otherwise.
	 * @throws IllegalArgumentException if the {@code extensionName} is
	 * {@code null}.
	 * @see #getExtension(String)
	 * @see Extension
	 */
	public boolean isExtensionEnabled(String extensionName) {
		if (extensionName == null) {
			throw new IllegalArgumentException("Parameter extensionName must not be null.");
		}

		Extension extension = getExtension(extensionName);
		if (extension == null) {
			return false;
		}

		return extension.isEnabled();
	}

	/**
	 * Hooks (adds) the {@code ConnectRequestProxyListener}s of the loaded extensions to the given {@code proxy}.
	 * <p>
	 * <strong>Note:</strong> even if public this method is expected to be called only by core classes (for example,
	 * {@code Control}).
	 *
	 * @param proxy the local proxy
	 * @since 2.5.0
	 */

	// ZAP: Added support for site map listeners

	// ZAP: method called by the scanner to load all scanner hooks. 
	public void hookScannerHook(Scanner scan) {
		Iterator<ExtensionHook> iter = extensionHooks.values().iterator();
		while (iter.hasNext()) {
			ExtensionHook hook = iter.next();
			List<ScannerHook> scannerHookList = hook.getScannerHookList();

			for (ScannerHook scannerHook : scannerHookList) {
				try {
					scan.addScannerHook(scannerHook);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	public void sessionAboutToChangeAllPlugin(Session session) {
		logger.debug("sessionAboutToChangeAllPlugin");
		notifySessionEvent(listener -> listener.sessionAboutToChange(session));
	}

	public void sessionChangedAllPlugin(Session session) {
		logger.debug("sessionChangedAllPlugin");
		notifySessionEvent(listener -> listener.sessionChanged(session));
	}

	public void sessionScopeChangedAllPlugin(Session session) {
		logger.debug("sessionScopeChangedAllPlugin");
		notifySessionEvent(listener -> listener.sessionScopeChanged(session));
	}

	public void sessionModeChangedAllPlugin(Mode mode) {
		logger.debug("sessionModeChangedAllPlugin");
		notifySessionEvent(listener -> listener.sessionModeChanged(mode));
	}

	public void addonFilesAdded() {
		notifyAddOnEvent(listener -> listener.filesAdded());
	}

	public void addonFilesRemoved() {
		notifyAddOnEvent(listener -> listener.filesRemoved());
	}

	/**
	 * Notifies {@code Extension}s' {@code AddOnInstallationStatusListener}s that the given add-on was installed.
	 *
	 * @param addOn the add-on that was installed, must not be {@code null}
	 * @since 2.5.0
	 */
	public void addOnInstalled(AddOn addOn) {
		notifyInstallEvent(listener -> listener.addOnInstalled(addOn));
	}

	/**
	 * Notifies {@code Extension}s' {@code AddOnInstallationStatusListener}s that the given add-on was soft uninstalled.
	 *
	 * @param addOn the add-on that was soft uninstalled, must not be {@code null}
	 * @param successfully if the soft uninstallation was successful, that is, no errors occurred while uninstalling it
	 * @since 2.5.0
	 */
	public void addOnSoftUninstalled(AddOn addOn, boolean successfully) {
		notifyInstallEvent(listener -> listener.addOnSoftUninstalled(addOn, successfully));
	}

	/**
	 * Notifies {@code Extension}s' {@code AddOnInstallationStatusListener}s that the given add-on was uninstalled.
	 *
	 * @param addOn the add-on that was uninstalled, must not be {@code null}
	 * @param successfully if the uninstallation was successful, that is, no errors occurred while uninstalling it
	 * @since 2.5.0
	 */
	public void addOnUninstalled(AddOn addOn, boolean successfully) {
		notifyInstallEvent(listener -> listener.addOnUninstalled(addOn, successfully));
	}

	public void optionsChangedAllPlugin(OptionsParam options) {
		notifyEvent(listener -> listener.optionsChanged(options), hook -> hook.getOptionsChangedListenerList());
	}

	private void notifySessionEvent(Consumer<SessionChangedListener> executer) {
		notifyEvent(executer, hook -> hook.getSessionListenerList());
	}

	private void notifyAddOnEvent(Consumer<AddonFilesChangedListener> executer) {
		notifyEvent(executer, hook -> hook.getAddonFilesChangedListener());
	}

	private <T> void notifyEvent(Consumer<T> executer, Function<ExtensionHook, List<T>> hookToList) {//List<T> listenerList) {
		for (ExtensionHook hook : extensionHooks.values()) {
			List<T> listenerList = hookToList.apply(hook);
			for (T listener : listenerList) {
				try {
					if (listener != null) {
						executer.accept(listener);
					}
				} catch (Exception e) {
					logger.error("An error occurred while notifying: " + listener.getClass().getCanonicalName(), e);
				}
			}
		}
	}

	private void notifyInstallEvent(Consumer<AddOnInstallationStatusListener> executer) {
		notifyEvent(executer, hook -> hook.getAddOnInstallationStatusListeners());
	}

	/**
	 * Initialize and start all Extensions
	 * This function loops for all getExtensionCount() exts
	 * launching each specific initialization element (model, xml, view, hook, etc.)
	 */
	public void startLifeCycle() {

		// Percentages are passed into the calls as doubles
		if (view != null) {
			view.setSplashScreenLoadingCompletion(0.0);
			extensionList.setSplashScreenMethod(p -> view.addSplashScreenLoadingCompletion(p));
		}
		
		// Step 3: initialize all (slow)
		initAllExtension(5.0);
		// Step 4: initialize models (quick)
		initModelAllExtension(0.0);
		// Step 5: initialize xmls (quick)
		initXMLAllExtension(model.getSession(), model.getOptionsParam(), 0.0);
		// Step 6: initialize viewes (slow)
		initViewAllExtension(view, 10.0);
		// Step 7: initialize hooks (slowest)
		hookAllExtension(75.0);
		// Step 8: start all extensions(quick)
		startAllExtension(10.0);
	}

	/**
	 * Initialize a specific Extension
	 * @param ext the Extension that need to be initialized
	 * @throws DatabaseUnsupportedException 
	 * @throws DatabaseException 
	 */
	public void startLifeCycle(Extension ext) throws DatabaseException, DatabaseUnsupportedException {
		ext.init();
		ext.databaseOpen(model.getDb());
		ext.initModel(model);
		ext.initXML(model.getSession(), model.getOptionsParam());
		ext.initView(view);

		ExtensionHook extHook = new ExtensionHook(model, view);
		try {
			ext.hook(extHook);
			extensionHooks.put(ext, extHook);

			hookContextDataFactories(ext, extHook);
			hookApiImplementors(ext, extHook);

			if (view != null) {
				// no need to hook view if no GUI
				hookView(ext, view, extHook);
				hookMenu(view, extHook);
			}

			hookOptions(extHook);
			ext.optionsLoaded();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		ext.start();

		hookProxyLinkerManager.hookProxyListener();

		//        hookPersistentConnectionListeners(proxy, extHook.getPersistentConnectionListener());
		//        hookConnectRequestProxyListeners(proxy, extHook.getConnectRequestProxyListeners());
		hookProxyLinkerManager.hookPersistentConnectionListener();
		hookProxyLinkerManager.hookConnectRequestProxyListener();

		if (view != null) {
			//            hookSiteMapListeners(view.getSiteTreePanel(), extHook.getSiteMapListenerList());
			hookProxyLinkerManager.hookSiteMapListener(view.getSiteTreePanel());
		}
	}

	public void runCommandLine() {
		for (Extension ext : extensionList.getExtensions()) {
			if (ext instanceof CommandLineListener) {
				CommandLineListener listener = (CommandLineListener) ext;
				listener.execute(extensionHooks.get(ext).getCommandLineArgument());
			}
		}
	}

	public void databaseOpen(Database db) {
		extensionList.databaseOpen(db);
	}

	public void stopAllExtension() {
		extensionList.stopAllExtension();
	}

	// ZAP: Added the type argument.
	private void addParamPanel(List<AbstractParamPanel> panelList, AbstractParamDialog dialog) {
		String[] ROOT = {};
		for (AbstractParamPanel panel : panelList) {
			try {
				dialog.addParamPanel(ROOT, panel, true);

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private void removeParamPanel(List<AbstractParamPanel> panelList, AbstractParamDialog dialog) {
		for (AbstractParamPanel panel : panelList) {
			try {
				dialog.removeParamPanel(panel);

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		dialog.revalidate();
	}

	private void hookAllExtension(double progressFactor) {
		final double factorPerc = progressFactor / extensionList.getExtensionCount();
		List<Extension> extensions = extensionList.getExtensions();

		for (final Extension ext : extensions) {
			try {
				logger.info("Initializing " + ext.getDescription());
				final ExtensionHook extHook = new ExtensionHook(model, view);
				ext.hook(extHook);
				extensionHooks.put(ext, extHook);

				hookContextDataFactories(ext, extHook);
				hookApiImplementors(ext, extHook);

				if (view != null) {
					EventQueue.invokeAndWait(new Runnable() {

						@Override
						public void run() {
							// no need to hook view if no GUI
							hookView(ext, view, extHook);
							hookMenu(view, extHook);
							view.addSplashScreenLoadingCompletion(factorPerc);
						}
					});
				}

				hookOptions(extHook);
				ext.optionsLoaded();

			} catch (Throwable e) {
				// Catch Errors thrown by out of date extensions as well as Exceptions
				logger.error(e.getMessage(), e);
			}
		}
		// Call postInit for all extensions after they have all been initialized
		for (Extension ext : extensions) {
			try {
				ext.postInit();
			} catch (Throwable e) {
				// Catch Errors thrown by out of date extensions as well as Exceptions
				logger.error(e.getMessage(), e);
			}
		}

		if (view != null) {
			view.getMainFrame().getMainMenuBar().validate();
			view.getMainFrame().validate();
		}
	}

	private void hookContextDataFactories(Extension extension, ExtensionHook extHook) {
		for (ContextDataFactory contextDataFactory : extHook.getContextDataFactories()) {
			try {
				model.addContextDataFactory(contextDataFactory);
			} catch (Exception e) {
				logger.error("Error while adding a ContextDataFactory from " + extension.getClass().getCanonicalName(), e);
			}
		}
	}

	private void hookApiImplementors(Extension extension, ExtensionHook extHook) {
		for (ApiImplementor apiImplementor : extHook.getApiImplementors()) {
			try {
				API.getInstance().registerApiImplementor(apiImplementor);
			} catch (Exception e) {
				logger.error("Error while adding an ApiImplementor from " + extension.getClass().getCanonicalName(), e);
			}
		}
	}

	/**
	 * Hook command line listener with the command line processor
	 *
	 * @param cmdLine
	 * @throws java.lang.Exception
	 */
	public void hookCommandLineListener(CommandLine cmdLine) throws Exception {
		List<CommandLineArgument[]> allCommandLineList = new ArrayList<>();
		Map<String, CommandLineListener> extMap = new HashMap<>();
		for (Map.Entry<Extension, ExtensionHook> entry : extensionHooks.entrySet()) {
			ExtensionHook hook = entry.getValue();
			CommandLineArgument[] arg = hook.getCommandLineArgument();
			if (arg.length > 0) {
				allCommandLineList.add(arg);
			}

			Extension extension = entry.getKey();
			if (extension instanceof CommandLineListener) {
				CommandLineListener cli = (CommandLineListener) extension;
				List<String> exts = cli.getHandledExtensions();
				if (exts != null) {
					for (String ext : exts) {
						extMap.put(ext, cli);
					}
				}
			}
		}

		cmdLine.parse(allCommandLineList, extMap);
	}

	private void hookMenu(View view, ExtensionHook hook) {
		if (view == null) {
			return;
		}

		ExtensionHookMenu hookMenu = hook.getHookMenu();
		if (hookMenu == null) {
			return;
		}

		MainMenuBar menuBar = view.getMainFrame().getMainMenuBar();
		MenuHandler mh = new MenuHandler();

		// 2 menus at the back (Tools/Help)
		mh.addMenuHelper(menuBar, hookMenu.getNewMenus(), 2);

		mh.addMenuHelper(menuBar.getMenuFile(), hookMenu.getFile(), 2);
		mh.addMenuHelper(menuBar.getMenuTools(), hookMenu.getTools(), 2);
		mh.addMenuHelper(menuBar.getMenuEdit(), hookMenu.getEdit());
		mh.addMenuHelper(menuBar.getMenuView(), hookMenu.getView());
		mh.addMenuHelper(menuBar.getMenuAnalyse(), hookMenu.getAnalyse());
		mh.addMenuHelper(menuBar.getMenuHelp(), hookMenu.getHelpMenus());
		mh.addMenuHelper(menuBar.getMenuReport(), hookMenu.getReportMenus());
		mh.addMenuHelper(menuBar.getMenuOnline(), hookMenu.getOnlineMenus());

		mh.addMenuHelper(view.getPopupList(), hookMenu.getPopupMenus());
	}
	private void hookOptions(ExtensionHook hook) {
		List<AbstractParam> list = hook.getOptionsParamSetList();
		for (AbstractParam paramSet : list) {
			try {
				model.getOptionsParam().addParamSet(paramSet);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private void unloadOptions(ExtensionHook hook) {
		List<AbstractParam> list = hook.getOptionsParamSetList();
		for (AbstractParam paramSet : list) {
			try {
				model.getOptionsParam().removeParamSet(paramSet);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private void hookView(Extension extension, View view, ExtensionHook hook) {
		if (view == null) {
			return;
		}

		ExtensionHookView pv = hook.getHookView();
		if (pv == null) {
			return;
		}

		for (ContextPanelFactory contextPanelFactory : pv.getContextPanelFactories()) {
			try {
				view.addContextPanelFactory(contextPanelFactory);
			} catch (Exception e) {
				logger.error("Error while adding a ContextPanelFactory from " + extension.getClass().getCanonicalName(), e);
			}
		}

		view.getWorkbench().addPanels(pv.getSelectPanel(), WorkbenchPanel.PanelType.SELECT);
		view.getWorkbench().addPanels(pv.getWorkPanel(), WorkbenchPanel.PanelType.WORK);
		view.getWorkbench().addPanels(pv.getStatusPanel(), WorkbenchPanel.PanelType.STATUS);

		addParamPanel(pv.getSessionPanel(), view.getSessionDialog());
		addParamPanel(pv.getOptionsPanel(), view.getOptionsDialog(""));
	}

	private void removeView(Extension extension, View view, ExtensionHook hook) {
		if (view == null) {
			return;
		}

		ExtensionHookView pv = hook.getHookView();
		if (pv == null) {
			return;
		}

		for (ContextPanelFactory contextPanelFactory : pv.getContextPanelFactories()) {
			try {
				view.removeContextPanelFactory(contextPanelFactory);
			} catch (Exception e) {
				logger.error("Error while removing a ContextPanelFactory from " + extension.getClass().getCanonicalName(), e);
			}
		}

		view.getWorkbench().removePanels(pv.getSelectPanel(), WorkbenchPanel.PanelType.SELECT);
		view.getWorkbench().removePanels(pv.getWorkPanel(), WorkbenchPanel.PanelType.WORK);
		view.getWorkbench().removePanels(pv.getStatusPanel(), WorkbenchPanel.PanelType.STATUS);

		removeParamPanel(pv.getSessionPanel(), view.getSessionDialog());
		removeParamPanel(pv.getOptionsPanel(), view.getOptionsDialog(""));
	}

	public void removeStatusPanel(AbstractPanel panel) {
		if (!View.isInitialised()) {
			return;
		}

		View.getSingleton().getWorkbench().removePanel(panel, WorkbenchPanel.PanelType.STATUS);
	}

	public void removeOptionsPanel(AbstractParamPanel panel) {
		if (!View.isInitialised()) {
			return;
		}

		View.getSingleton().getOptionsDialog("").removeParamPanel(panel);
	}

	public void removeOptionsParamSet(AbstractParam params) {
		model.getOptionsParam().removeParamSet(params);
	}

	public void removeWorkPanel(AbstractPanel panel) {
		if (!View.isInitialised()) {
			return;
		}

		View.getSingleton().getWorkbench().removePanel(panel, WorkbenchPanel.PanelType.WORK);
	}

	/**
	 * Init all extensions
	 */
	private void initAllExtension(double progressFactor) {
		extensionList.initExtensions(ext -> {
			try {
				ext.init();
				ext.databaseOpen(Model.getSingleton().getDb());
			} catch (Exception e) {
				e.printStackTrace();
			}
		},  progressFactor);
	}

	public void initXMLAllExtension(Session session, OptionsParam params, double progressFactor) {
		extensionList.initExtensions(ext -> ext.initXML(session, params), progressFactor);
	}
	
	/**
	 * Init all extensions with the same Model
	 * @param model the model to apply to all extensions
	 */
	private void initModelAllExtension(double progressFactor) {
		extensionList.initExtensions(ext -> ext.initModel(model), progressFactor);
	}

	/**
	 * Init all extensions with the same View
	 * @param view the View that need to be applied
	 */
	private void initViewAllExtension(final View view, double progressFactor) {
		if (view == null) {
			return;
		}

		final double factorPerc = progressFactor / getExtensionCount();
		for (final Extension ext : extensionList.getExtensions()) {
			try {
				EventQueue.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						ext.initView(view);
						view.addSplashScreenLoadingCompletion(factorPerc);
					}
				});

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public void startAllExtension(double progressFactor) {
		extensionList.initExtensions(ext -> ext.start(), progressFactor);
	}

	/**
	 * Removes an extension from internal list. As a result listeners added via
	 * the {@link ExtensionHook} object are unregistered.
	 *
	 * @param extension
	 * @param hook
	 */
	public void removeExtension(Extension extension, ExtensionHook hook) {
		extensionList.remove(extension);

		if (hook == null) {
			logger.info("ExtensionHook is null for \"" + extension.getClass().getCanonicalName()
					+ "\" the hooked objects will not be automatically removed.");
			return;
		}

		// by removing the ExtensionHook object,
		// the following listeners are no longer informed:
		// 		* SessionListeners
		// 		* OptionsChangedListeners
		extensionHooks.values().remove(hook);

		unloadOptions(hook);

		hookProxyLinkerManager.removeAllListeners(hook);

		for (ContextDataFactory contextDataFactory : hook.getContextDataFactories()) {
			try {
				model.removeContextDataFactory(contextDataFactory);
			} catch (Exception e) {
				logger.error("Error while removing a ContextDataFactory from " + extension.getClass().getCanonicalName(), e);
			}
		}

		for (ApiImplementor apiImplementor : hook.getApiImplementors()) {
			try {
				API.getInstance().removeApiImplementor(apiImplementor);
			} catch (Exception e) {
				logger.error("Error while removing an ApiImplementor from " + extension.getClass().getCanonicalName(), e);
			}
		}

		removeViewInEDT(extension, hook);
	}

	private void removeViewInEDT(final Extension extension, final ExtensionHook hook) {
		if (view == null) {
			return;
		}

		if (EventQueue.isDispatchThread()) {
			removeView(extension, view, hook);
			MenuHandler mh = new MenuHandler();
			mh.removeMenu(view, hook.getHookMenu());
		} else {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					removeViewInEDT(extension, hook);
				}
			});
		}
	}

	public int getExtensionCount() {
		return extensionList.getExtensionCount();
	}

	public Extension getExtension(int i) {
		return extensionList.get(i);
	}

	public <T extends Extension> T getExtension(Class<T> clazz) {
		return extensionList.getExtension(clazz);
	}

	public void destroyAllExtension() {
		extensionList.destroyAllExtensions();
	}

	public List<String> getUnsavedResources() {
		return extensionList.getUnsavedResources();
	}

	public List<String> getActiveActions() {
		return extensionList.getActiveActions();
	}
}
