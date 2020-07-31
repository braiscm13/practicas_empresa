package com.opentach.adminclient.modules.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.ontimize.db.Entity;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.MemoDataField;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.util.blackberryRenderer;
import com.opentach.common.blackberry.IBlackberryService;
import com.opentach.common.user.IUserData;
import com.utilmize.client.fim.UBasicFIM;

public class IMPushBlackberry extends UBasicFIM {

	private JPanel			jpsmspush	= null;
	private MemoDataField	cdsmspush	= null;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {

		super.registerInteractionManager(form, formManager);
		Button bpush = this.managedForm.getButton("bpush");
		Table tblackberry = (Table) this.managedForm.getDataFieldReference("EBlackberry");
		if (tblackberry != null) {
			tblackberry.setCellRendererColorManager(new blackberryRenderer());
			tblackberry.addComponentToControls(bpush);
			this.managedForm.remove(bpush);
		}

		final EntityReferenceLocator bref = formManager.getReferenceLocator();

		if (bpush != null) {
			bpush.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					Table tblackberry = (Table) IMPushBlackberry.this.managedForm.getDataFieldReference("EBlackberry");
					int[] selectedRows = tblackberry.getSelectedRows();
					if (selectedRows.length > 0) {
						boolean encontrado = false;
						Vector v = new Vector();
						for (int i = 0; i < selectedRows.length; i++) {
							Hashtable av = tblackberry.getRowData(selectedRows[i]);
							if (av.get("PIN") != null) {
								v.add(av.get("PIN"));
							} else {
								encontrado = true;
							}
						}
						if (encontrado) {
							IMPushBlackberry.this.managedForm.message("BLACKBERRY_SIN_PIN", Form.INFORMATION_MESSAGE);
						}

						if (IMPushBlackberry.this.jpsmspush == null) {
							IMPushBlackberry.this.jpsmspush = new JPanel(new BorderLayout());
							IMPushBlackberry.this.jpsmspush.setPreferredSize(new Dimension(100, 200));
							Hashtable params = new Hashtable();

							params.put(DataField.ATTR, "TEXTO_SMS");
							params.put(DataField.DIM, "text");
							params.put("maxlength", "100");
							params.put(DataField.LABELSIZE, "12");
							params.put("expand", "yes");
							params.put(DataField.LABELVISIBLE, "yes");
							params.put(DataField.LABELPOSITION, "top");
							params.put("rows", "5");
							try {
								IMPushBlackberry.this.cdsmspush = new MemoDataField(params);
								IMPushBlackberry.this.cdsmspush.setParentForm(IMPushBlackberry.this.managedForm);
								IMPushBlackberry.this.cdsmspush.setResourceBundle(IMPushBlackberry.this.managedForm.getResourceBundle());
								IMPushBlackberry.this.jpsmspush.add(IMPushBlackberry.this.cdsmspush, BorderLayout.CENTER);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						IMPushBlackberry.this.cdsmspush.setValue(null);
						int option = JOptionPane.showConfirmDialog(IMPushBlackberry.this.managedForm, IMPushBlackberry.this.jpsmspush,
								ApplicationManager.getTranslation("M_WRITE_SMS"), JOptionPane.OK_CANCEL_OPTION);
						if (option == 0) {

							String sms = (String) IMPushBlackberry.this.cdsmspush.getValue();
							Entity entidad;
							try {
								entidad = bref.getEntityReference("EBlackberry");
								((IBlackberryService) entidad).sendPushBlackberry(v, sms);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					} else {
						IMPushBlackberry.this.managedForm.message("SELECT_A_BLACKBERRY", Form.INFORMATION_MESSAGE);
					}

				}
			});
		}

		UReferenceDataField cif = (UReferenceDataField) this.managedForm.getDataFieldReference("CIF");
		if (cif != null) {
			cif.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChanged(ValueEvent e) {
					if (e.getNewValue() != null) {
						((Table) IMPushBlackberry.this.managedForm.getDataFieldReference("EBlackberry")).refresh();
					}
				}
			});
		}

	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		super.setQueryMode();

		this.managedForm.getDataFieldReference("EBlackberry").setEnabled(true);
		this.managedForm.getDataFieldReference("CIF").setEnabled(true);

		OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
		try {
			IUserData ud = ocl.getUserData();
			if (ud.getCompaniesList().size() == 1) {
				this.managedForm.setDataFieldValue("CIF", ud.getCIF());
			}
		} catch (Exception e) {
		}

	}
}
