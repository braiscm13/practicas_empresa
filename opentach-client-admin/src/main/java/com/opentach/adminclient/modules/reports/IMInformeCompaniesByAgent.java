package com.opentach.adminclient.modules.reports;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.modules.IMReportRoot;

public class IMInformeCompaniesByAgent extends IMReportRoot {

	private static final Logger logger = LoggerFactory.getLogger(IMInformeCompaniesByAgent.class);
	public IMInformeCompaniesByAgent() {
		super("EInformeCompaniesByAgent", "informe_companies_by_Agent");
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("EInformeCompaniesByAgent", "EInformeCompaniesByAgent");
		this.setEntidadesInformes(mEntityReport);
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {

		super.registerInteractionManager(f, gf);
		Button bt = f.getButton("btnInforme2");
		bt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				try {
					Table tb = (Table) IMInformeCompaniesByAgent.this.managedForm.getElementReference("EInformeCompaniesByAgent");
					IMInformeCompaniesByAgent.this.createReports(57,"EInformeCompaniesByAgent",	new EntityResult((Hashtable) tb.getValue()));
				} catch (Exception err) {
					MessageManager.getMessageManager().showExceptionMessage(err, IMInformeCompaniesByAgent.logger);
				}
			}
		});
	}

	@Override
	protected Map<String, Object> getParams(String title, String delegCol) {
		Map<String, Object> mParams = new HashMap<String, Object>();
		Date fecFin = (Date) this.managedForm.getDataFieldValue("FILTERFECFIN");
		Date fecIni = (Date) this.managedForm.getDataFieldValue("FILTERFECINI");
		mParams.put("title", ApplicationManager.getTranslation(title));
		if (fecIni!=null) {
			mParams.put("f_inicio", new Timestamp(fecIni.getTime()));
		}
		if (fecFin!=null) {
			mParams.put("f_fin", new Timestamp(fecFin.getTime()));
		}

		return mParams;
	}

	@Override
	public void doOnQuery(final boolean alert) {
		if (this.managedForm.existEmptyRequiredDataField()) {
			if (alert) {
				this.managedForm.message("Establezca filtro de búsqueda.", Form.INFORMATION_MESSAGE);
			}
		} else if (this.checkQuery()) {
			this.refreshTable(this.tablename);
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.doOnQuery(false);
		this.managedForm.getButton("btnInforme2").setEnabled(true);
	}

	@Override
	protected List<DataField> getFilterFields() {
		List<DataField> filterFields = super.getFilterFields();
		filterFields.add((DataField) this.managedForm.getDataFieldReference("AGENTE"));
		return filterFields;
	}

}
