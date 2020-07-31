package com.opentach.client.comp;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.plaf.TabbedPaneUI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.manager.TabbedFormManager.ButtonTabComponent;

public class TabbedFormPane extends JTabbedPane implements Internationalization, MouseMotionListener {

	private static final Logger	logger						= LoggerFactory.getLogger(TabbedFormPane.class);
	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 */
	private static String		NAME						= "FormTabbedPane";
	private static final String	UICLASSID					= "FormTabbedPaneUI";

	protected JPopupMenu		tabOptionMenu;
	protected JMenuItem			menuClose;
	protected JMenuItem			menuCloseOthers;
	protected JMenuItem			menuCloseAll;

	protected String			menuCloseBundleKey			= "tabbedformmanager.close";
	protected String			menuCloseOthersBundleKey	= "tabbedformmanager.closeothers";
	protected String			menuCloseAllBundleKey		= "tabbedformmanager.closeall";

	protected int				selectedMenuIndex			= -1;

	protected ResourceBundle	bundle;

	protected List<JFrame>		frameList;

	protected boolean			dragToFrame					= true;

	public TabbedFormPane() {
		super();
		KeyStroke altF = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, java.awt.event.InputEvent.CTRL_DOWN_MASK, false);
		Action control_tab_action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = TabbedFormPane.this.getSelectedIndex();
				int total = TabbedFormPane.this.getTabCount();
				index++;
				if (index >= total) {
					index = 0;
				}
				TabbedFormPane.this.setSelectedIndex(index);
			}
		};
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(altF, "CONTROL_TAB");
		this.getActionMap().put("CONTROL_TAB", control_tab_action);
		// this.setTransferHandler(new FormTabbedTransferHandler(this));
		this.addMouseMotionListener(this);
		ToolTipManager.sharedInstance().registerComponent(this);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					TabbedPaneUI ui = TabbedFormPane.this.getUI();
					int tab = ui.tabForCoordinate(TabbedFormPane.this, e.getX(), e.getY());
					if (tab > 0) {
						TabbedFormPane.this.selectedMenuIndex = tab;
						TabbedFormPane.this.showTabOptionMenu(e);
					}

				}
			}
		});
	}

	@Override
	public String getName() {
		return TabbedFormPane.NAME;
	}

	@Override
	public String getUIClassID() {
		if (ApplicationManager.useOntimizePlaf) {
			return TabbedFormPane.UICLASSID;
		}
		return super.getUIClassID();
	}

	@Override
	public void addTab(String title, Component component) {
		int index = this.getTabCount();
		super.addTab(title, component);
		if (index > 0) {
			this.setTabComponentAt(index, new ButtonTabComponent(this));
		} else {
			this.setTabComponentAt(index, new ButtonTabComponent(this, false));
		}
	}

	@Override
	public void setTabComponentAt(int index, Component component) {
		super.setTabComponentAt(index, component);
	}

	@Override
	public void setComponentLocale(Locale l) {

	}

	@Override
	public void setResourceBundle(ResourceBundle resourceBundle) {
		this.bundle = resourceBundle;
		this.applyResourceBundle();
	}

	@Override
	public Vector getTextsToTranslate() {
		return null;
	}

	public boolean isDragToFrame() {
		return this.dragToFrame;
	}

	public void setDragToFrame(boolean dragToFrame) {
		this.dragToFrame = dragToFrame;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if ((this.getTransferHandler() != null) && this.isDragToFrame()) {
			TransferHandler th = this.getTransferHandler();
			th.exportAsDrag(this, e, TransferHandler.MOVE);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	protected void applyResourceBundle() {
		if (this.tabOptionMenu != null) {
			this.menuClose.setText(ApplicationManager.getTranslation(this.menuCloseBundleKey, this.bundle));
			this.menuCloseAll.setText(ApplicationManager.getTranslation(this.menuCloseAllBundleKey, this.bundle));
			this.menuCloseOthers.setText(ApplicationManager.getTranslation(this.menuCloseOthersBundleKey, this.bundle));
		}

		// int tabCount = this.getTabCount();
		// for (int i = 0; i < tabCount; i++) {
		// Component component = this.getComponentAt(i);
		// if (component instanceof IDetailForm) {
		// Form currentForm = ((IDetailForm) component).getForm();
		// currentForm.setResourceBundle(this.bundle);
		// }
		// }

		if (this.frameList != null) {
			for (JFrame current : this.frameList) {
				Component[] components = current.getContentPane().getComponents();
				for (Component component : components) {
					if (component instanceof Internationalization) {
						((Internationalization) component).setResourceBundle(this.bundle);
					}
				}
			}
		}
	}

	protected void createTabOptionMenu() {
		this.tabOptionMenu = new JPopupMenu();
		this.menuClose = new JMenuItem(this.menuCloseBundleKey);
		this.menuCloseAll = new JMenuItem(this.menuCloseAllBundleKey);
		this.menuCloseOthers = new JMenuItem(this.menuCloseOthersBundleKey);

		this.tabOptionMenu.add(this.menuClose);
		this.tabOptionMenu.add(this.menuCloseOthers);
		this.tabOptionMenu.add(this.menuCloseAll);

		this.applyResourceBundle();

		this.menuCloseAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int tabCount = TabbedFormPane.this.getTabCount();
				if (tabCount > 1) {
					for (int i = tabCount - 1; i > 0; i--) {
						TabbedFormPane.this.remove(i);
					}
				}
			}
		});

		this.menuClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (TabbedFormPane.this.selectedMenuIndex > 0) {
					TabbedFormPane.this.remove(TabbedFormPane.this.selectedMenuIndex);
				}
			}
		});

		this.menuCloseOthers.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int tabCount = TabbedFormPane.this.getTabCount();
				if ((tabCount > 2) && (TabbedFormPane.this.selectedMenuIndex > 0)) {
					Component selectedComponent = TabbedFormPane.this.getTabComponentAt(TabbedFormPane.this.selectedMenuIndex);
					for (int i = tabCount - 1; i > 0; i--) {
						Component currentComponent = TabbedFormPane.this.getTabComponentAt(i);
						if (selectedComponent.equals(currentComponent)) {
							continue;
						} else {
							TabbedFormPane.this.remove(i);
						}
					}
				}
			}
		});

	}

	@Override
	public void remove(int index) {
		super.remove(index);
		if (this.getTabCount() == 1) {
			Container tabComponentAt = (Container) this.getComponentAt(0);
			super.remove(0);
			JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
			frame.setContentPane(tabComponentAt);
		}
	}

	public void showTabOptionMenu(MouseEvent e) {
		if (e.getSource() instanceof TabbedFormPane) {
			if (this.tabOptionMenu == null) {
				this.createTabOptionMenu();
			}
			this.tabOptionMenu.show((Component) e.getSource(), e.getX(), e.getY());
		}
	}

	// protected Frame createTabAtFrame(int index) {
	// TabbedDetailForm detailForm = (TabbedDetailForm) this.getComponentAt(index);
	// EJFrame frame = new EJFrame(detailForm.getTitle());
	// frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	// // Retrieve Images
	// Window w = SwingUtilities.getWindowAncestor(this);
	// if (w != null) {
	// frame.setIconImages(w.getIconImages());
	// }
	// detailForm.hideDetailForm();
	// frame.getContentPane().add(detailForm);
	// frame.pack();
	// Point p = this.getLocation();
	// SwingUtilities.convertPointToScreen(p, this);
	// frame.setLocation(p);
	// ApplicationManager.center(frame);
	//
	// if (this.frameList == null) {
	// this.frameList = new ArrayList<JFrame>();
	// }
	// this.frameList.add(frame);
	// frame.addWindowListener(new WindowAdapter() {
	// @Override
	// public void windowClosing(WindowEvent e) {
	// if (FormTabbedPane.this.frameList.contains(e.getSource())) {
	// FormTabbedPane.this.frameList.remove(e.getSource());
	// }
	// }
	//
	// @Override
	// public void windowClosed(WindowEvent e) {}
	// });
	// return frame;
	// }
}