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
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.comp.render.MinutesCellRender;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.report.util.JRAutorewindTableModelDataSource;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMInformeIncidentes extends IMReportRoot {

	private static final Logger logger = LoggerFactory.getLogger(IMInformeIncidentes.class);
	public IMInformeIncidentes() {
		super("EIncidentes", "informe_incidentes");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_incidentes", "EIncidentes");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD, OpentachFieldNames.FECFIN_FIELD));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		Table tbIncidentes = (Table) f.getElementReference("EIncidentes");
		if (tbIncidentes != null) {
			MinutesCellRender mincr = new MinutesCellRender("DURACION_SEGUNDOS");
			mincr.setSecondsResolution(true);
			tbIncidentes.setRendererForColumn("DURACION_SEGUNDOS", mincr);
		}

		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				try {
					IMInformeIncidentes.this.createReports(28, "EIncidentes", null);
				} catch (Exception err) {
					MessageManager.getMessageManager().showExceptionMessage(err, IMInformeIncidentes.logger);
				}
			}
		});
	}

	@Override
	protected EntityResult getMainEntityResult(String entityName, EntityResult resEntity) {
		EntityResult er = super.getMainEntityResult(entityName, resEntity);
		er = EntityResultTools.doSort(er, "TIPO_DSCR_INC");
		return er;
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
		try {
			ResourceBundle res = this.managedForm.getResourceBundle();
			mParams.put("numreq", cgContrato);
			mParams.put("empresa", empresa);
			mParams.put("title", ApplicationManager.getTranslation(title, res));
			mParams.put("f_informe", new Timestamp(fecFin.getTime()));
			mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
			mParams.put("f_fin", new Timestamp(fecFin.getTime()));
			mParams.put("TIPO_INFORME", "1");
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

			Hashtable<String, Object> av = new Hashtable<String, Object>();
			av.put("numreq", cgContrato);
			av.put("f_inicio", new Timestamp(fecIni.getTime()));
			av.put("f_fin", new Timestamp(fecFin.getTime()));
			av.put("matricula", this.managedForm.getDataFieldValue("MATRICULA"));
			Entity eUsoVeh = this.formManager.getReferenceLocator().getEntityReference("EIncidentesResumen");
			EntityResult er = eUsoVeh.query(av, new Vector(), this.formManager.getReferenceLocator().getSessionId());

			mParams.put("DATASOURCE_RESUMEN",
					new JRAutorewindTableModelDataSource(com.ontimize.db.EntityResultUtils.createTableModel(er, false, false, false)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mParams;
	}
}
