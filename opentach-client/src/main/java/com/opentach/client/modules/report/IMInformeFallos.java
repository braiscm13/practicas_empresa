package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.comp.render.MinutesCellRender;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

import net.sf.jasperreports.engine.data.JRTableModelDataSource;

public class IMInformeFallos extends IMReportRoot {

	private static final Logger logger = LoggerFactory.getLogger(IMInformeFallos.class);

	public IMInformeFallos() {
		super("EFallos", "informe_fallos");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_fallos", "EFallos");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD, OpentachFieldNames.FECFIN_FIELD));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		Table tbIncidentes = (Table) f.getElementReference("EFallos");
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
					IMInformeFallos.this.createReports(29, "EFallos", null);
				} catch (Exception err) {
					MessageManager.getMessageManager().showExceptionMessage(err, IMInformeFallos.logger);
				}
			}
		});

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
		mParams.put("title", ApplicationManager.getTranslation(title));
		mParams.put("f_informe", new Timestamp(fecFin.getTime()));
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(fecFin.getTime()));
		mParams.put("TIPO_INFORME", "1");

		try {
			Hashtable<String, Object> av = new Hashtable<String, Object>();
			av.put("numreq", cgContrato);
			av.put("f_inicio", new Timestamp(fecIni.getTime()));
			av.put("f_fin", new Timestamp(fecFin.getTime()));
			av.put("matricula", this.managedForm.getDataFieldValue("MATRICULA"));
			Entity eResumen = this.formManager.getReferenceLocator().getEntityReference("EFallosResumen");
			EntityResult er = eResumen.query(av, new Vector(), this.formManager.getReferenceLocator().getSessionId());
			mParams.put("DATASOURCE_RESUMEN", new JRTableModelDataSource(EntityResultUtils.createTableModel(er)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mParams;
	}
}
