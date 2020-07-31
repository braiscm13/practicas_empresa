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

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.DataComponentGroup;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.IReportService;
import com.opentach.common.report.util.JRPropertyManager;
import com.opentach.common.report.util.JRReportDescriptor;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.report.JRDialogViewer;

import net.sf.jasperreports.engine.JasperPrint;

public class IMInformeKMs extends IMReportRoot {

	public IMInformeKMs() {
		super("EInformekms", "informe_kms");
		this.dateEntity = "EUFecha";
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_kms", "EInformekms");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags("FECHA"));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDCONDUCTOR_FIELD, true);

		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				IMInformeKMs.this.createReports();
			}
		});
	}

	protected void createReports() {

		final Map<String, Object> params = this.getParams("informe_kms", "iddelegacion");
		final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(this.managedForm);
		final OpentachClientLocator ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();

		try {
			final String urljr = "com/opentach/reports/kmrecorridosempresa.jasper";
			JRPropertyManager jpm = new JRPropertyManager("com/opentach/reports/reports.properties");
			final JRReportDescriptor jrd = jpm.getDataMap().get(43);
			OperationThread opth = new OperationThread() {
				@Override
				public void run() {

					this.hasStarted = true;
					JasperPrint jp = null;
					try {
						Table tb = (Table) IMInformeKMs.this.managedForm.getElementReference("EInformekms");
						EntityResult res = new EntityResult((Hashtable) tb.getValue());
						Hashtable av = new Hashtable<String, Object>(params);
						jp = ocl.getRemoteService(IReportService.class).fillReport(urljr, av, jrd.getMethodAfter(), jrd.getMethodBefore(), res,
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
				JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation("informe_kms"), jd, jp);
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
		mParams.put("title", ApplicationManager.getTranslation(title));
		mParams.put("f_informe", new Timestamp(fecFin.getTime()));
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(fecFin.getTime()));

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

		String aux = " ( IDCONDUCTOR = '";
		Object oconductor = this.managedForm.getDataFieldValue("IDCONDUCTOR");
		if (oconductor instanceof String) {
			mParams.put("idconductor", aux + (String) this.managedForm.getDataFieldValue("IDCONDUCTOR") + "' ) AND ");
			Vector v = new Vector();
			v.add(this.managedForm.getDataFieldValue("IDCONDUCTOR"));
			mParams.put("vconductor", v);
		} else if (oconductor instanceof SearchValue) {
			Vector v = (Vector) ((SearchValue) this.managedForm.getDataFieldValue("IDCONDUCTOR")).getValue();
			for (int j = 0; j < v.size(); j++) {
				if (j == 0) {
					aux += v.get(j) + "'";
				} else {
					aux += " OR  idconductor = '" + v.get(j) + "' ";
				}
			}
			aux += ") and ";

			if (aux.length() > 19) {
				mParams.put("idconductor", aux);

			}
			mParams.put("vconductor", v);
		}

		return mParams;
	}

}
