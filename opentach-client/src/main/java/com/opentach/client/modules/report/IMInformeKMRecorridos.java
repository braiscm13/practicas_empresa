package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMInformeKMRecorridos extends IMReportRoot {

	private static final Logger	logger		= LoggerFactory.getLogger(IMInformeKMRecorridos.class);
	private Button	bInforme	= null;

	public IMInformeKMRecorridos() {
		super("EInformeKMRecorridos", "informe_km_recorridos");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_km_recorridos", "EInformeKMRecorridos");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags("FECHA"));
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.bInforme = f.getButton("btnInforme2");
		this.cgContract = f.getDataFieldReference(OpentachFieldNames.CG_CONTRATO_FIELD);
		if (this.bInforme != null) {
			this.bInforme.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						IMInformeKMRecorridos.this.accBotInforme();
					} catch (Exception err) {
						MessageManager.getMessageManager().showExceptionMessage(err, IMInformeKMRecorridos.logger);
					}
				}
			});
		}
	}

	private void accBotInforme() throws Exception {
		if (!this.checkRequiredVisibleDataFields(true)) {
			return;
		}
		this.createReports(25, "EInformeKMRecorridos", null);
	}

	@Override
	protected Hashtable<String, Object> getParams(String title, String delegCol) {
		Hashtable<String, Object> mParams = new Hashtable<String, Object>();
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		String cif = (String) cCif.getValue();
		Map htRow = cCif.getCodeValues(cif);
		String empresa = (String) htRow.get("NOMB");
		String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");

		Calendar c = Calendar.getInstance();
		c.setTime(fecFin);
		c.add(Calendar.DAY_OF_YEAR, +1);

		ResourceBundle res = this.managedForm.getResourceBundle();
		mParams.put("numreq", cgContrato);
		mParams.put("empresa", empresa);
		mParams.put("title", ApplicationManager.getTranslation(title, res));
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(c.getTime().getTime()));
		mParams.put("TIPO_INFORME", "1");
		return mParams;
	}

}
