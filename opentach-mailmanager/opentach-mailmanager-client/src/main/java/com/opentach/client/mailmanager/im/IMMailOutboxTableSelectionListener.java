package com.opentach.client.mailmanager.im;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.field.DataField;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.exception.OpentachException;
import com.opentach.common.mailmanager.MailManagerNaming;
import com.opentach.common.mailmanager.services.IMailManagerService;
import com.opentach.common.util.OpentachCheckingTools;
import com.utilmize.client.gui.AbstractListSelectionListener;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.field.table.UTable;

public class IMMailOutboxTableSelectionListener extends AbstractListSelectionListener {

	private static final Logger			logger	= LoggerFactory.getLogger(IMMailOutboxTableSelectionListener.class);
	@FormComponent(attr = "MAI_BODY")
	private DataField					bodyField;
	private SwingWorker<String, Void>	worker;

	public IMMailOutboxTableSelectionListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void valueChanged(ListSelectionEvent evt) {
		if (evt.getValueIsAdjusting()) {
			return;
		}
		synchronized (this) {
			if (this.worker != null) {
				this.worker.cancel(true);
			}
			this.worker = new SwingWorker<String, Void>() {
				@Override
				protected String doInBackground() throws Exception {
					UTable table = (UTable) IMMailOutboxTableSelectionListener.this.getFormComponent();
					if ((table.getSelectedRows().length == 0) || (table.getSelectedRows().length > 1)) {
						return null;
					}
					int selectedRow = table.getSelectedRow();
					final Object maiId = table.getRowValue(selectedRow, MailManagerNaming.MAI_ID);
					if (maiId == null) {
						return null;
					}
					EntityResult res = null;
					if (table.getEntityName().toUpperCase().contains("ADMIN")) {
						res = BeansFactory.getBean(IMailManagerService.class).mailAdminQuery(EntityResultTools.keysvalues(MailManagerNaming.MAI_ID, maiId),
								Arrays.asList(MailManagerNaming.MAI_BODY));
					} else {
						res = BeansFactory.getBean(IMailManagerService.class).mailUserQuery(EntityResultTools.keysvalues(MailManagerNaming.MAI_ID, maiId),
								Arrays.asList(MailManagerNaming.MAI_BODY));
					}
					OpentachCheckingTools.checkValidEntityResult(res, OpentachException.class, MailManagerNaming.ERR_MANDATORY_MAI_ID, true, true, new Object[] {});
					return (String) ((List<?>) res.get(MailManagerNaming.MAI_BODY)).get(0);
				}

				@Override
				protected void done() {
					try {
						if (!this.isCancelled()) {
							String body = this.get();
							IMMailOutboxTableSelectionListener.this.bodyField.setValue(body);
						}
					} catch (Exception err) {
						MessageManager.getMessageManager().showExceptionMessage(err, IMMailOutboxTableSelectionListener.logger);
					}
				}
			};
			this.worker.execute();
		}
	}

}
