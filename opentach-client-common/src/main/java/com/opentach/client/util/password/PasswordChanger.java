package com.opentach.client.util.password;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.locator.ReferenceLocator;

/**
 * The Class PasswordChanger.
 */
public final class PasswordChanger {

	/** The Constant logger. */
	private static final Logger		logger	= LoggerFactory.getLogger(PasswordChanger.class);

	/** The instance. */
	private static PasswordChanger	instance;

	/**
	 * Do change password.
	 *
	 * @param allowCancel
	 *            the allow cancel
	 */
	public static void doChangePassword(boolean allowCancel, Window owner) {
		PasswordChanger.getInstance().changePassword(allowCancel, owner);
	}

	/**
	 * Gets the single instance of PasswordChanger.
	 *
	 * @return single instance of PasswordChanger
	 */
	protected static PasswordChanger getInstance() {
		if (PasswordChanger.instance == null) {
			PasswordChanger.instance = new PasswordChanger();
		}
		return PasswordChanger.instance;
	}

	/** The cambio password. */
	private Form fCambioPassword;

	/**
	 * Instantiates a new password changer.
	 */
	private PasswordChanger() {
		super();
	}

	/**
	 * Change password.
	 *
	 * @param allowCancel
	 *            the allow cancel
	 */
	protected void changePassword(boolean allowCancel, Window owner) {
		this.ensureForm();
		this.fCambioPassword.getButton("cancelar").setVisible(allowCancel);
		this.fCambioPassword.getInteractionManager().setUpdateMode();
		this.fCambioPassword.deleteDataFields();
		this.fCambioPassword.setDataFieldValue("USUARIO", ((ReferenceLocator) this.getReferenceLocator()).getUser());
		this.fCambioPassword.enableDataFields();
		this.fCambioPassword.enableButtons();
		this.fCambioPassword.disableDataField("USUARIO");

		Dialog dCambioPassword = this.fCambioPassword.putInModalDialog(ApplicationManager.getApplication().getFrame());
		dCambioPassword.setTitle(ApplicationManager.getTranslation("CambiarPassword", this.fCambioPassword.getResourceBundle()));
		dCambioPassword.setVisible(true);
	}

	private void ensureForm() {
		if (this.fCambioPassword != null) {
			return;
		}
		this.fCambioPassword = ApplicationManager.getApplication().getFormManager("managerpassword").getFormCopy("formCambiarPassword.xml");
		this.fCambioPassword.getButton("aceptar").addActionListener(event -> PasswordChanger.this.onAceptAction(event));
		this.fCambioPassword.getButton("cancelar").addActionListener(event -> PasswordChanger.this.onCancelAction(event));
	}

	/**
	 * On cancel action.
	 *
	 * @param event
	 *            the event
	 */
	protected void onCancelAction(ActionEvent event) {
		SwingUtilities.getWindowAncestor((Component) event.getSource()).setVisible(false);
		SwingUtilities.getWindowAncestor((Component) event.getSource()).dispose();
	}

	/**
	 * On acept action.
	 *
	 * @param event
	 *            the event
	 */
	protected void onAceptAction(ActionEvent event) {
		if (this.fCambioPassword.isEmpty("Password") || this.fCambioPassword.isEmpty("ConfirmarPassword") || this.fCambioPassword.isEmpty("OldPassword")) {
			MessageManager.getMessageManager().showMessage((Component) event.getSource(), "M_INTRODUZCA_CONTRASENHA_Y_CONFIRMACION_DE_CONTRASENHA", MessageType.ERROR,
					new Object[] {});
			return;
		}
		if (this.fCambioPassword.getDataFieldValue("Password").equals(this.fCambioPassword.getDataFieldValue("ConfirmarPassword")) == false) {
			MessageManager.getMessageManager().showMessage((Component) event.getSource(), "M_CONTRASENHA_Y_CONFIRMACION_DE_CONTRASENHA_NO_COINCIDEN_DEBEN_COINCIDIR",
					MessageType.ERROR, new Object[] {});
			return;
		}
		try {
			Entity eUsu = this.getReferenceLocator().getEntityReference("EUsuariosTodos");
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			cv.put("User_", ((ReferenceLocator) this.getReferenceLocator()).getUser());
			EntityResult resPassword = eUsu.query(cv, new Vector<>(), this.getReferenceLocator().getSessionId());
			if (resPassword.calculateRecordNumber() == 1) {
				String oldPassword = (String) resPassword.getRecordValues(0).get("PASSWORD");
				if (!oldPassword.equals(this.fCambioPassword.getDataFieldValue("OldPassword"))) {
					MessageManager.getMessageManager().showMessage((Component) event.getSource(), "M_CONTRASENHA_Y_CONTRASENHA_ANTERIOR_NO_COINCIDEN_DEBEN_COINCIDIR",
							MessageType.ERROR, new Object[] {});
					return;
				}
			}

			Hashtable<String, Object> av = new Hashtable<String, Object>();
			av.put("PASSWORD", this.fCambioPassword.getDataFieldValue("Password"));

			EntityResult res = eUsu.update(av, cv, this.getReferenceLocator().getSessionId());
			if (res.getCode() == EntityResult.OPERATION_WRONG) {
				MessageManager.getMessageManager().showMessage((Component) event.getSource(), res.getMessage(), MessageType.ERROR, new Object[] {});
			} else {
				MessageManager.getMessageManager().showMessage((Component) event.getSource(), "M_CONTRASENHA_MODIFICADA_CON_EXITO", MessageType.INFORMATION, new Object[] {});
				SwingUtilities.getWindowAncestor((Component) event.getSource()).setVisible(false);
				SwingUtilities.getWindowAncestor((Component) event.getSource()).dispose();
			}
		} catch (Exception ex) {
			PasswordChanger.logger.error(null, ex);
		}
	}

	/**
	 * Gets the reference locator.
	 *
	 * @return the reference locator
	 */
	private EntityReferenceLocator getReferenceLocator() {
		return ApplicationManager.getApplication().getReferenceLocator();
	}

}
