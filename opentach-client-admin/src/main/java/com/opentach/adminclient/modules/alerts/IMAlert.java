package com.opentach.adminclient.modules.alerts;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.alerts.IAlertSystem;
import com.ontimize.util.notice.INoticeSystem;
import com.utilmize.client.fim.UBasicFIM;

public class IMAlert extends UBasicFIM {

	/**
	 * Nombre de la entidad que proporcionará el listado de todos los posibles usuarios a los que se podrán enviar avisos internos
	 */
	public static final String			NOTICE_USER_ENTITY	= "ENoticeDestinationUsers";

	/**
	 * Nombre de la entidad que proporcionará el listado de todos los correos electónicos a los que se podrán enviar e-mails
	 */
	public static final String			NOTICE_MAILS_ENTITY	= "ENoticeEmailDestinationUsers";

	/**
	 * Columna de las tablas tAllUsers y tInternalNoticeTo donde está indicado el nombre de usuario
	 */
	public static final String			userColumnName		= "user_";

	/**
	 * Columna de las tablas tAllMails y tMailNoticeTo donde están las direcciones de correo.
	 */
	public static final String			mailColumnaName		= "email";

	/**
	 * Clave del botón para insertar usuarios en tInternalNoticeTo
	 */
	public static final String			B_INSERT_USER		= "insertuser";

	/**
	 * Clave del botón para insertar mails de tAllMails a tMailsNoticeTo
	 */
	public static final String			B_INSERT_MAIL		= "insertmail";

	/**
	 * Clave del botón para eliminar usuarios de tInternalNoticeTo
	 */
	public static final String			B_REMOVE_USER		= "removeuser";

	/**
	 * Clave del botón para eliminar direcciones de e-mail de tMailsNoticeTo
	 */
	public static final String			B_REMOVE_MAIL		= "removemail";

	/**
	 * Botón para actualizar la configuracion de la tarea
	 */
	protected Button					bActualizar			= null;

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

	/**
	 * Tabla que contiene los usuarios posibles como destino del aviso
	 */
	protected Table						tNoticeUsers;

	/**
	 * Tabla que contiene los usuarios destino del aviso
	 */
	protected Table						tDestinyUsers;

	/**
	 * Tabla con todas las direcciones de e-mail que se podrán seleccionar para enviar el aviso
	 */
	protected Table						tAllMails;

	/**
	 * Tabla en la que se introducen los destinatarios si se trata de un aviso por e-mail
	 */
	protected Table						tMailNoticeTo;

	/**
	 * Indica si el aviso se enviará como un correo electónico, si no está seleccionado se enviará un aviso interno.
	 */
	protected CheckDataField			sendEMail;

	protected boolean					destinyUserModified	= false;

	protected boolean					destinyMailModified	= false;

	protected EntityReferenceLocator	buscador			= null;

	public IMAlert() {
		super();
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);

		this.buscador = this.formManager.getReferenceLocator();

		this.bRemoveMail = this.managedForm.getButton(IMAlert.B_REMOVE_MAIL);
		this.bRemoveUser = this.managedForm.getButton(IMAlert.B_REMOVE_USER);
		this.bInsertMail = this.managedForm.getButton(IMAlert.B_INSERT_MAIL);
		this.bInsertUser = this.managedForm.getButton(IMAlert.B_INSERT_USER);

		this.tNoticeUsers = (Table) this.managedForm.getElementReference(IMAlert.NOTICE_USER_ENTITY);
		this.tDestinyUsers = (Table) this.managedForm.getElementReference("UsuarioDestino");

		this.tAllMails = (Table) this.managedForm.getDataFieldReference(IMAlert.NOTICE_MAILS_ENTITY);
		this.tMailNoticeTo = (Table) this.managedForm.getDataFieldReference(INoticeSystem.NOTICE_MAILTO_PARAMETER);

		if (this.bInsertMail != null) {
			this.bInsertMail.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Si se añade un usuario, se activa el botón de actualizar
					IMAlert.this.managedForm.enableButton(InteractionManager.UPDATE_KEY);
					IMAlert.this.destinyMailModified = true;
				}
			});
		}

		if (this.bRemoveMail != null) {
			this.bRemoveMail.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Si se añade un usuario, se activa el botón de actualizar
					IMAlert.this.managedForm.enableButton(InteractionManager.UPDATE_KEY);
					IMAlert.this.destinyMailModified = true;
				}
			});
		}

		if (this.bInsertUser != null) {
			this.bInsertUser.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Si se añade un usuario, se activa el botón de actualizar
					IMAlert.this.managedForm.enableButton(InteractionManager.UPDATE_KEY);
					IMAlert.this.destinyUserModified = true;
				}
			});
		}

		if (this.bRemoveUser != null) {
			this.bRemoveUser.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Si se añade un usuario, se activa el botón de actualizar
					IMAlert.this.managedForm.enableButton(InteractionManager.UPDATE_KEY);
					IMAlert.this.destinyUserModified = true;
				}
			});
		}

		if (this.managedForm.getDataFieldReference(IAlertSystem.NOTICE_SEND_MAIL) != null) {
			this.sendEMail = (CheckDataField) this.managedForm.getDataFieldReference(IAlertSystem.NOTICE_SEND_MAIL);
			this.sendEMail.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					IMAlert.this.setUserSelectionVisible(!((CheckDataField) e.getSource()).isSelected());
					if (IMAlert.this.managedForm.getDataFieldReference(IAlertSystem.NOTICE_RESPONSE_REQUEST) != null) {
						IMAlert.this.managedForm.getDataFieldReference(IAlertSystem.NOTICE_RESPONSE_REQUEST).setEnabled(!((CheckDataField) e.getSource()).isSelected());
					}
					if (IMAlert.this.managedForm.getDataFieldReference(IAlertSystem.NOTICE_FORCE_READ) != null) {
						IMAlert.this.managedForm.getDataFieldReference(IAlertSystem.NOTICE_FORCE_READ).setEnabled(!((CheckDataField) e.getSource()).isSelected());
					}
				}
			});
		}

		this.removeUpdateListener();
		this.bActualizar = this.managedForm.getButton(InteractionManager.UPDATE_KEY);
		if (this.bActualizar != null) {
			this.bActualizar.addActionListener(this.myEscuchadorActualizacion);
		}

	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();

		if (this.sendEMail != null) {
			this.setUserSelectionVisible(!this.sendEMail.isSelected());
		}

		this.setMailTables();
		this.setUserTables();
	}

	protected void setUserSelectionVisible(boolean visible) {
		if (this.bInsertUser != null) {
			this.bInsertUser.setVisible(visible);
		}
		if (this.bRemoveUser != null) {
			this.bRemoveUser.setVisible(visible);
		}
		if (this.tNoticeUsers != null) {
			this.tNoticeUsers.setVisible(visible);
		}
		if (this.tDestinyUsers != null) {
			this.tDestinyUsers.setVisible(visible);
			this.tDestinyUsers.setModifiable(visible);
		}

		if (this.bRemoveMail != null) {
			this.bRemoveMail.setVisible(!visible);
		}
		if (this.bInsertMail != null) {
			this.bInsertMail.setVisible(!visible);
		}
		if (this.tAllMails != null) {
			this.tAllMails.setVisible(!visible);
		}
		if (this.tMailNoticeTo != null) {
			this.tMailNoticeTo.setVisible(!visible);
		}

	}

	protected void setUserTables() {

		if (this.tNoticeUsers != null) {
			this.tNoticeUsers.refresh();
		}

		String NOTICE_TO_PARAMETER = (String) this.managedForm.getDataFieldValue("NOTICE_TO_PARAMETER");
		if (NOTICE_TO_PARAMETER != null) {
			EntityResult destinyTable = new EntityResult();
			EntityResult origTable = (EntityResult) this.tNoticeUsers.getValue();
			Vector<Object> vUser_ = (Vector<Object>) origTable.get(IMAlert.userColumnName);
			if (vUser_ != null) {
				Vector<Object> actualUsers = new Vector<Object>();
				StringTokenizer tkn = new StringTokenizer(NOTICE_TO_PARAMETER, ";");
				while (tkn.hasMoreTokens()) {
					actualUsers.add(tkn.nextToken());
				}
				for (int i = 0; i < vUser_.size(); i++) {
					Object vU = vUser_.elementAt(i);
					if (actualUsers.contains(vU)) {
						Hashtable<String, Object> dataReg = origTable.getRecordValues(i);
						destinyTable.addRecord(dataReg);
						origTable.deleteRecord(i);
					}
				}

				this.tNoticeUsers.setValue(origTable);
				this.tDestinyUsers.setValue(destinyTable);
			}
		}
	}

	protected void setMailTables() {

		if (this.tAllMails != null) {
			this.tAllMails.refresh();
		}

		String NOTICE_MAILTO_PARAMETER = (String) this.managedForm.getDataFieldValue("NOTICE_MAILTO_PARAMETER");
		if (NOTICE_MAILTO_PARAMETER != null) {
			EntityResult destinyTable = new EntityResult();
			EntityResult origTable = (EntityResult) this.tAllMails.getValue();
			Vector<Object> vUser_ = (Vector<Object>) origTable.get(IMAlert.userColumnName);
			Vector<Object> vMailUser_ = (Vector<Object>) origTable.get(IMAlert.mailColumnaName);
			if ((vUser_ != null) && (vMailUser_ != null)) {
				Vector<Object> actualMailUsers = new Vector<Object>();
				StringTokenizer tkn = new StringTokenizer(NOTICE_MAILTO_PARAMETER, ";");
				while (tkn.hasMoreTokens()) {
					actualMailUsers.add(tkn.nextToken());
				}
				Vector<Object> usrRegElim = new Vector<Object>();
				int numUsers = origTable.calculateRecordNumber();
				for (int i = 0; i < numUsers; i++) {
					Hashtable<String, Object> regData = origTable.getRecordValues(i);
					Object mailUs = regData.get(IMAlert.mailColumnaName);
					if (actualMailUsers.contains(mailUs)) {
						destinyTable.addRecord(regData);
						Object user = regData.get(IMAlert.userColumnName);
						usrRegElim.add(user);
					}
				}

				// Se eliminan de la tabla origen los registros que estan en
				// destino
				for (int i = 0; i < usrRegElim.size(); i++) {
					Object userComp = usrRegElim.elementAt(i);
					numUsers = origTable.calculateRecordNumber();
					for (int j = 0; j < numUsers; j++) {
						Hashtable<String, Object> regData = origTable.getRecordValues(j);
						Object user = regData.get(IMAlert.userColumnName);
						if (userComp.equals(user)) {
							origTable.deleteRecord(j);
							break;
						}
					}
				}

				this.tAllMails.setValue(origTable);
				this.tMailNoticeTo.setValue(destinyTable);
			}
		}
	}

	UpdateListener	myEscuchadorActualizacion	= new UpdateListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (((IMAlert.this.modifiedFieldAttributes != null) && (IMAlert.this.modifiedFieldAttributes.size() > 0)) || IMAlert.this.destinyUserModified || IMAlert.this.destinyMailModified) {

				Hashtable<Object, Object> modifiedConfig = new Hashtable<Object, Object>();

				Object taskName = IMAlert.this.managedForm.getDataFieldValue(IAlertSystem.TASK_NAME_FIELD);
				if (taskName != null) {
					// Se pasa el
					// task name y
					// el task group
					// para
					// identificar
					// la tarea a
					// modificar
					modifiedConfig.put(IAlertSystem.TASK_NAME_FIELD, taskName);
					Object taskGroup = IMAlert.this.managedForm.getDataFieldValue(IAlertSystem.TASK_GROUP_FIELD);
					if (taskGroup != null) {
						modifiedConfig.put(IAlertSystem.TASK_GROUP_FIELD, taskGroup);
					}

					for (int i = 0; i < IMAlert.this.modifiedFieldAttributes.size(); i++) {
						Object key = IMAlert.this.modifiedFieldAttributes.elementAt(i);
						Object value = IMAlert.this.managedForm.getDataFieldValue(key.toString());
						if (value != null) {
							modifiedConfig.put(key, value);
						}
					}

					if (IMAlert.this.destinyUserModified && (IMAlert.this.tDestinyUsers != null)) {
						Hashtable<String, Object> tvalDUsert = (Hashtable<String, Object>) IMAlert.this.tDestinyUsers.getValue();
						if (tvalDUsert != null) {
							EntityResult destUsers = new EntityResult(tvalDUsert);
							Vector<Object> vUser_ = (Vector<Object>) destUsers.get(IMAlert.userColumnName);
							String NOTICE_TO_PARAMETER = ApplicationManager.vectorToStringSeparateBySemicolon(vUser_);
							if (NOTICE_TO_PARAMETER != null) {
								modifiedConfig.put("NOTICE_TO_PARAMETER", NOTICE_TO_PARAMETER);
							}
						}
					}

					if (IMAlert.this.destinyMailModified && (IMAlert.this.tMailNoticeTo != null)) {
						Hashtable<String, Object> tvalDMailt = (Hashtable<String, Object>) IMAlert.this.tMailNoticeTo.getValue();
						if (tvalDMailt != null) {
							EntityResult destMails = new EntityResult(tvalDMailt);
							Vector<Object> vMailUser_ = (Vector<Object>) destMails.get(IMAlert.mailColumnaName);
							String NOTICE_MAILTO_PARAMETER = ApplicationManager.vectorToStringSeparateBySemicolon(vMailUser_);
							if (NOTICE_MAILTO_PARAMETER != null) {
								modifiedConfig.put("NOTICE_MAILTO_PARAMETER", NOTICE_MAILTO_PARAMETER);
							}
						}
					}

					if (IMAlert.this.sendEMail != null) {
						if (IMAlert.this.sendEMail.isSelected()) {
							modifiedConfig.remove("NOTICE_TO_PARAMETER");
						} else {
							modifiedConfig.remove("NOTICE_MAILTO_PARAMETER");
						}
					}

					try {
						((IAlertSystem) ApplicationManager.getApplication().getReferenceLocator())
						.updateAlertConfiguration(modifiedConfig);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			IMAlert.this.managedForm.disableButton(InteractionManager.UPDATE_KEY);
		}

		@Override
		protected void postCorrectUpdate(EntityResult arg0, Entity arg1) throws Exception {
			IMAlert.this.destinyUserModified = false;
			IMAlert.this.destinyMailModified = false;
		}

		@Override
		protected void postIncorrectUpdate(EntityResult arg0, Entity arg1) throws Exception {
			// super.postActualizacionIncorrecta(arg0,
			// arg1);
		}

	};

}
