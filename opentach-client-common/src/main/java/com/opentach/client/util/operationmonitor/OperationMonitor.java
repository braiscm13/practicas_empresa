package com.opentach.client.util.operationmonitor;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ApplicationManager.ExtOpThreadsMonitorComponent;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.gui.MainApplication;
import com.ontimize.gui.MainApplication.StatusBar;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.util.swing.EJFrame;

public final class OperationMonitor {

	private static String				ExtOpThreadsMonitor	= "ExtOpThreadsMonitor";
	private static ExtOpThreadsMonitor	monitor				= null;

	private OperationMonitor() {
		super();
	}

	/**
	 * Returns the ExtOpThreadsMonitor for the application. If no ExtOpThreadsMonitor has been set, the method creates one and sets it to the application.
	 *
	 * @param component
	 *            any application component
	 * @return the ExtOpThreadsMonitor
	 */
	public static ExtOpThreadsMonitor getExtOpThreadsMonitor() {
		if (OperationMonitor.monitor == null) {
			OperationMonitor.monitor = new ExtOpThreadsMonitor("applicationmanager.current_operations");
			OperationMonitor.monitor.setContentPane(new ExtOpThreadsMonitorComponent());
			if (ApplicationManager.getApplication() != null) {
				OperationMonitor.monitor.setResourceBundle(ApplicationManager.getApplication().getResourceBundle());
			}
			OperationMonitor.monitor.pack();
			ApplicationManager.setLocationSouthEast(OperationMonitor.monitor);
			OperationMonitor.registerExtOpThreadsMonitor();
		}
		return OperationMonitor.monitor;
	}

	/**
	 * Sets a ExtOpThreadsMonitor to the application. Users can see this monitor using the status bar icon on the right.
	 *
	 * @param op
	 *            the operation thread monitor
	 */
	public static void registerExtOpThreadsMonitor() {
		if ((OperationMonitor.monitor != null) && (ApplicationManager.getApplication() instanceof MainApplication)) {
			MainApplication application = (MainApplication) ApplicationManager.getApplication();
			StatusBar statusBar = application.getStatusBar();
			if ((statusBar != null) && (statusBar.getIconLabel(OperationMonitor.ExtOpThreadsMonitor) == null)) {
				SwingUtilities.invokeLater((Runnable) () -> {
					application.addStatusBarIcon(OperationMonitor.ExtOpThreadsMonitor, ApplicationManager.getDefaultExtOpThreadsMonitorIcon());
					statusBar.getIconLabel(OperationMonitor.ExtOpThreadsMonitor).setCursor(new Cursor(Cursor.HAND_CURSOR));
					application.addMouseListenerToStatusIcon(OperationMonitor.ExtOpThreadsMonitor, new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							OperationMonitor.getExtOpThreadsMonitor().setVisible(true);
						}
					});
				});
			}
		}
	}

	/**
	 * Class that represents a monitor for the Operation Threads. With this class, the evolution of the threads can be monitored.
	 */
	public static class ExtOpThreadsMonitor extends EJFrame implements Internationalization {
		protected ExtOpThreadsMonitorComponent	eOPC		= null;

		protected String						titleKey	= null;

		/**
		 * Creates a ExtOpThreadsMonitor for a dialog
		 *
		 * @param dialog
		 *            the window
		 * @param title
		 *            the title
		 * @param modal
		 *            if true, the window will be modal
		 */
		public ExtOpThreadsMonitor(String title) {
			super(title);
			this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.setAlwaysOnTop(true);
			this.titleKey = title;
		}

		/**
		 * The container can be a ExtOpThreadsMonitorComponent. See {@see JDialog#setContentPane(Container)}
		 */
		@Override
		public void setContentPane(Container container) {
			super.setContentPane(container);
			if (container instanceof ExtOpThreadsMonitorComponent) {
				this.eOPC = (ExtOpThreadsMonitorComponent) container;
			}
		}

		/**
		 * Adds a thread to the monitor.
		 *
		 * @param extendedOperationThread
		 *            a new thread
		 */
		public void addExtOpThread(ExtendedOperationThread extendedOperationThread) {
			this.eOPC.addExtOpThread(extendedOperationThread);
		}

		@Override
		public void setResourceBundle(ResourceBundle resourceBundle) {
			this.setTitle(ApplicationManager.getTranslation(this.titleKey, resourceBundle));
			this.eOPC.setResourceBundle(resourceBundle);
		}

		@Override
		public void setComponentLocale(Locale l) {

		}

		/**
		 * Unused
		 */
		@Override
		public Vector getTextsToTranslate() {
			return null;
		}

		/**
		 * Returns the threads that are alive.
		 *
		 * @return the number of threads alive
		 */
		public int getAliveThreadsCount() {
			return this.eOPC.getAliveThreadsCount();
		}

	}
}
