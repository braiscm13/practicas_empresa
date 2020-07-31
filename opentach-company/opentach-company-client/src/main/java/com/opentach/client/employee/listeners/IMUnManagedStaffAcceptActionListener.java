package com.opentach.client.employee.listeners;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.IDetailForm;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.client.employee.fim.IMUnManagedStaff;
import com.opentach.common.labor.IUnManagedStaff;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

/**
 * Listener for the "accept" button within {@link IMUnManagedStaff}.
 *
 */
public class IMUnManagedStaffAcceptActionListener extends AbstractActionListenerButton {
	@FormComponent(attr = IUnManagedStaff.ATTR.ENTITY)
	private Table unManagedStaffTable;
	@FormComponent(attr = IUnManagedStaff.ATTR.IDENCARGADO)
	private TextDataField foremanId;
    protected Form usersInfoForm;
    protected JDialog usersInfoDialog;
	/** Logger */
	private static final Logger logger = LoggerFactory.getLogger(IMUnManagedStaffAcceptActionListener.class);

	public IMUnManagedStaffAcceptActionListener() throws Exception {
		super();
	}

	public IMUnManagedStaffAcceptActionListener(AbstractButton button, IUFormComponent formComponent, Map params)
			throws Exception {
		super(button, formComponent, (Hashtable) params);
	}

	public IMUnManagedStaffAcceptActionListener(Map params) throws Exception {
		super((Hashtable) params);
	}

	public IMUnManagedStaffAcceptActionListener(UButton button, Map params) throws Exception {
		super(button, (Hashtable) params);
	}

	@Override
	public void actionPerformed(ActionEvent e){
		// Take all unmanaged staff selected on the table
		// and assign them to the indicated foreman:
		if (this.unManagedStaffTable.getSelectedRowData() != null && this.getForm() != null) {
			String foreman = this.foremanId.getValue().toString();
			
			try {
				EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
				Entity unManagedStaffEntity = locator.getEntityReference(IUnManagedStaff.ATTR.ENTITY);
				int sessionId = locator.getSessionId();
				
				for (int i = 0; i < ((Vector) this.unManagedStaffTable.getSelectedRowData().get(IUnManagedStaff.ATTR.IDCONDUCTOR)).size(); i++) {
					String cif    = (String) ((Vector) this.unManagedStaffTable.getSelectedRowData().get(IUnManagedStaff.ATTR.CIF)).get(i);
					String driver = (String) ((Vector) this.unManagedStaffTable.getSelectedRowData().get(IUnManagedStaff.ATTR.IDCONDUCTOR)).get(i);
					
					EntityResult entityResult = unManagedStaffEntity.update(
							EntityResultTools.keysvalues(IUnManagedStaff.ATTR.IDENCARGADO, foreman),
							EntityResultTools.keysvalues(IUnManagedStaff.ATTR.CIF, cif,
									IUnManagedStaff.ATTR.IDCONDUCTOR, driver),
							sessionId);
					
					CheckingTools.checkValidEntityResult(entityResult);
				}
				
				// We're done. Close this form and refresh its parent.
				SwingUtilities.getAncestorOfClass(Window.class, this.getForm()).setVisible(false);
				IDetailForm parentForm = this.getForm().getDetailComponent();
				if (parentForm != null) {
					parentForm.getTable().refreshInThread(0);
				}
			} catch (Exception exception) {
				IMUnManagedStaffAcceptActionListener.logger.error("MANAGEDSTAFF.ERROR_UPDATING_DATA", exception);
			}
		}
	}
}
