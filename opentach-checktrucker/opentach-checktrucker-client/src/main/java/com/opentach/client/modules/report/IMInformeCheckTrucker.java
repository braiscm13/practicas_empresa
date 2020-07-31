package com.opentach.client.modules.report;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.comp.CampoFechaNoEditable;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.util.DateUtil;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMInformeCheckTrucker extends IMReportRoot {
	private static final Logger		logger	= LoggerFactory.getLogger(IMInformeCheckTrucker.class);

	@FormComponent(attr = "CIF")
	private UReferenceDataField		cif;

	@FormComponent(attr = "IDCONDUCTOR")
	private UReferenceDataField		idConductor;

	@FormComponent(attr = "IDPERSONAL")
	private UReferenceDataField		idPersonal;

	@FormComponent(attr = "ID_SURVEY")
	private UReferenceDataField		idSurvey;

	@FormComponent(attr = "FILTERFECINI")
	private CampoFechaNoEditable	fIni;

	@FormComponent(attr = "FILTERFECFIN")
	private CampoFechaNoEditable	fFin;

	@FormComponent(attr = "EInformeCheckTrucker")
	private Table					tInforme;

	public IMInformeCheckTrucker() {

		super("EInformeCheckTrucker", "informe_activ_cond");
		this.dateEntity = "EUFecha";
		HashMap<String, String> mEntityReport = new HashMap<String, String>();
		mEntityReport.put("informe_activ_cond.xml", "EInformeCheckTrucker");
		this.setEntidadesInformes(mEntityReport);
		this.setDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD));
		String lastValue = this.fieldsChain.get(this.fieldsChain.size() - 1);
		this.fieldsChain.set(this.fieldsChain.size() - 1, "ID_SURVEY");
		this.fieldsChain.add(lastValue);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.managedForm.getButton("btnInforme2").setVisible(false);
		this.cif.setEnabled(true);
	}

	@Override
	protected void updateChainStatus(String fieldsrc, boolean empty) {
		try {
			// Permitir que sólo permanezca uno de los filtros: IDCONDUCTOR o IDPERSONAL. Cuando se modifica alguno de los campos, borramos el otro
			if (fieldsrc.equals("IDCONDUCTOR") && !this.idConductor.isEmpty()) {
				this.idPersonal.deleteData();
			} else if (fieldsrc.equals("IDPERSONAL") && !this.idPersonal.isEmpty()) {
				this.idConductor.deleteData();
			}
			if ((this.cif.getValue() != null) && (this.fIni.getValue() != null) && (this.fFin.getValue() != null)) {
				this.doOnQuery();
			} else {
				this.tInforme.setValue(null);
			}
			if (this.cif.getValue() != null) {
				this.idConductor.setEnabled(true);
				this.idPersonal.setEnabled(true);
				this.idSurvey.setEnabled(true);
			} else {
				this.idConductor.deleteData();
				this.idConductor.setEnabled(false);
				this.idPersonal.deleteData();
				this.idPersonal.setEnabled(false);
				this.idSurvey.deleteData();
				this.idSurvey.setEnabled(false);
			}
		} catch (Exception ex) {
			IMInformeCheckTrucker.logger.error(null, ex);
		}
	}

	@Override
	protected void ultimosDatos() {
		if ((this.crbUltimosDias != null) && this.crbUltimosDias.isSelected()) {
			this.managedForm.disableDataField(OpentachFieldNames.FILTERFECINI);
			this.managedForm.disableDataField(OpentachFieldNames.FILTERFECFIN);
			try {
				Hashtable av = new Hashtable();
				DataField idCond = (DataField) this.managedForm.getDataFieldReference(OpentachFieldNames.IDCONDUCTOR_FIELD);
				String fieldname = OpentachFieldNames.IDCONDUCTOR_FIELD;
				if (idCond == null) {
					fieldname = OpentachFieldNames.MATRICULA_FIELD;
					idCond = (DataField) this.managedForm.getDataFieldReference(fieldname);
				}
				if (idCond == null) {
					fieldname = OpentachFieldNames.IDORIGEN_FIELD;
					idCond = (DataField) this.managedForm.getDataFieldReference(fieldname);
				}
				Object idConfFilter = idCond == null ? null : idCond.getValue();
				if ((idConfFilter != null) && (idConfFilter instanceof SearchValue)) {
					List options = (List) ((SearchValue) idConfFilter).getValue();
					if (options.size() == 1) {
						idConfFilter = options.get(0);
					}
				}
				if (idConfFilter != null) {
					av.put(fieldname, idConfFilter);
				}
				UReferenceDataField CIF = (UReferenceDataField) this.managedForm.getDataFieldReference("CIF");
				if ((idConfFilter == null) || (CIF.getValue() == null)) {
					return;
				}
				if ((this.dateEntity != null) && (idConfFilter instanceof String)) {
					Entity ufecha = this.formManager.getReferenceLocator().getEntityReference(this.dateEntity);
					int session = this.formManager.getReferenceLocator().getSessionId();
					av.put(OpentachFieldNames.CIF_FIELD, CIF.getValue());
					Vector v = new Vector(0);
					Date end = null;
					try {
						EntityResult res = ufecha.query(av, v, session);
						if (res != null) {
							Hashtable a = res.getRecordValues(0);
							end = (Date) a.get(OpentachFieldNames.FECFIN_FIELD);
						}
					} catch (Exception e) {
						IMInformeCheckTrucker.logger.error(null, e);
					}
					if (end == null) {
						end = new Date();
					}
					end = DateUtil.truncToEnd(end);
					int nofdays = 28;
					if (end != null) {
						Date ini = DateUtil.addDays(end, -nofdays);
						this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, ini);
						this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, end);
					}
				}
				// Si no existe entidad para recupearr la fecha del ultimo dato
				// establezco los ultimos 28
				// dias.
				else {
					Calendar c = Calendar.getInstance();
					Number nofdays = (Number) this.managedForm.getDataFieldValue(IMDataRoot.NUM_DIAS);
					if (nofdays == null) {
						nofdays = Integer.valueOf(28);
					}
					Date d = new Date();
					c.setTime(d);
					c.add(Calendar.DATE, -nofdays.intValue());
					this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, c.getTime());
					this.managedForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, d);
				}
			} catch (Exception e) {
				IMInformeCheckTrucker.logger.error(null, e);
			}
		}
		if ((this.crbRangoFechas != null) && this.crbRangoFechas.isSelected()) {
			this.managedForm.enableDataField(OpentachFieldNames.FILTERFECINI);
			this.managedForm.enableDataField(OpentachFieldNames.FILTERFECFIN);
		}
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.managedForm.setAdvancedQueryMode(OpentachFieldNames.IDCONDUCTOR_FIELD, true);
		this.managedForm.getElementReference(OpentachFieldNames.FILTERFECINI);
	}
}
