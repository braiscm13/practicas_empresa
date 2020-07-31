package com.opentach.client.modules.notices;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.locator.ReferenceLocator;
import com.ontimize.util.notice.HTMLTemplate;
import com.ontimize.util.notice.INoticeSystem;
import com.ontimize.util.notice.NewNoticeWindow;
import com.ontimize.util.notice.NoticeManagerFactory;
import com.ontimize.util.notice.NoticeTable;
import com.utilmize.client.fim.UBasicFIM;

public class IMNoticesReceived extends UBasicFIM {

	/**
	 * Tabla donde se muestran los avisos enviados
	 */
	Table						tNotices;

	/**
	 * Tabla donde se muestran los avisos recibidos de los usuarios
	 */
	Table						tMyReceivedUserNotices;


	/**
	 * Componente HTML donde se muestra la información del aviso recibido
	 * seleccionado
	 */
	HTMLTemplate				htmlTemplate;

	// protected ITemplateManagement templateManager;

	/**
	 * entity usada en el formulario para indicar la tabla donde se mostrarán
	 * los avisos recibidos
	 */
	public static final String	MY_RECEIVED_USER_NOTICES_TABLE	= "EAvisosRecibidosUser";

	public IMNoticesReceived() {
		super();
		// setTemplateManager(new
		// DefaultTemplateManager("com/imatia/avisos/utils/avisos/noticestemplates/templates.properties",
		// "com/imatia/avisos/utils/avisos/noticestemplates/noticetemplate.html"));
	}

	@Override
	public void setQueryInsertMode() {
		this.setUpdateMode();
	}

	@Override
	public void setQueryMode() {
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.tNotices != null) {
			this.tNotices.refresh();
		}
		if (this.formManager.getReferenceLocator() instanceof INoticeSystem) {
			try {
				this.tMyReceivedUserNotices.refresh(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public void setInsertMode() {
		this.setUpdateMode();
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		((NewNoticeWindow) NoticeManagerFactory.getNoticeWindow(true)).setFormReference("manageravisos", "formnotices.xml");

		// Establece el valor del nombre del usuario actual para poder filtrar
		// los avisos enviados
		if (this.managedForm.getDataFieldReference(INoticeSystem.NOTICE_FROM_PARAMETER) != null) {
			try {
				this.managedForm.setDataFieldValue(INoticeSystem.NOTICE_FROM_PARAMETER, ((INoticeSystem) this.formManager.getReferenceLocator())
						.getUserId(this.formManager.getReferenceLocator().getSessionId()).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.tNotices = (Table) this.managedForm.getElementReference(ReferenceLocator.NOTICE_ENTITY_NAME);
		this.tMyReceivedUserNotices = (Table) this.managedForm.getElementReference(IMNoticesReceived.MY_RECEIVED_USER_NOTICES_TABLE);
		this.htmlTemplate = (HTMLTemplate) this.managedForm.getElementReference("htmltemplate");
		// Se incluye un escuhador para la tabla de avisos recibidos para que al
		// seleccioanar un aviso se muestre su información en el componente HTML
		if (this.tMyReceivedUserNotices != null) {
			this.tMyReceivedUserNotices.getJTable().getSelectionModel()
			.addListSelectionListener(new ReceivedTableListener(this.tMyReceivedUserNotices));
		}

	}

	// Escuchador para los cambios de selección de la tabla
	class ReceivedTableListener implements ListSelectionListener {

		protected Table	table	= null;

		public ReceivedTableListener(Table table) {
			this.table = table;

		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			int selectedRow = this.table.getSelectedRow();
			if (selectedRow >= 0) {
				Hashtable h = this.table.getRowData(selectedRow);
				if ((h != null) && (IMNoticesReceived.this.htmlTemplate != null)) {
					String template = null;
					if (h.get(INoticeSystem.NOTICE_MESSAGE_COLUMN_NAME) != null) {
						template = (String) h.get(INoticeSystem.NOTICE_MESSAGE_COLUMN_NAME);
						IMNoticesReceived.this.htmlTemplate.setText(template);

						// Si corresponde debe actualizarse como leido el
						// mensaje
						if (h.containsKey(INoticeSystem.NOTICE_READ) && h.get(INoticeSystem.NOTICE_READ).equals(Integer.valueOf(0))
								&& (this.table instanceof NoticeTable)) {
							Vector keys = new Vector();
							keys.add(h.get(INoticeSystem.NOTICE_KEY));
							Vector request = new Vector();
							if ((h.get(INoticeSystem.NOTICE_RESPONSE_REQUEST) != null)
									&& h.get(INoticeSystem.NOTICE_RESPONSE_REQUEST).equals(Integer.valueOf(1))) {
								request.add(Integer.valueOf(selectedRow));
							}
							if (((NoticeTable) this.table).isCheckReadAutomatic()) {
								((NoticeTable) this.table).checkRead(keys, request, false);
							}
						}

					}

				}
			} else if (IMNoticesReceived.this.htmlTemplate != null) {
				IMNoticesReceived.this.htmlTemplate.delete();
				// String NO_SELECTED_NOTICE =
				// ApplicationManager.getTranslation("NO_SELECTED_NOTICE",
				// formularioGestionado.getResourceBundle());
				// htmlTemplate.setText("<HTML><BODY bgcolor=\"#f1f1f1\"><b>" +
				// NO_SELECTED_NOTICE + "</b></BODY></HTML>");
			}
		}
	}

}
