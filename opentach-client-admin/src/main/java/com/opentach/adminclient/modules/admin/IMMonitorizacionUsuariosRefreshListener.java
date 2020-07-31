package com.opentach.adminclient.modules.admin;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.IntegerDataField;
import com.ontimize.gui.table.Table;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class IMMonitorizacionUsuariosRefreshListener extends AbstractValueChangeListener {

	private static final Logger	logger	= LoggerFactory.getLogger(IMMonitorizacionUsuariosRefreshListener.class);

	private IntegerDataField	intervalDataField;
	private Table				table;
	private String				intervalAttr;
	private String				tableAttr;
	private Thread				thread;

	public IMMonitorizacionUsuariosRefreshListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.intervalAttr = (String) params.get("interval");
		this.tableAttr = (String) params.get("table");
	}

	@Override
	public void parentFormSetted() {
		super.parentFormSetted();
		this.intervalDataField = (IntegerDataField) this.getForm().getElementReference(this.intervalAttr);
		this.table = (Table) this.getForm().getElementReference(this.tableAttr);
		this.thread = new Thread(new RefreshRunnable(), "refreshTableThread-" + this.tableAttr);
		this.thread.setDaemon(true);
		this.thread.start();
	}

	@Override
	public void valueChanged(ValueEvent e) {}

	private class RefreshRunnable implements Runnable {
		@Override
		public void run() {
			CheckDataField checkField = (CheckDataField) IMMonitorizacionUsuariosRefreshListener.this.getFormComponent();
			while (true) {
				try {
					Integer value = (Integer) IMMonitorizacionUsuariosRefreshListener.this.intervalDataField.getValue();
					if (value == null) {
						value = 0;
					}
					int timeToSleep = value <= 0 ? 500 : (value * 1000);
					if (checkField.isSelected() && (timeToSleep > 0)) {
						IMMonitorizacionUsuariosRefreshListener.this.refreshTable();

					}
					Thread.sleep(timeToSleep);
				} catch (Exception error) {
					IMMonitorizacionUsuariosRefreshListener.logger.error(null, error);
				}
			}
		}
	}

	public void refreshTable() throws Exception {
		EntityResult res = this.getEntity(this.table.getEntityName()).query(this.table.getParentKeyValues(), this.table.getAttributeList(), this.getSessionId());
		this.table.setValue(res);
	}
}
