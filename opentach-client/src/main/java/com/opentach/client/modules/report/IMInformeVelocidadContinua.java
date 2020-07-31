package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

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

public class IMInformeVelocidadContinua extends IMReportRoot {

	private static final Logger	logger		= LoggerFactory.getLogger(IMInformeVelocidadContinua.class);

	private static final String	pNUMREQ		= "numreq";
	private static final String	pCIF		= "cif";
	private static final String	pEMPRESA	= "empresa";
	private static final String	pTITLE		= "title";
	private static final String	pFINFORME	= "f_informe";

	private Button				bInforme	= null;

	public IMInformeVelocidadContinua() {

		super("EInformeVelocidadContinua", "informe_activ_cond");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_activ_cond.xml", "EInformeActivPeriodosCond");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags("FECHORA"));
		this.dateEntity = "EUFecha";

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
						IMInformeVelocidadContinua.this.accBotInforme();
					} catch (Exception err) {
						MessageManager.getMessageManager().showExceptionMessage(err, IMInformeVelocidadContinua.logger);
					}
				}
			});
		}
	}

	@Override
	public void setInitialState() {

		super.setInitialState();
		this.managedForm.enableButton("btnInforme2");
	}

	private void accBotInforme() throws Exception {
		if (!this.checkRequiredVisibleDataFields(true)) {
			return;
		}
		Table tb = (Table) this.managedForm.getElementReference("EInformeVelocidadContinua");
		this.createReports(22, null, new EntityResult((Hashtable) tb.getValue()));
	}

	@Override
	protected Map<String, Object> getParams(String title, String delegCol) {

		Map<String, Object> mParams = new HashMap<String, Object>();
		try {
			ResourceBundle res = this.managedForm.getResourceBundle();
			UReferenceDataField cCif = (UReferenceDataField) this.CIF;
			String cif = (String) cCif.getValue();
			Map htRow = cCif.getCodeValues(cif);
			String empresa = (String) htRow.get("NOMB");
			String cgContrato = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD);
			cgContrato = this.checkContratoFicticio(cgContrato);
			Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
			Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
			mParams.put(IMInformeVelocidadContinua.pNUMREQ, cgContrato);
			mParams.put(IMInformeVelocidadContinua.pCIF, cif);
			mParams.put(IMInformeVelocidadContinua.pEMPRESA, empresa);
			mParams.put("f_informe", new Timestamp(fecFin.getTime()));
			mParams.put("f_fin", new Timestamp(fecFin.getTime()));
			mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
			mParams.put(IMInformeVelocidadContinua.pTITLE, ApplicationManager.getTranslation(title, res));
			mParams.put(IMInformeVelocidadContinua.pFINFORME, new Timestamp(fecFin.getTime()));
			mParams.put("TIPO_INFORME", "1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mParams;
	}
}
