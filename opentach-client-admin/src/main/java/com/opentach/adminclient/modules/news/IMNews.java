package com.opentach.adminclient.modules.news;

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

public class IMNews extends UBasicFIM {

	private static final Logger	logger		= LoggerFactory.getLogger(IMNews.class);
	final static String[]		NEWS_ATTRS	= new String[] { "IDNEW", "TITLE", "ACTIVE", "CONTENT", "CREATION_DATE", "LOCALE" };

	@FormComponent(attr = "ENews")
	Table						newsTable;
	@FormComponent(attr = "save")
	Button						saveButton;

	public IMNews() {
		super();
	}

	public IMNews(boolean actualizar, boolean formDetalle) {
		super(actualizar, formDetalle);
	}

	public IMNews(boolean actualizar) {
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
				int[] selectedRows = IMNews.this.newsTable.getSelectedRows();
				if ((selectedRows == null) || (selectedRows.length != 1)) {
					this.cleanFrom();
				} else {
					Object object = IMNews.this.newsTable.getSelectedRowData().get("IDNEW");
					Object idNew = null;
					if (object != null) {
						idNew = ((List) object).get(0);
					}
					if (idNew == null) {
						this.cleanFrom();
					} else {
						this.fillForm(idNew);
					}
				}
			} catch (Exception ex) {
				MessageManager.getMessageManager().showExceptionMessage(ex, IMNews.logger);
			}
		}

		private void cleanFrom() {
			Vector dataComponents = IMNews.this.managedForm.getDataComponents();
			for (Object dc : dataComponents) {
				if ((dc instanceof DataComponent) && !(dc instanceof Table)) {
					((DataComponent) dc).deleteData();
				}
			}
			for (String attr : IMNews.NEWS_ATTRS) {
				IMNews.this.managedForm.disableDataField(attr);
			}
			IMNews.this.saveButton.setEnabled(false);
		}

		private void fillForm(Object idNew) throws Exception {
			String entityName = IMNews.this.newsTable.getEntityName();
			Entity entity = IMNews.this.formManager.getReferenceLocator().getEntityReference(entityName);
			EntityResult res = entity.query(EntityResultTools.keysvalues("IDNEW", idNew), EntityResultTools.attributes(IMNews.NEWS_ATTRS),
					IMNews.this.formManager.getReferenceLocator().getSessionId());
			CheckingTools.checkValidEntityResult(res, "NEWS_NOT_FOUND", true, true, (Object[]) null);
			Hashtable recordValues = res.getRecordValues(0);
			for (String attr : IMNews.NEWS_ATTRS) {
				IMNews.this.managedForm.setDataFieldValue(attr, recordValues.get(attr));
				IMNews.this.managedForm.enableDataField(attr);
			}
			IMNews.this.saveButton.setEnabled(true);
		}
	}

}
