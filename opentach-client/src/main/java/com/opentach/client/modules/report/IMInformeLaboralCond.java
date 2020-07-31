package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.DataComponentGroup;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ertools.SumAggregateFunction;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.ReferenceLocator;
import com.opentach.client.comp.render.MinutesCellRender;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.interfaces.IConsultarResumenActividades;
import com.opentach.common.report.util.JRAutorewindTableModelDataSource;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMInformeLaboralCond extends IMReportRoot {

	private static final Logger	logger	= LoggerFactory.getLogger(IMInformeLaboralCond.class);

	private UReferenceDataField	cIdCond;
	private Table				tbInf;

	public IMInformeLaboralCond() {
		super("EInformeLaboral", "informe_laboral");
		this.dateEntity = "EUFecha";
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_laboral", "EInformeLaboral");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.cIdCond = (UReferenceDataField) f.getDataFieldReference(OpentachFieldNames.IDCONDUCTOR_FIELD);
		this.tbInf = (Table) f.getDataFieldReference("EInformeLaboral");

		Button bInforme = f.getButton("btnInforme2");
		if (bInforme != null) {
			bInforme.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMInformeLaboralCond.this.accBotInforme();
				}
			});
		}
	}

	private void accBotInforme() {
		if (!this.checkRequiredVisibleDataFields(true)) {
			return;
		}

		ReferenceLocator bref = (ReferenceLocator) this.formManager.getReferenceLocator();
		Object idCond = this.cIdCond.getValue();
		Date fIni = (Date) this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECINI);
		Date fFin = (Date) this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECFIN);
		String cgCont = (String) this.cgContract.getValue();
		if ((fIni == null) || (fFin == null)) {
			return;
		}
		try {
			Entity ent = bref.getEntityReference("EInformeResumenActividades");
			EntityResult res = ((IConsultarResumenActividades) ent).consultarInformeLaboral(cgCont, idCond, fIni, fFin, bref.getSessionId());

			EntityResult res2 = new EntityResult();
			res2.putAll(res);

			// Vemos la semana
			EntityResult resGroupSemana = EntityResultTools.doGroup(res2, new String[] { "IDCONDUCTOR", "DNI", "ANHO", "SEMANA" }, new SumAggregateFunction("TDIARIO", "SUM"));

			Vector<Object> vSemana = (Vector<Object>) res.get("SEMANA");
			Vector<Object> vAnyo = (Vector<Object>) res.get("ANHO");

			Vector<Object> nuevo = new Vector<Object>();

			Vector<Object> vSemanaGroup = (Vector<Object>) resGroupSemana.get("SEMANA");
			Vector<Object> vAnyoGroup = (Vector<Object>) resGroupSemana.get("ANHO");

			Vector<Object> vTotalGroup = (Vector<Object>) resGroupSemana.get("SUM");

			for (int i = 0; i < vSemana.size(); i++) {
				for (int j = 0; j < vSemanaGroup.size(); j++) {
					if (vSemana.get(i).equals(vSemanaGroup.get(j)) && vAnyo.get(i).equals(vAnyoGroup.get(j))) {
						nuevo.add(vTotalGroup.get(j));
					}
				}
			}
			// Vemos el mes
			EntityResult resGroupMes = EntityResultTools.doGroup(res2, new String[] { "IDCONDUCTOR", "DNI", "ANHO", "MES2" }, new SumAggregateFunction("TDIARIO", "SUM"));

			Vector<Object> vMes = (Vector<Object>) res.get("MES2");

			Vector<Object> nuevoMes = new Vector<Object>();

			Vector<Object> vMesGroup = (Vector<Object>) resGroupMes.get("MES2");

			Vector<Object> vTotalGroupMes = (Vector<Object>) resGroupMes.get("SUM");

			for (int i = 0; i < vMes.size(); i++) {
				for (int j = 0; j < vMesGroup.size(); j++) {
					if (vMes.get(i).equals(vMesGroup.get(j)) && vAnyo.get(i).equals(vAnyoGroup.get(j))) {
						nuevoMes.add(vTotalGroupMes.get(j));
					}
				}
			}

			EntityResultTools.doSort(res, "IDCONDUCTOR", "DATERANGE");

			if ((res.getCode() == EntityResult.OPERATION_SUCCESSFUL) && (res.calculateRecordNumber() > 0)) {
				this.createReports(30, "EInformeLaboral", res);
			}
		} catch (Exception e) {
			MessageManager.getMessageManager().showExceptionMessage(e, IMInformeLaboralCond.logger);
		}
	}

	@Override
	protected Hashtable<String, Object> getParams(String title, String delegCol) {

		Hashtable<String, Object> mParams = new Hashtable<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		Map<String, Object> htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");

		try {
			ResourceBundle res = this.managedForm.getResourceBundle();
			mParams.put("empresa", empresa);
			mParams.put("title", ApplicationManager.getTranslation(title, res));
			mParams.put("f_informe", new Timestamp(fecFin.getTime()));
			mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
			mParams.put("f_fin", new Timestamp(fecFin.getTime() + (24 * 3600000)));
			mParams.put("TIPO_INFORME", "1");

			// PRIMAFRIO
			UReferenceDataField cCifCertif = (UReferenceDataField) this.managedForm.getDataFieldReference("CIF_CERTIFICADO");
			if (cCifCertif != null) {
				String sCifCertif = (String) cCifCertif.getValue();
				if (sCifCertif != null) {
					String empresaCert = (String) cCifCertif.getCodeValues(sCifCertif).get("NOMB");
					mParams.put("empresa", empresaCert);
				}

			}

			int i = 1;
			DataComponentGroup filtergroup;
			while ((filtergroup = (DataComponentGroup) this.managedForm.getElementReference(this.bandcfgname + "." + i)) != null) {
				Vector<String> attrs = filtergroup.getAttributes();
				for (Enumeration<String> enumeration = attrs.elements(); enumeration.hasMoreElements();) {
					String attr = enumeration.nextElement();
					DataField campo = (DataField) this.managedForm.getDataFieldReference(attr);
					if (campo != null) {
						mParams.put(attr, ((Integer) campo.getValue()).intValue() == 0 ? false : true);
					}
				}
				i++;
			}

			Table tb = (Table) this.managedForm.getElementReference("EInformeLaboral");
			EntityResult avTable = (EntityResult) tb.getValue();
			Hashtable<String, Object> resTable = new Hashtable<String, Object>();
			EntityResult restotal = new EntityResult();
			for (int j = 0; j < avTable.calculateRecordNumber(); j++) {
				Hashtable<String, Object> av = avTable.getRecordValues(j);

				av.put("TIPO_T", ApplicationManager.getTranslation("DCDIARIO", res));
				av.put("VALOR", av.get("DCDIARIO"));
				restotal.addRecord(av);
				av.put("TIPO_T", ApplicationManager.getTranslation("DTDIARIO", res));
				av.put("VALOR", av.get("DTDIARIO"));
				restotal.addRecord(av);

				av.put("TIPO_T", ApplicationManager.getTranslation("DPDIARIO", res));
				av.put("VALOR", av.get("DPDIARIO"));
				restotal.addRecord(av);

				av.put("TIPO_T", ApplicationManager.getTranslation("DPINDEF", res));
				av.put("VALOR", av.get("DPINDEF"));
				restotal.addRecord(av);

				av.put("TIPO_T", ApplicationManager.getTranslation("DDDIARIO", res));
				av.put("VALOR", av.get("DDDIARIO"));
				restotal.addRecord(av);
			}
			restotal.remove("DDDIARIO");
			restotal.remove("DPINDEF");
			restotal.remove("DPDIARIO");
			restotal.remove("DTDIARIO");
			restotal.remove("DCDIARIO");

			EntityResult resWeek = EntityResultTools.doSort(restotal, "DNI", "ANHO", "MES", "SEMANA");
			resWeek = EntityResultTools.doGroup(resWeek, new String[] { "DNI", "ANHO", "MES", "SEMANA", "TIPO_T" }, new SumAggregateFunction("VALOR", "SUM"));
			resWeek.put("VALOR", resWeek.remove("SUM"));
			mParams.put("DATASOURCE_RESUMEN", new JRAutorewindTableModelDataSource(com.ontimize.db.EntityResultUtils.createTableModel(resWeek)));

			EntityResult resMonth = EntityResultTools.doSort(restotal, "DNI", "ANHO", "MES");
			resMonth = EntityResultTools.doGroup(resMonth, new String[] { "DNI", "ANHO", "MES", "TIPO_T" }, new SumAggregateFunction("VALOR", "SUM"));
			resMonth.put("VALOR", resMonth.remove("SUM"));
			mParams.put("DATASOURCE_RESUMEN_MES", new JRAutorewindTableModelDataSource(com.ontimize.db.EntityResultUtils.createTableModel(resMonth)));

			EntityResult resDNI = EntityResultTools.doSort(restotal, "DNI");
			resDNI = EntityResultTools.doGroup(resDNI, new String[] { "DNI", "TIPO_T" }, new SumAggregateFunction("VALOR", "SUM"));
			resDNI.put("VALOR", resDNI.remove("SUM"));
			mParams.put("DATASOURCE_RESUMEN_DNI", new JRAutorewindTableModelDataSource(com.ontimize.db.EntityResultUtils.createTableModel(resDNI)));

		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return mParams;
	}

	@Override
	protected void setDateTableRenders() {
		super.setDateTableRenders();
		Table tb = (Table) this.managedForm.getElementReference("EInformeLaboral");
		if (tb != null) {
			tb.setRendererForColumn("DDDIARIO", new MinutesCellRender("DDDIARIO"));
			tb.setRendererForColumn("DTDIARIO", new MinutesCellRender("DTDIARIO"));
			tb.setRendererForColumn("DCDIARIO", new MinutesCellRender("DCDIARIO"));
			tb.setRendererForColumn("DPDIARIO", new MinutesCellRender("DPDIARIO"));
			tb.setRendererForColumn("DPINDEF", new MinutesCellRender("DPINDEF"));
		}
	}

	@Override
	protected void refreshTable(String tablename) {
		ReferenceLocator bref = (ReferenceLocator) this.formManager.getReferenceLocator();
		Object idCond = this.cIdCond.getValue();
		Date fIni = (Date) this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECINI);
		Date fFin = (Date) this.managedForm.getDataFieldValue(OpentachFieldNames.FILTERFECFIN);
		String cgCont = (String) this.cgContract.getValue();
		if ((fIni == null) || (fFin == null)) {
			return;
		}
		try {
			cgCont = this.checkContratoFicticio(cgCont);
			Entity ent = bref.getEntityReference("EInformeResumenActividades");
			EntityResult res = ((IConsultarResumenActividades) ent).consultarInformeLaboral(cgCont, idCond, fIni, fFin, bref.getSessionId());
			EntityResultTools.doSort(res, "IDCONDUCTOR", "DATERANGE");
			this.tbInf.setValue(res);
			this.tbInf.setEnabled((res.calculateRecordNumber() > 0) ? true : false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refreshTables() {
		this.refreshTable("EInformeLaboral");
	}

}
