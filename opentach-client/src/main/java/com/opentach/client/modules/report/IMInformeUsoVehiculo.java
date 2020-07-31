package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.DataComponentGroup;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IReportService;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeUsoVehiculo extends IMReportRoot {

	public IMInformeUsoVehiculo() {
		super("EInformeUsoVehiculoVehiculo", "informe_usovehiculo");
		this.dateEntity = "EUFechaVehi";
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_usovehiculo", "EInformeUsoVehiculoVehiculo");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD));
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.MATRICULA_FIELD, true);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);

		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				// createReports(27, "EInformeUsoVehiculoVehiculo",null);
				IMInformeUsoVehiculo.this.createReports();
			}
		});

	}

	protected void createReports() {

		final Map<String, Object> params = this.getParams("conductores_por_vehiculo", "iddelegacion");
		final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(this.managedForm);
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();

		try {
			final String urljr = "com/opentach/reports/conductoresporvehiculo.jasper";
			OperationThread opth = new OperationThread() {
				@Override
				public void run() {
					this.hasStarted = true;
					JasperPrint jp = null;
					try {
						jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, params, null, null, null, ocl.getSessionId());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						this.hasFinished = true;
						this.res = jp;
					}
				}
			};
			ExtendedApplicationManager.proccessOperation(jd, opth, 1000);
			JasperPrint jp = (JasperPrint) opth.getResult();
			if ((jp != null) && (jp.getPages() != null) && (jp.getPages().size() > 0)) {
				JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation("conductores_por_vehiculo"), jd, jp);
				jv.setVisible(true);
			} else {
				this.managedForm.message("M_NO_SE_HAN_ENCONTRADO_DATOS", Form.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected Hashtable<String, Object> getParams(String title, String delegCol) {
		Hashtable<String, Object> mParams = new Hashtable<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		Map htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		cgContrato = this.checkContratoFicticio(cgContrato);


		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		mParams.put("numreq", cgContrato);
		mParams.put("empresa", empresa);

		mParams.put("title", title);
		mParams.put("f_informe", new Timestamp(fecFin.getTime()));
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(fecFin.getTime()));

		String aux = " ( MATRICULA = ";
		Object omatricula = this.managedForm.getDataFieldValue("MATRICULA");
		if (omatricula instanceof String) {
			mParams.put("matricula", "matricula = '" + (String) this.managedForm.getDataFieldValue("MATRICULA") + "' AND ");
		} else if (omatricula instanceof Vector) {
			Vector v = (Vector) this.managedForm.getDataFieldValue("MATRICULA");
			for (int i = 0; i < v.size(); i++) {
				if (i == 0) {
					aux += "'" + v.get(i) + "' ";
				} else {
					aux += " matricula = '" + v.get(i) + "'";
				}
				if ((i + 1) != v.size()) {
					aux += "  or ";
				}
			}
			aux += " ) AND ";
			mParams.put("matricula", aux);
		} else if (omatricula instanceof SearchValue) {
			Vector v = (Vector) ((SearchValue) this.managedForm.getDataFieldValue("MATRICULA")).getValue();
			for (int i = 0; i < v.size(); i++) {
				if (i == 0) {
					aux += "'" + v.get(i) + "' ";
				} else {
					aux += " matricula = '" + v.get(i) + "'";
				}
				if ((i + 1) != v.size()) {
					aux += "  or ";
				}
			}
			aux += " ) AND ";
			mParams.put("matricula", aux);

		}

		int i = 1;
		DataComponentGroup filtergroup;
		while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.bandcfgname + "." + i)) != null) {
			Vector attrs = filtergroup.getAttributes();
			for (Enumeration enumeration = attrs.elements(); enumeration.hasMoreElements();) {
				String attr = (String) enumeration.nextElement();
				DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
				if (campo != null) {
					mParams.put(attr, ((Integer) campo.getValue()).intValue() == 0 ? false : true);
				}
			}
			i++;
		}

		return mParams;
	}

}
