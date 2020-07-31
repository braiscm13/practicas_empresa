package com.opentach.client.modules.report;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMInformeFicherosTGD extends IMReportRoot {

	private static final Logger	logger		= LoggerFactory.getLogger(IMInformeFicherosTGD.class);
	private Button				bInforme	= null;

	private static final String	pNUMREQ		= "numreq";
	private static final String	pCIF		= "cif";
	private static final String	pEMPRESA	= "empresa";
	private static final String	pTITLE		= "title";
	private static final String	pFINFORME	= "f_informe";

	public IMInformeFicherosTGD() {
		super("EHuecosFicheros", "informe_ficheros_tgd");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_ficheros_tgd", "EHuecosFicheros");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD, OpentachFieldNames.FECFIN_FIELD));
		this.dateEntity = "EUFechaFichero";
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDORIGEN_FIELD, true);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Hashtable getFilterValues() {
		Hashtable filtro = super.getFilterValues();
		DateDataField a = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
		DateDataField a1 = (DateDataField) this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
		Timestamp minv = (Timestamp) a.getValue();
		Timestamp maxv = (Timestamp) a1.getValue();
		Date minval = new Date(minv.getTime());
		Date maxval = new Date(maxv.getTime());
		if ((filtro != null) && filtro.containsKey(OpentachFieldNames.FECFIN_FIELD)) {
			filtro.remove(OpentachFieldNames.FECINI_FIELD);
			BasicExpression bs = new BasicExpression(new SQLStatementBuilder.BasicField(OpentachFieldNames.FECINI_FIELD), SQLStatementBuilder.BasicOperator.LESS_EQUAL_OP, minval);
			BasicExpression bs2 = new BasicExpression(new SQLStatementBuilder.BasicField(OpentachFieldNames.FECFIN_FIELD), SQLStatementBuilder.BasicOperator.MORE_EQUAL_OP, minval);
			BasicExpression bs_res = new BasicExpression(bs, SQLStatementBuilder.BasicOperator.AND_OP, bs2);
			BasicExpression bs3 = new BasicExpression(new SQLStatementBuilder.BasicField(OpentachFieldNames.FECINI_FIELD), SQLStatementBuilder.BasicOperator.MORE_EQUAL_OP, minval);
			BasicExpression bs4 = new BasicExpression(new SQLStatementBuilder.BasicField(OpentachFieldNames.FECFIN_FIELD), SQLStatementBuilder.BasicOperator.LESS_EQUAL_OP, maxval);
			BasicExpression bs_res2 = new BasicExpression(bs3, SQLStatementBuilder.BasicOperator.AND_OP, bs4);
			BasicExpression res = new BasicExpression(bs_res, SQLStatementBuilder.BasicOperator.OR_OP, bs_res2);
			filtro.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, res);
			if (a != null) {
				filtro.put(OpentachFieldNames.FILTERFECINI, a);
			}
			if (a1 != null) {
				filtro.put(OpentachFieldNames.FILTERFECFIN, a1);
			}
		}
		return filtro;
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
						IMInformeFicherosTGD.this.accBotInforme();
					} catch (Exception err) {
						MessageManager.getMessageManager().showExceptionMessage(err, IMInformeFicherosTGD.logger);
					}
				}
			});
		}
	}

	private void accBotInforme() throws Exception {
		if (!this.checkRequiredVisibleDataFields(true)) {
			return;
		}
		Table tb = (Table) this.managedForm.getElementReference("EHuecosFicheros");
		this.createReports(24, "EHuecosFicheros", new EntityResult((Hashtable) tb.getValue()));
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
		mParams.put(IMInformeFicherosTGD.pNUMREQ, cgContrato);
		mParams.put(IMInformeFicherosTGD.pCIF, cif);
		mParams.put(IMInformeFicherosTGD.pEMPRESA, empresa);
		mParams.put(IMInformeFicherosTGD.pTITLE, ApplicationManager.getTranslation(title));
		mParams.put(IMInformeFicherosTGD.pFINFORME, new Timestamp(fecFin.getTime()));
		mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		mParams.put("f_fin", new Timestamp(fecFin.getTime() + (24 * 3600000)));
		mParams.put("TIPO_INFORME", "1");
		return mParams;
	}

}
