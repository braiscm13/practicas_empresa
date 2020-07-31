package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IOpentachReportService;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeExpressCond extends IMRoot {

	@FormComponent(attr = OpentachFieldNames.IDCONDUCTOR_FIELD)
	private UReferenceDataField	idConductorField;

	private Button	bInforme	= null;

	public IMInformeExpressCond() {
		super();
		this.fieldsChain.clear();
		this.fieldsChain.add(OpentachFieldNames.CIF_FIELD);
		this.fieldsChain.add(OpentachFieldNames.CG_CONTRATO_FIELD);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.cgContract = f.getDataFieldReference(OpentachFieldNames.CG_CONTRATO_FIELD);
		this.bInforme = f.getButton("btnInforme");
		if (this.bInforme != null) {
			this.bInforme.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!IMInformeExpressCond.this.checkRequiredVisibleDataFields(true)) {
						return;
					}
					IMInformeExpressCond.this.createReports();
				}
			});
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.managedForm.enableButton("btnInforme");
		this.managedForm.enableDataField("FILTERFECINI");
		this.managedForm.enableDataField("FILTERFECFIN");
		this.idConductorField.setAdvancedQueryMode(true);
	}

	private void createReports() {
		try {
			UReferenceDataField cCif = (UReferenceDataField) this.CIF;
			final String cif = (String) cCif.getValue();
			final Map<String, Object> htRow = cCif.getCodeValues(cif);
			final String empresa = (String) htRow.get("NOMB");
			final String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
			String idconductor = this.getDriverId();
			Map<String, Object> av = this.idConductorField.getValuesToCode(idconductor);
			final String dni = (String) av.get("DNI");
			final String apellidos = (String) av.get("APELLIDOS");
			final String nombre = (String) av.get("NOMBRE");

			final Date fdesde = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
			final Date fhasta = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
			final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
			OperationThread opth = new OperationThread() {
				@Override
				public void run() {
					this.hasStarted = true;
					try {
						String conductor = dni + " " + apellidos + "," + nombre;
						JasperPrint jv = ocl.getRemoteService(IOpentachReportService.class).generaInformeExpressCond(cif, empresa, cgContrato, idconductor,
								conductor, dni, fdesde, fhasta, ocl.getSessionId());
						this.res = jv;
					} catch (Exception e) {
						e.printStackTrace();
						this.res = e;
					} finally {
						this.hasFinished = true;
					}
				}
			};
			ExtendedApplicationManager.proccessOperation((JDialog) SwingUtilities.getWindowAncestor(this.managedForm), opth, 1000);
			Object res = opth.getResult();
			if (res instanceof JasperPrint) {
				JRDialogViewer jdv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation("express_cond"), (JDialog) SwingUtilities.getWindowAncestor(this.managedForm),
						(JasperPrint) res);
				jdv.setVisible(true);
			} else {
				Exception e = (Exception) res;
				if (e != null) {
					if ("M_NO_SE_HAN_ENCONTRADO_DATOS".equals(e.getMessage())) {
						this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.ERROR_MESSAGE);
					} else {
						this.managedForm.message("M_PETICION_ERROR", Form.ERROR_MESSAGE);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.managedForm.message("M_PETICION_ERROR", Form.ERROR_MESSAGE);
		}
	}

	private String getDriverId() {
		Object value = this.idConductorField.getValue();
		if (value instanceof SearchValue) {
			SearchValue sb = (SearchValue) value;
			return (String) ((Vector) sb.getValue()).get(0);
		}
		return (String) value;
	}
}
