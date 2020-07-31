package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Calendar;
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

public class IMInformeUsoVehiculoCond extends IMReportRoot {

	public IMInformeUsoVehiculoCond() {
		super("EInformeUsoVehiculoConductor", "informe_usovehiculo_cond");
		this.dateEntity = "EUFecha";
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_usovehiculo_cond", "EInformeUsoVehiculoConductor");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDCONDUCTOR_FIELD, true);

		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeUsoVehiculoCond.this.createReports();
			}
		});
	}

	protected void createReports() {
		final Map<String, Object> params = this.getParams("vehiculos_por_conductor", "iddelegacion");
		final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(this.managedForm);
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();

		try {
			final String urljr = "com/opentach/reports/vehiculosporconductor.jasper";
			OperationThread opth = new OperationThread() {
				@Override
				public void run() {
					this.hasStarted = true;
					JasperPrint jp = null;
					try {
						jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, params, null, "beforeVehiculosPorConductor", null,
								ocl.getSessionId());
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
				JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation("vehiculos_por_conductor"), jd, jp);
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
		mParams.put("TIPO_INFORME", "2");
		mParams.put("title", title);
		mParams.put("f_informe", new Timestamp(fecFin.getTime()));
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));

		Calendar c = Calendar.getInstance();
		c.setTime(fecFin);
		// c.set(Calendar.HOUR_OF_DAY, 23);
		// c.set(Calendar.MINUTE, 59);
		// c.set(Calendar.SECOND, 59);
		// c.getTime();

		mParams.put("f_fin", new Timestamp(c.getTimeInMillis() + (24 * 3600000)));
		mParams.put("titulo_horas", "Uso_de_vehículo_por_horas");
		mParams.put("titulo_semana", "Uso_de_vehículo_por_día_semana");
		mParams.put("titulo_semana_mes", "Uso_de_vehículo_por_semana_mes");
		mParams.put("porc_vehiculo_cond", "porc_vehiculo_cond");

		mParams.put("porc_vehiculo_km", "porc_vehiculo_km");
		mParams.put("veh_dia_semana", "veh_dia_semana");

		String aux = " and ( idconductor = ";
		Object oconductor = this.managedForm.getDataFieldValue("IDCONDUCTOR");
		if (oconductor == null) {
			mParams.put("idconductor", "");
		} else if (oconductor instanceof String) {
			aux += "'" + (String) this.managedForm.getDataFieldValue("IDCONDUCTOR") + "' ) ";
			mParams.put("idconductor", aux);
		} else if (oconductor instanceof SearchValue) {
			Vector v = (Vector) ((SearchValue) this.managedForm.getDataFieldValue("IDCONDUCTOR")).getValue();
			for (int i = 0; i < v.size(); i++) {
				if (i == 0) {
					aux += "'" + v.get(i) + "'";
				} else {
					aux += " idconductor = '" + v.get(i) + "'";
				}
				if ((i + 1) != v.size()) {
					aux += "  or ";
				}
			}
			aux += " ) ";
			if (aux.length() > 22) {
				mParams.put("idconductor", aux);
			}
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
