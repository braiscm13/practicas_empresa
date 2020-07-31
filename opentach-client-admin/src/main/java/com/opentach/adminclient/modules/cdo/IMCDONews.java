package com.opentach.adminclient.modules.cdo;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.utilmize.client.fim.UBasicFIM;

public class IMCDONews extends UBasicFIM {

	private static final Logger	logger		= LoggerFactory.getLogger(IMCDONews.class);
	final static String[]		NEWS_ATTRS	= new String[] { "IDCDONEW", "TITLE", "ACTIVE", "CONTENT", "CREATION_DATE", "TIME_TO_SHOW", "LOCALE" };

	@FormComponent(attr = "ECDONews")
	Table						newsTable;
	@FormComponent(attr = "save")
	Button						saveButton;

	public IMCDONews() {
		super();
	}

	public IMCDONews(boolean actualizar, boolean formDetalle) {
		super(actualizar, formDetalle);
	}

	public IMCDONews(boolean actualizar) {
		super(actualizar);
	}

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);

		this.newsTable.getJTable().getSelectionModel().addListSelectionListener(new TableSelectionListener());
		this.newsTable.getJTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.newsTable.refreshInThread(0);
	}

	class TableSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			try {
				int[] selectedRows = IMCDONews.this.newsTable.getSelectedRows();
				if ((selectedRows == null) || (selectedRows.length != 1)) {
					this.cleanFrom();
				} else {
					Object object = IMCDONews.this.newsTable.getSelectedRowData().get("IDCDONEW");
					Object idCdoNew = null;
					if (object != null) {
						idCdoNew = ((List) object).get(0);
					}
					if (idCdoNew == null) {
						this.cleanFrom();
					} else {
						this.fillForm(idCdoNew);
					}
				}
			} catch (Exception ex) {
				MessageManager.getMessageManager().showExceptionMessage(ex, IMCDONews.logger);
			}
		}

		private void cleanFrom() {
			Vector dataComponents = IMCDONews.this.managedForm.getDataComponents();
			for (Object dc : dataComponents) {
				if ((dc instanceof DataComponent) && !(dc instanceof Table)) {
					((DataComponent) dc).deleteData();
				}
			}
			for (String attr : IMCDONews.NEWS_ATTRS) {
				IMCDONews.this.managedForm.disableDataField(attr);
			}
			IMCDONews.this.saveButton.setEnabled(false);
		}

		private void fillForm(Object idCdoNew) throws Exception {
			String entityName = IMCDONews.this.newsTable.getEntityName();
			Entity entity = IMCDONews.this.formManager.getReferenceLocator().getEntityReference(entityName);
			EntityResult res = entity.query(EntityResultTools.keysvalues("IDCDONEW", idCdoNew), EntityResultTools.attributes(IMCDONews.NEWS_ATTRS),
					IMCDONews.this.formManager.getReferenceLocator().getSessionId());
			CheckingTools.checkValidEntityResult(res, "NEWS_NOT_FOUND", true, true, (Object[]) null);
			Hashtable recordValues = res.getRecordValues(0);
			for (String attr : IMCDONews.NEWS_ATTRS) {
				IMCDONews.this.managedForm.setDataFieldValue(attr, recordValues.get(attr));
				IMCDONews.this.managedForm.enableDataField(attr);
			}
			IMCDONews.this.saveButton.setEnabled(true);
		}
	}

}
