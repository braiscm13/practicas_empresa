package com.opentach.client.sessionstatus;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imlabs.client.laf.common.components.CustomMenuGroupGUI;
import com.ontimize.gui.ApToolBarButton;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.field.NavigationMenu.MenuItem;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.common.sessionstatus.StatisticsDto;

public final class StatisticsRecollector implements Runnable {

	private static final Logger				logger	= LoggerFactory.getLogger(StatisticsRecollector.class);

	private static StatisticsRecollector	recolector;

	public static StatisticsRecollector getInstance() {
		if (StatisticsRecollector.recolector == null) {
			StatisticsRecollector.recolector = new StatisticsRecollector();
		}
		return StatisticsRecollector.recolector;
	}

	private final List<StatisticsDto>			statistics;
	private final Thread						processThread;
	private final LinkedBlockingQueue<AWTEvent>	eventQueue;

	private StatisticsRecollector() {
		super();
		this.statistics = new ArrayList<>();
		this.eventQueue = new LinkedBlockingQueue<AWTEvent>();
		this.processThread = new Thread(this);
		this.processThread.setDaemon(true);
	}

	public void start() {
		this.processThread.start();
		this.install();
	}

	private void install() {
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			@Override
			public void eventDispatched(AWTEvent event) {
				StatisticsRecollector.this.eventQueue.offer(event);
			}
		}, AWTEvent.MOUSE_EVENT_MASK);
	}

	public List<StatisticsDto> getStatistics() {
		return this.statistics;
	}

	public void onStatusUpdate() {
		this.statistics.clear();
	}

	/**
	 * Adds the statistic.
	 *
	 * @param formManager
	 *            the form manager
	 * @param form
	 *            the form
	 * @param action
	 *            the action
	 */
	protected void addStatistic(String formManager, String form, String action) {
		StatisticsDto statistic = new StatisticsDto(formManager, form, action);
		int indexOf = this.statistics.indexOf(statistic);
		if (indexOf < 0) {
			this.statistics.add(statistic);
		} else {
			this.statistics.get(indexOf).add();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				AWTEvent event = this.eventQueue.take();
				if (event instanceof MouseEvent) {
					MouseEvent mEvent = (MouseEvent) event;
					if ((mEvent.getButton() == MouseEvent.BUTTON1) && (mEvent.getID() == MouseEvent.MOUSE_RELEASED)) {
						String formName = null;
						String fManagerName = null;
						String compName = null;

						Object source = event.getSource();

						IdentifiedElement idElement = (IdentifiedElement) SwingUtilities.getAncestorOfClass(IdentifiedElement.class, (Component) source);
						IFormManager formManager = (IFormManager) SwingUtilities.getAncestorOfClass(IFormManager.class, (Component) source);
						Form form = (Form) SwingUtilities.getAncestorOfClass(Form.class, (Component) source);

						if ((source instanceof CustomMenuGroupGUI) || (source instanceof com.ontimize.gui.MenuItem) || (source instanceof ApToolBarButton)) {
							String tmp = null;
							if (source instanceof CustomMenuGroupGUI) {
								MenuItem menuItemAt = ((CustomMenuGroupGUI) source).getMenuItemAt(mEvent.getX(), mEvent.getY());
								if (menuItemAt != null) {
									tmp = menuItemAt.getManager();
								}
							} else if (source instanceof com.ontimize.gui.MenuItem) {
								tmp = String.valueOf(((com.ontimize.gui.MenuItem) source).getAttribute());
							} else if (source instanceof ApToolBarButton) {
								tmp = String.valueOf(((ApToolBarButton) source).getAttribute());
							}
							if (tmp != null) {
								fManagerName = tmp;
								formManager = ApplicationManager.getApplication().getFormManager(tmp);
								if (formManager != null) {
									form = formManager.getActiveForm();
								}
							}
						}

						if ((formManager == null) && (form != null)) {
							formManager = form.getFormManager();
						}
						if ((form == null) && (formManager != null)) {
							form = formManager.getActiveForm();
						}
						formName = form == null ? null : form.getArchiveName();
						fManagerName = ObjectTools.coalesce(fManagerName, (formManager == null) ? null : formManager.getId());
						compName = idElement == null ? null : String.valueOf(idElement.getAttribute());

						if (source instanceof Button) {
							compName = ((Button) source).getKey();
						}

						if (fManagerName != null) {
							this.addStatistic(fManagerName, formName, compName);
						}
						// MenuGroupGUI
						// UMenuItem
						// Tree
						// UTable (doble click)
						// JButton

					}
				}
			} catch (Throwable error) {
				StatisticsRecollector.logger.error(null, error);
			}
		}

	}

}
