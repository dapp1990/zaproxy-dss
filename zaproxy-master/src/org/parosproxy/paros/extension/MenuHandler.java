package org.parosproxy.paros.extension;

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.parosproxy.paros.view.MainMenuBar;
import org.parosproxy.paros.view.View;

public class MenuHandler {

	public void addMenuHelper(JMenu menu, List<JMenuItem> items) {
		addMenuHelper(menu, items, 0);
	}

	public void addMenuHelper(JMenuBar menuBar, List<JMenuItem> items, int existingCount) {
		for (JMenuItem item : items) {
			if (item != null) {
				menuBar.add(item, menuBar.getMenuCount() - existingCount);
			}
		}
		menuBar.revalidate();
	}

	public void addMenuHelper(JMenu menu, List<JMenuItem> items, int existingCount) {
		for (JMenuItem item : items) {
			if (item != null) {
				if (item == ExtensionHookMenu.MENU_SEPARATOR) {
					menu.addSeparator();
					continue;
				}

				menu.add(item, menu.getItemCount() - existingCount);
			}
		}

		menu.revalidate();
	}

	public void addMenuHelper(List<JMenuItem> menuList, List<JMenuItem> items) {
		for (JMenuItem item : items) {
			if (item != null) {
				menuList.add(item);
			}
		}
	}

	public void removeMenu(View view, ExtensionHookMenu hookMenu) {
		if (view == null) {
			return;
		}

		if (hookMenu == null) {
			return;
		}

		MainMenuBar menuBar = view.getMainFrame().getMainMenuBar();

		// clear up various menus
		removeMenuHelper(menuBar, hookMenu.getNewMenus());

		removeMenuHelper(menuBar.getMenuFile(), hookMenu.getFile());
		removeMenuHelper(menuBar.getMenuTools(), hookMenu.getTools());
		removeMenuHelper(menuBar.getMenuEdit(), hookMenu.getEdit());
		removeMenuHelper(menuBar.getMenuView(), hookMenu.getView());
		removeMenuHelper(menuBar.getMenuAnalyse(), hookMenu.getAnalyse());
		removeMenuHelper(menuBar.getMenuHelp(), hookMenu.getHelpMenus());
		removeMenuHelper(menuBar.getMenuReport(), hookMenu.getReportMenus());
		removeMenuHelper(menuBar.getMenuOnline(), hookMenu.getOnlineMenus());

		removeMenuHelper(view.getPopupList(), hookMenu.getPopupMenus());

		view.refreshTabViewMenus();
	}

	public void removeMenuHelper(MainMenuBar menuBar, List<JMenuItem> items) {
		for (JMenuItem item : items) {
			removeMenuHelper(menuBar, item);
		}
		//        menuBar.revalidate();
	}

	public void removeMenuHelper(MainMenuBar menuBar, JMenuItem item) {
		if (item != null) {
			menuBar.remove(item);
		}
		menuBar.revalidate();
	}

	public void removeMenuHelper(JMenu menu, List<JMenuItem> items) {
		for (JMenuItem item : items) {
			if (item != null) {
				menu.remove(item);
			}
		}
		menu.revalidate();
	}

	public void removeMenuHelper(List<JMenuItem> menuList, List<JMenuItem> items) {
		for (JMenuItem item : items) {
			if (item != null) {
				menuList.remove(item);
			}
		}
	}
	//    
	//    public void removePopupMenuItem(ExtensionPopupMenuItem popupMenuItem, JMenuBar menu) {
	//        View.getSingleton().getPopupList().remove(popupMenuItem);
	//    }

}
