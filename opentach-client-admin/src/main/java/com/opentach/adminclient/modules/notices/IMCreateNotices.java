package com.opentach.adminclient.modules.notices;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.DataNavigationEvent;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.RowTransferEvent;
import com.ontimize.gui.RowTransferListener;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.button.MoveBetweenTablesButton;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.DateDataField;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.RefreshTableEvent;
import com.ontimize.gui.table.RefreshTableListener;
import com.ontimize.gui.table.Table;
import com.ontimize.locator.ReferenceLocator;
import com.ontimize.util.notice.INoticeSystem;
import com.utilmize.client.fim.UBasicFIM;

public class IMCreateNotices extends UBasicFIM {
	private static final Logger			LOGGER					= LoggerFactory.getLogger(IMCreateNotices.class);
	/**
	 * Nombre de la entidad que proporcionará el listado de todos los posibles usuarios a los que se podrán enviar avisos internos
	 */
	public static final String			NOTICE_USER_ENTITY		= "ENoticeDestinationUsers";

	/**
	 * Nombre de la entidad que proporcionará el listado de todos los correos electónicos a los que se podrán enviar e-mails
	 */
	public static final String			NOTICE_MAILS_ENTITY		= "ENoticeEmailDestinationUsers";

	/**
	 * Columna de las tablas tAllUsers y tInternalNoticeTo donde está indicado el nombre de usuario
	 */
	public static final String			userColumnName			= "USER_";

	/**
	 * Columna de las tablas tAllMails y tMailNoticeTo donde están las direcciones de correo.
	 */
	public static final String			mailColumnaName			= "EMAIL";

	DateDataField						insertDate;

	/**
	 * Indica si el aviso se enviará como un correo electónico, si no está seleccionado se enviará un aviso interno.
	 */
	CheckDataField						sendEMail;

	/**
	 * Tabla en la que se introducen los destinatarios si se trata de un aviso interno
	 */
	Table								tInternalNoticeTo;

	/**
	 * Tabla en la que se introducen los destinatarios si se trata de un aviso por e-mail
	 */
	Table								tMailNoticeTo;

	/**
	 * Tabla con todos los usuarios posibles a los que se le podrá enviar un aviso interno
	 */
	Table								tAllUsers;

	/**
	 * Tabla con todas las direcciones de e-mail que se podrán seleccionar para enviar el aviso
	 */
	Table								tAllMails;

	/**
	 * Tabla que se utilizará en el UPDATEMODE para mostrar los destinatarios a los que se la ha enviado un aviso.
	 */
	// Tabla tableToEntity;
	/**
	 * Clave del botón para insertar usuarios en tInternalNoticeTo
	 */
	public static final String			B_INSERT_USER			= "insertuser";

	/**
	 * Clave del botón para insertar mails de tAllMails a tMailsNoticeTo
	 */
	public static final String			B_INSERT_MAIL			= "insertmail";

	/**
	 * Clave del botón para eliminar usuarios de tInternalNoticeTo
	 */
	public static final String			B_REMOVE_USER			= "removeuser";

	/**
	 * Clave del botón para eliminar direcciones de e-mail de tMailsNoticeTo
	 */
	public static final String			B_REMOVE_MAIL			= "removemail";

	// public static String ROW_NAME_INSERT_MODE = "InsertMode";

	// public static String ROW_NAME_UPDATE_MODE = "UpdateMode";

	/**
	 * Mensaje que se mostrará cuando se intente enviar un aviso sin haber seleccionado ninguna dirección de destino correcta
	 */
	public static final String			TO_PARAMETER_REQUIRED	= "TO_PARAMETER_REQUIRED";

	/**
	 * botón utilizado para insertar un nuevo registro, es decir insertar un nuevo aviso
	 */
	protected Button					bInsert;

	/**
	 * Botón que añade un nuevo usuario a la lista de destinatarios del aviso
	 */
	protected Button					bInsertUser;

	/**
	 * Botón que permite eliminar los usuarios seleccionados de la lista de destinatarios del aviso
	 */
	protected Button					bRemoveUser;

	/**
	 * Botón que añade nuevos e-mails a la lista de destinatarios del aviso
	 */
	protected Button					bInsertMail;

	/**
	 * Botón que permite eliminar los e-mails seleccionados de la lista de destinatarios del aviso
	 */
	protected Button					bRemoveMail;

	protected TextComboDataField		cNoticeType;

	private ReferenceLocator			buscador				= null;

	@FormComponent(attr = "NOMBRE_PERFIL")
	protected UReferenceDataField	perfil;

	@FormComponent(attr = "PROVINCIA")
	protected UReferenceDataField	provincia;

	@FormComponent(attr = "DestinyUser")
	protected Table						tDestinyUser;

	@FormComponent(attr = "NOMB_EMP")
	protected UReferenceDataField	nombEmp;

	@FormComponent(attr = "IDVEHICLETYPE")
	protected TextComboDataField		idVehicleType;

	@FormComponent(attr = "insertalluser")
	protected Button					bInsertAllUser;

	@FormComponent(attr = "removealluser")
	protected Button					bRemoveAllUser;

	public IMCreateNotices() {}

	@Override
	public void dataChanged(DataNavigationEvent arg0) {
		super.dataChanged(arg0);
	}

	@Override
	public boolean dataWillChange(DataNavigationEvent arg0) {
		return super.dataWillChange(arg0);
	}

	@Override
	public void setQueryInsertMode() {
		super.setQueryInsertMode();
	}

	@Override
	public void setInitialState() {
		this.setInsertMode();
	}

	@Override
	public void setQueryMode() {
		super.setQueryMode();
	}

	protected void setUserSelectionVisible(boolean visible) {
		this.bInsertAllUser.setVisible(visible);
		this.bRemoveAllUser.setVisible(visible);
		this.bInsertUser.setVisible(visible);
		this.bRemoveUser.setVisible(visible);
		this.tAllUsers.setVisible(visible);
		this.tInternalNoticeTo.setVisible(visible);
		this.tInternalNoticeTo.setModifiable(visible);
		this.perfil.setVisible(visible);
		this.provincia.setVisible(visible);
		this.nombEmp.setVisible(visible);
		this.idVehicleType.setVisible(visible);
		this.bRemoveMail.setVisible(!visible);
		this.bInsertMail.setVisible(!visible);
		this.tAllMails.setVisible(!visible);
		this.tMailNoticeTo.setVisible(!visible);
	}

	@Override
	public void setInsertMode() {
		super.setInsertMode();

		this.managedForm.setDataFieldValue("USUARIO", this.buscador.getUser());
		this.managedForm.setDataFieldValue("Asunto", null);
		this.managedForm.setDataFieldValue("Contenido", null);
		this.tInternalNoticeTo.deleteData();
		this.tMailNoticeTo.deleteData();
		this.insertDate.setVisible(false);
		List components = this.managedForm.getComponentList();
		for (int i = 0; i < components.size(); i++) {
			if (components.get(i) instanceof Table) {
				((Table) components.get(i)).setEnabled(true);
				((Table) components.get(i)).setVisible(false);
			}
		}
		if (this.tAllUsers != null) {
			this.tAllUsers.refresh();
		}
		if (this.tAllMails != null) {
			this.tAllMails.refresh();
		}

		if (this.sendEMail != null) {
			this.setUserSelectionVisible(!this.sendEMail.isSelected());
		} else {
			this.setUserSelectionVisible(true);
		}
		this.managedForm.setDataFieldValue(INoticeSystem.NOTICE_CONTENT, "<html><body></body></html>");

		String user = this.buscador.getUser();
		this.managedForm.setDataFieldValue("UsuarioEmisor", user);

		this.bInsertAllUser.setEnabled(!this.tAllUsers.isEmpty());
		this.bRemoveAllUser.setEnabled(!this.tDestinyUser.isEmpty());
	}

	protected void refreshTAllUsers() {
		IMCreateNotices.this.tAllUsers.refreshInThread(0);

		// if (!IMCreateNotices.this.tDestinyUser.isEmpty() && !IMCreateNotices.this.tAllUsers.isEmpty()) {
		// EntityResult resDestiny = new EntityResult((Hashtable<String, Object>) IMCreateNotices.this.tDestinyUser.getValue());
		// Vector<String> usersDestiny = (Vector<String>) resDestiny.get("USER_");
		// EntityResult resAllUsers = new EntityResult((Hashtable<String, Object>) IMCreateNotices.this.tAllUsers.getValue());
		// Vector<String> vAllUsers = (Vector<String>) resAllUsers.get("USER_");
		// int nRows = vAllUsers.size();
		// List<Integer> lIndex = new ArrayList<Integer>();
		// int j = 0;
		// for (int i = 0; i < nRows; i++) {
		// String userAllUsers = vAllUsers.get(i);
		// if (usersDestiny.contains(userAllUsers)) {
		// lIndex.add(i);
		// }
		// j++;
		// }
		//
		// int[] rowsToDelete = new int[lIndex.size()];
		// for (int i = 0; i < lIndex.size(); i++) {
		// Integer valueIndex = lIndex.get(i);
		// rowsToDelete[i] = valueIndex;
		// }
		// IMCreateNotices.this.tAllUsers.deleteRows(rowsToDelete);
		// }
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);

		this.buscador = (ReferenceLocator) this.formManager.getReferenceLocator();
		this.insertDate = (DateDataField) this.managedForm.getDataFieldReference(INoticeSystem.NOTICE_CREATE_DATE);
		this.bRemoveMail = this.managedForm.getButton(IMCreateNotices.B_REMOVE_MAIL);
		this.bRemoveUser = this.managedForm.getButton(IMCreateNotices.B_REMOVE_USER);
		((MoveBetweenTablesButton) this.bRemoveUser).addRowTransferListener(new ListenerDeleteRowTransfer());

		this.bInsertMail = this.managedForm.getButton(IMCreateNotices.B_INSERT_MAIL);
		this.bInsertUser = this.managedForm.getButton(IMCreateNotices.B_INSERT_USER);

		this.bInsert = this.managedForm.getButton(InteractionManager.INSERT_KEY);
		this.cNoticeType = (TextComboDataField) this.managedForm.getDataFieldReference(INoticeSystem.NOTICE_MESSAGE_TYPE_COLUMN_NAME);
		if (this.formManager.getReferenceLocator() instanceof INoticeSystem) {
			try {
				this.cNoticeType.setValues(((INoticeSystem) this.formManager.getReferenceLocator()).getNoticeTypes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.removeInsertListener();
		this.bInsert.addActionListener(new InsertNoticeListener());

		if (this.managedForm.getDataFieldReference(INoticeSystem.NOTICE_SEND_MAIL) != null) {
			this.sendEMail = (CheckDataField) this.managedForm.getDataFieldReference(INoticeSystem.NOTICE_SEND_MAIL);
			this.sendEMail.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent arg0) {
					IMCreateNotices.this.setUserSelectionVisible(!((CheckDataField) arg0.getSource()).isSelected());
					if (IMCreateNotices.this.managedForm.getDataFieldReference(INoticeSystem.NOTICE_RESPONSE_REQUEST) != null) {
						IMCreateNotices.this.managedForm.getDataFieldReference(INoticeSystem.NOTICE_RESPONSE_REQUEST)
						.setEnabled(!((CheckDataField) arg0.getSource()).isSelected());
					}
					if (IMCreateNotices.this.managedForm.getDataFieldReference(INoticeSystem.NOTICE_FORCE_READ) != null) {
						IMCreateNotices.this.managedForm.getDataFieldReference(INoticeSystem.NOTICE_FORCE_READ)
						.setEnabled(!((CheckDataField) arg0.getSource()).isSelected());
					}
				}
			});
		}

		this.tInternalNoticeTo = (Table) this.managedForm.getElementReference(INoticeSystem.NOTICE_TO_PARAMETER);
		this.tMailNoticeTo = (Table) this.managedForm.getElementReference(INoticeSystem.NOTICE_MAILTO_PARAMETER);
		this.tAllUsers = (Table) this.managedForm.getElementReference(IMCreateNotices.NOTICE_USER_ENTITY);
		this.tAllUsers.getJTable().addMouseListener(new InsertNewDestinyListener(this.tAllUsers, this.tInternalNoticeTo));
		this.tAllUsers.getJTable().getTableHeader().setReorderingAllowed(false);
		this.tAllUsers.addRefreshTableListener(new TAllUsersRefreshListener());

		this.tInternalNoticeTo.getJTable().addMouseListener(new InsertNewDestinyListener(this.tInternalNoticeTo, this.tAllUsers));
		this.tAllMails = (Table) this.managedForm.getElementReference(IMCreateNotices.NOTICE_MAILS_ENTITY);
		this.tAllMails.getJTable().addMouseListener(new InsertNewDestinyListener(this.tAllMails, this.tMailNoticeTo));
		this.tMailNoticeTo.getJTable().addMouseListener(new InsertNewDestinyListener(this.tMailNoticeTo, this.tAllMails));

		this.perfil.addValueChangeListener(new FiltersValueChangeListener());
		this.provincia.addValueChangeListener(new FiltersValueChangeListener());
		this.nombEmp.addValueChangeListener(new FiltersValueChangeListener());
		this.idVehicleType.addValueChangeListener(new FiltersValueChangeListener());

	}

	protected class InsertNewDestinyListener extends MouseAdapter {

		Table	source;

		Table	destiny;

		InsertNewDestinyListener(Table source, Table destiny) {
			this.source = source;
			this.destiny = destiny;
		}

		@Override
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getClickCount() == 2) {
				int[] rows = this.source.getSelectedRows();
				if (rows.length == 1) {
					this.destiny.addRow(this.source.getRowData(rows[0]));
					this.source.deleteRow(rows[0]);
				}
			}
		}
	}

	protected class InsertNoticeListener extends InsertListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (IMCreateNotices.this.getCurrentMode() == InteractionManager.QUERYINSERT) {
				super.actionPerformed(arg0);
				return;
			}
			if (IMCreateNotices.this.checkInsert()) {
				if (IMCreateNotices.this.formManager.getReferenceLocator() instanceof INoticeSystem) {
					try {
						Hashtable<String, Object> h = IMCreateNotices.this.managedForm.getDataFieldValues(false);
						h.put("Prueba", "Esto es una prueba");
						boolean toExist = false;
						if ((IMCreateNotices.this.sendEMail != null) && IMCreateNotices.this.sendEMail.isSelected()) {
							// Si se trata de un e-mail
							if ((IMCreateNotices.this.tMailNoticeTo != null) && (IMCreateNotices.this.tMailNoticeTo
									.getValue() != null) && !((Hashtable<String, Object>) IMCreateNotices.this.tMailNoticeTo.getValue()).isEmpty()) {
								Vector<Object> v = (Vector<Object>) ((Hashtable<String, Object>) IMCreateNotices.this.tMailNoticeTo.getValue())
										.get(IMCreateNotices.mailColumnaName);
								if ((v != null) && (v.size() > 0)) {
									toExist = true;
									h.put(INoticeSystem.NOTICE_MAILTO_PARAMETER, v);
								}
							}
						} else {
							// Si se trata de un e-mail
							if ((IMCreateNotices.this.tInternalNoticeTo != null) && (IMCreateNotices.this.tInternalNoticeTo
									.getValue() != null) && !((Hashtable<String, Object>) IMCreateNotices.this.tInternalNoticeTo.getValue())
									.isEmpty()) {
								Vector<Object> v = (Vector<Object>) ((Hashtable<String, Object>) IMCreateNotices.this.tInternalNoticeTo.getValue())
										.get(IMCreateNotices.userColumnName);
								if ((v != null) && (v.size() > 0)) {
									toExist = true;
									h.put(INoticeSystem.NOTICE_TO_PARAMETER, v);
								}
							}

						}
						if (toExist) {
							((INoticeSystem) IMCreateNotices.this.formManager.getReferenceLocator()).sendNotice(h,
									IMCreateNotices.this.formManager.getReferenceLocator().getSessionId(), false);
							if (IMCreateNotices.this.detailForm) {
								IMCreateNotices.this.managedForm.getDetailComponent().hideDetailForm();
								;
								IMCreateNotices.this.managedForm.getDetailComponent().getTable().refresh();
							} else {
								if (IMCreateNotices.this.managedForm.getJDialog() != null) {
									IMCreateNotices.this.managedForm.getJDialog().setVisible(false);
								}
							}
							IMCreateNotices.this.setInitialState();
						} else {
							IMCreateNotices.this.managedForm.message(IMCreateNotices.TO_PARAMETER_REQUIRED, Form.INFORMATION_MESSAGE);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					super.actionPerformed(arg0);
				}
			}
		}
	}

	class ListenerDeleteRowTransfer implements RowTransferListener {

		public ListenerDeleteRowTransfer() {}

		@Override
		public void rowsTransferred(RowTransferEvent ev) {
			IMCreateNotices.this.refreshTAllUsers();
		}
	}

	class FiltersValueChangeListener implements ValueChangeListener {

		public FiltersValueChangeListener() {}

		@Override
		public void valueChanged(ValueEvent e) {
			IMCreateNotices.this.refreshTAllUsers();

		}
	}

	class TAllUsersRefreshListener implements RefreshTableListener {

		@Override
		public void postCorrectRefresh(RefreshTableEvent e) {
			if (!IMCreateNotices.this.tDestinyUser.isEmpty() && !IMCreateNotices.this.tAllUsers.isEmpty()) {
				EntityResult resDestiny = new EntityResult((Hashtable<String, Object>) IMCreateNotices.this.tDestinyUser.getValue());
				Vector<String> usersDestiny = (Vector<String>) resDestiny.get("USER_");
				EntityResult resAllUsers = new EntityResult((Hashtable<String, Object>) IMCreateNotices.this.tAllUsers.getValue());
				Vector<String> vAllUsers = (Vector<String>) resAllUsers.get("USER_");
				int nRows = vAllUsers.size();
				List<Integer> lIndex = new ArrayList<Integer>();
				int j = 0;
				for (int i = 0; i < nRows; i++) {
					String userAllUsers = vAllUsers.get(i);
					if (usersDestiny.contains(userAllUsers)) {
						lIndex.add(i);
					}
					j++;
				}

				int[] rowsToDelete = new int[lIndex.size()];
				for (int i = 0; i < lIndex.size(); i++) {
					Integer valueIndex = lIndex.get(i);
					rowsToDelete[i] = valueIndex;
				}
				IMCreateNotices.this.tAllUsers.deleteRows(rowsToDelete);
			}
		}

		@Override
		public void postIncorrectRefresh(RefreshTableEvent e) {

		}

	}
}
