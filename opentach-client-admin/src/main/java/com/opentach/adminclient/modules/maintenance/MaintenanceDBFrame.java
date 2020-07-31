package com.opentach.adminclient.modules.maintenance;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JProgressBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.Scroll;
import com.ontimize.gui.field.DataLabel;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.util.swing.EJFrame;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.maintenance.IMaintenanceService;
import com.opentach.common.maintenance.MaintenanceStatus;
import com.opentach.common.maintenance.MaintenanceStatus.MaintenanceStatusType;
import com.opentach.common.maintenance.MaintenanceStatusTask;
import com.utilmize.client.gui.Column;
import com.utilmize.client.gui.UScroll;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.UDateDataField;
import com.utilmize.client.gui.field.ULabel;
import com.utilmize.client.gui.field.UTextDataField;

public class MaintenanceDBFrame extends EJFrame {

	private static final Logger								logger	= LoggerFactory.getLogger(MaintenanceDBFrame.class);
	private UTextDataField									backupFolder;
	private UDateDataField									filterDate;
	private final Map<MaintenanceStatusType, StatusPanel>	statusPanels;
	private UButton											button;
	private DataLabel										statusLabel;

	public MaintenanceDBFrame() throws HeadlessException {
		super("Maintenance");
		this.statusPanels = new HashMap<>();
		this.build();
		this.setSize(800, 700);
		this.setLocationRelativeTo(null);
		this.startStatusCheckThread();
	}

	private void build() {
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(this.buildOptionsPanel(), BorderLayout.NORTH);
		this.getContentPane().add(this.buildStatusPanel(), BorderLayout.CENTER);
		this.setResouceBundle(this.getContentPane());
	}

	private void startStatusCheckThread() {
		Thread thread = new Thread(new StatusCheckTask(), "Maintentance status thread");
		thread.setDaemon(true);
		thread.start();
	}

	private void setResouceBundle(Container contentPane) {
		for (Component comp : contentPane.getComponents()) {
			if (comp instanceof Internationalization) {
				((Internationalization) comp).setResourceBundle(ApplicationManager.getApplicationBundle());
			}
			if (comp instanceof Container) {
				this.setResouceBundle((Container) comp);
			}
		}
	}

	private Component buildOptionsPanel() {
		this.backupFolder = new UTextDataField(EntityResultTools.keysvalues(//
				"attr", "backupfolder",//
				"dim", "text", //
				"labelsize", "40",//
				"align", "left" //
				));
		this.filterDate = new UDateDataField(EntityResultTools.keysvalues( //
				"attr", "filterdate", //
				"labelsize", "40", //
				"align", "left" //
				));
		this.button = new UButton(EntityResultTools.keysvalues( //
				"key", "doit", //
				"text", "begin", //
				"align", "right" //
				));
		this.button.addActionListener(new StartTaskActionListener());
		Column column = new Column(EntityResultTools.keysvalues(//
				"title", "params"));
		column.add(this.backupFolder, this.backupFolder.getConstraints(column.getLayout()));
		column.add(this.filterDate, this.filterDate.getConstraints(column.getLayout()));
		column.add(this.button, this.button.getConstraints(column.getLayout()));
		return column;
	}

	private Component buildStatusPanel() {
		Column columnMain = new Column(EntityResultTools.keysvalues(//
				"title", "status"));
		Scroll scroll = new UScroll(EntityResultTools.keysvalues(//
				"unitincrement", "30"//
				));
		Column column = new Column(EntityResultTools.keysvalues());
		this.statusLabel = new DataLabel(EntityResultTools.keysvalues(//
				"attr", "running",//
				"align", "left",//
				"nodatatext", "Estado: "//
				));
		for (MaintenanceStatusType type : MaintenanceStatusType.values()) {
			StatusPanel statusPanel = new StatusPanel(type);
			this.statusPanels.put(type, statusPanel);
			column.add(statusPanel, statusPanel.getConstraints(column.getLayout()));
		}
		columnMain.add(this.statusLabel, this.statusLabel.getConstraints(columnMain.getLayout()));
		columnMain.add(scroll, scroll.getConstraints(columnMain.getLayout()));
		scroll.add(column, column.getConstraints(scroll.getLayout()));
		return columnMain;
	}

	public static class StatusPanel extends Column {
		private final MaintenanceStatusType	type;
		private JProgressBar				progressBar;
		private ULabel						label;

		public StatusPanel(MaintenanceStatusType type) {
			super(EntityResultTools.keysvalues(//
					"title", type.toString()));
			this.type = type;
			this.build();
		}

		public void build() {
			this.progressBar = new JProgressBar(0, 10000);
			this.progressBar.setStringPainted(true);
			this.label = new ULabel(EntityResultTools.keysvalues(//
					"align", "left"));
			this.add(this.progressBar, new GridBagConstraints(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, 1, 1, 1f, 0f, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(0, 30, 0, 30), 0, 0));
			this.add(this.label, this.label.getConstraints(this.getLayout()));
		}

		public void updateStatus(MaintenanceStatusTask status) {
			this.progressBar.setValue(status == null ? 0 : ((int) (status.getPercent() * 100)));
			this.label.setText(status == null ? "" : ApplicationManager.getTranslation(status.getCurrentStatus(), ApplicationManager.getApplicationBundle(),
					status.getCurrentStatusParameters()));
		}
	}

	public class StatusCheckTask implements Runnable {

		private MaintenanceStatus	status;

		@Override
		public void run() {
			while (true) {
				OpentachClientLocator ocl = (OpentachClientLocator) ApplicationManager.getApplication().getReferenceLocator();
				try {
					if (MaintenanceDBFrame.this.isVisible()) {
						this.status = ocl.getRemoteService(IMaintenanceService.class).getStatus(ocl.getSessionId());
						this.updateInfo(this.status);
					}
				} catch (Exception error) {
					MaintenanceDBFrame.logger.error(null, error);
				} finally {
					try {
						Thread.sleep(500);
					} catch (InterruptedException error) {
						MaintenanceDBFrame.logger.error(null, error);
					}
				}
			}
		}

		private void updateInfo(MaintenanceStatus status) {
			MaintenanceDBFrame.this.button.setEnabled(status.isAvailable());
			MaintenanceDBFrame.this.statusLabel.setValue(status.isAvailable() ? "Ejecutando: no" : "Ejecutando: si");
			for (MaintenanceStatusType type : MaintenanceStatusType.values()) {
				StatusPanel statusPanel = MaintenanceDBFrame.this.statusPanels.get(type);
				if (statusPanel != null) {
					statusPanel.updateStatus(status == null ? null : status.getStatus(type));
				}
			}
		}
	}

	public class StartTaskActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			OpentachClientLocator ocl = (OpentachClientLocator) ApplicationManager.getApplication().getReferenceLocator();
			try {
				ocl.getRemoteService(IMaintenanceService.class).doClear((String) MaintenanceDBFrame.this.backupFolder.getValue(),
						(Timestamp) MaintenanceDBFrame.this.filterDate.getValue(), ocl.getSessionId());
			} catch (Exception ex) {
				MessageManager.getMessageManager().showExceptionMessage(ex, MaintenanceDBFrame.logger);
			}
		}
	}
}
