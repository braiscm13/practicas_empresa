package com.opentach.client.modules.inspec;

import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.table.Table;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.activities.IInfractionService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class IMGestionInspecAnalyzeActionListenerButton extends AbstractActionListenerButton {

	private static final Logger	logger	= LoggerFactory.getLogger(IMGestionInspecAnalyzeActionListenerButton.class);

	@FormComponent(attr = IMGestionInspec.EFICHEROSCONDU)
	private Table				tbCond;
	@FormComponent(attr = IMGestionInspec.EINFRACCIONES)
	private Table				tbInfrac;

	@FormComponent(attr = OpentachFieldNames.CG_CONTRATO_FIELD)
	private DataComponent		cgContract;
	@FormComponent(attr = OpentachFieldNames.CIF_FIELD)
	private DataComponent		CIF;

	public IMGestionInspecAnalyzeActionListenerButton() throws Exception {
		super();
	}

	public IMGestionInspecAnalyzeActionListenerButton(Hashtable params) throws Exception {
		super(params);
	}

	public IMGestionInspecAnalyzeActionListenerButton(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMGestionInspecAnalyzeActionListenerButton(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (!this.getBasicInteractionManager().checkUpdate()) {
			return;
		}
		if (this.tbInfrac != null) {
			this.tbInfrac.deleteData();
			this.tbInfrac.setEnabled(true);
			OperationThread op = new OperationThread() {
				@Override
				public String getDescription() {
					return ApplicationManager.getTranslation("Analizando");
				}

				@Override
				@SuppressWarnings("unchecked")
				public void run() {
					this.hasStarted = true;
					try {
						Entity ent = IMGestionInspecAnalyzeActionListenerButton.this.getEntity("EInformeInfrac");
						if (ent != null) {
							Hashtable ht = new Hashtable();
							String cgContrato = (String) IMGestionInspecAnalyzeActionListenerButton.this.cgContract.getValue();
							String cif = (String) IMGestionInspecAnalyzeActionListenerButton.this.CIF.getValue();
							ht.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
							ht.put(OpentachFieldNames.CIF_FIELD, cif);
							ht.put(IInfractionService.ENGINE_ANALYZER,
									IMGestionInspecAnalyzeActionListenerButton.this.getForm().getDataFieldValue(IInfractionService.ENGINE_ANALYZER));
							Date fini = (Date) IMGestionInspecAnalyzeActionListenerButton.this.getForm().getDataFieldValue(
									OpentachFieldNames.FILTERFECINI);
							Date ffin = (Date) IMGestionInspecAnalyzeActionListenerButton.this.getForm().getDataFieldValue(
									OpentachFieldNames.FILTERFECFIN);
							if ((fini == null) || (ffin == null)) {
								return;
							}
							ht.put(OpentachFieldNames.FILTERFECINI, fini);
							ht.put(OpentachFieldNames.FILTERFECFIN, ffin);
							Hashtable rCond = (Hashtable) IMGestionInspecAnalyzeActionListenerButton.this.tbCond.getValue();
							Vector vCond = (Vector) rCond.get(OpentachFieldNames.IDORIGEN_FIELD);
							if ((vCond == null) || (vCond.size() == 0)) {
								IMGestionInspecAnalyzeActionListenerButton.this.getForm().message("M_NO_EXISTEN_CONDUCTORES_ANALIZAR",
										Form.WARNING_MESSAGE);
								return;
							}
							Set hsCond = new HashSet(vCond);
							ht.put(OpentachFieldNames.IDCONDUCTOR_FIELD, new SearchValue(SearchValue.IN, new Vector(hsCond)));
							Vector v = IMGestionInspecAnalyzeActionListenerButton.this.tbInfrac.getCurrentColumns();
							v.addElement(OpentachFieldNames.DNI_FIELD);
							v.addElement(OpentachFieldNames.NAME_FIELD);
							v.addElement(OpentachFieldNames.SURNAME_FIELD);
							v.addElement("NUM_TRJ_CONDU");
							v.addElement("TIPO_DSCR");
							v.addElement("DSCR_TIPO_INFRAC");
							EntityResult res = ent.query(ht, v, IMGestionInspecAnalyzeActionListenerButton.this.getSessionId());
							if ((res.calculateRecordNumber() == 0) && (res.getCode() == EntityResult.OPERATION_SUCCESSFUL)) {
								IMGestionInspecAnalyzeActionListenerButton.this.getForm().message("M_NO_HAY_INFRACCIONES", Form.MESSAGE);
							} else if ((res != null) && (res.getCode() == EntityResult.OPERATION_SUCCESSFUL)) {
								IMGestionInspecAnalyzeActionListenerButton.this.tbInfrac.setEnabled(true);
								IMGestionInspecAnalyzeActionListenerButton.this.tbInfrac.setValue(res);
							} else if (res != null) {
								IMGestionInspecAnalyzeActionListenerButton.this.getForm().message(res.getMessage(), Form.ERROR_MESSAGE);
								IMGestionInspecAnalyzeActionListenerButton.this.tbInfrac.deleteData();
							} else {
								IMGestionInspecAnalyzeActionListenerButton.this.getForm().message("M_ERROR_INFRACCIONES", Form.ERROR_MESSAGE);
								IMGestionInspecAnalyzeActionListenerButton.this.tbInfrac.deleteData();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						this.hasFinished = true;
					}
				}
			};
			ExtendedApplicationManager.proccessOperation((JFrame) SwingUtilities.getWindowAncestor(this.getForm()), op, 50);
		}

	}

}
