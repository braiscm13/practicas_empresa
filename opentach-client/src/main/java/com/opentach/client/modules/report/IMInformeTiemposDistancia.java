package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMInformeTiemposDistancia extends IMReportRoot {

	private static final Logger	logger	= LoggerFactory.getLogger(IMInformeTiemposDistancia.class);
	Vector	v	= new Vector();

	public IMInformeTiemposDistancia() {

		super("EInformeTiemposDistanciasCond", "informe_activ_cond");
		this.dateEntity = "EUFecha";
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_activ_cond.xml", "EInformeTiemposDistanciasCond");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags("FECHA"));

		this.v.add("CONDUCCION");
		this.v.add("PAUSA/DESCANSO");
		this.v.add("TRABAJO");
		this.v.add("DISPONIBILIDAD");
		this.v.add("INDEFINIDA");
		this.v.add("PERIODOS_TRABAJO");
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);

		// Table tb =
		// (Table)managedForm.getElementReference("EInformeTiemposDistanciasCond");
		// if (tb!=null){
		// tb.add
		// }
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDCONDUCTOR_FIELD, true);

		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				try {
					EntityResult res = (EntityResult) ((Table) IMInformeTiemposDistancia.this.managedForm
							.getElementReference("EInformeTiemposDistanciasCond")).getValue();
					IMInformeTiemposDistancia.this.createReports(40, "EInformeTiemposDistanciasCond", res);
				} catch (Exception err) {
					MessageManager.getMessageManager().showExceptionMessage(err, IMInformeTiemposDistancia.logger);
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

		Calendar c = Calendar.getInstance();
		c.setTime(fecFin);
		// c.set(Calendar.HOUR_OF_DAY, 23);
		// c.set(Calendar.MINUTE, 59);
		// c.set(Calendar.SECOND, 59);
		// c.getTime();
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		mParams.put("numreq", cgContrato);
		mParams.put("empresa", empresa);

		mParams.put("title", ApplicationManager.getTranslation(title));
		mParams.put("f_informe", new Timestamp(fecFin.getTime()));
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(c.getTimeInMillis() + (24 * 3600000)));
		mParams.put("locale", ApplicationManager.getLocale());
		return mParams;
	}

}
