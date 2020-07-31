package com.opentach.client.labor.modules.report;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.comp.field.CampoComboReferenciaDespl;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMInformeLaboralAddJournalActionListener extends AbstractActionListenerButton {

	private static final Logger		logger					= LoggerFactory.getLogger(IMInformeLaboralAddJournalActionListener.class);

	private final static String[]	ATTRS_TO_INSERT			= { "MAJ_DAY_YEAR", "MAJ_DAY_MONTH", "MAJ_DAY_DAY", "MAJ_BEGINDATE", "MAJ_ENDDATE", "MAJ_WORK_TIME" };
	private final static String[]	EXTRA_ATTRS_TO_INSERT	= { "IDCONDUCTOR", "CIF" };
	@FormComponent(attr = "Table")
	private Table					table;
	@FormComponent(attr = "IDCONDUCTOR")
	private CampoComboReferenciaDespl	driverField;
	@FormComponent(attr = "CIF")
	private UReferenceDataField	companyField;

	public IMInformeLaboralAddJournalActionListener() throws Exception {
		super();
	}

	public IMInformeLaboralAddJournalActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMInformeLaboralAddJournalActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMInformeLaboralAddJournalActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new USwingWorker<EntityResult, Void>() {

			@Override
			protected EntityResult doInBackground() throws Exception {
				Hashtable av = FIMUtils.getFormValues(IMInformeLaboralAddJournalActionListener.this.getForm(), IMInformeLaboralAddJournalActionListener.ATTRS_TO_INSERT);
				av.putAll(FIMUtils.getFormValues(IMInformeLaboralAddJournalActionListener.this.getForm(), IMInformeLaboralAddJournalActionListener.EXTRA_ATTRS_TO_INSERT));
				Object object = av.get("IDCONDUCTOR");
				if (object instanceof SearchValue) {
					List<?> drivers = ((List<?>) ((SearchValue) object).getValue());
					if (drivers.size() != 1) {
						throw new Exception("SELECT_ONE_DRIVER");
					}
					av.put("IDCONDUCTOR", drivers.get(0));
				}
				EntityResult res = IMInformeLaboralAddJournalActionListener.this.getEntity("EManualJournal").insert(av,
						IMInformeLaboralAddJournalActionListener.this.getSessionId());
				CheckingTools.checkValidEntityResult(res);
				return res;
			}

			@Override
			protected void done() {
				try {
					EntityResult res = this.uget();
					IMInformeLaboralAddJournalActionListener.this.table.addRow(0, IMInformeLaboralAddJournalActionListener.this.fillRowRecord(res));
					for (String attr : IMInformeLaboralAddJournalActionListener.ATTRS_TO_INSERT) {
						IMInformeLaboralAddJournalActionListener.this.getForm().deleteDataField(attr);
					}
				} catch (Throwable ex) {
					MessageManager.getMessageManager().showExceptionMessage(ex, IMInformeLaboralAddJournalActionListener.logger);
				}
			}


		}.executeOperation(this.getForm());

	}

	private Hashtable fillRowRecord(EntityResult res) {
		Map companyValues = this.companyField.getCodeValues(res.get("CIF"));
		Map driverValues = this.driverField.getValuesToCode(res.get("IDCONDUCTOR"));
		Hashtable<String, Object> record = new Hashtable<String, Object>();
		MapTools.safePut(record, "IDCONDUCTOR", res.get("IDCONDUCTOR"));
		MapTools.safePut(record, "DNI", driverValues.get("DNI"));
		MapTools.safePut(record, "NOMBRE", driverValues.get("NOMBRE"));
		MapTools.safePut(record, "APELLIDOS", driverValues.get("APELLIDOS"));
		MapTools.safePut(record, "DIA", res.get("MAJ_DAY"));
		MapTools.safePut(record, "FECINI", res.get("MAJ_BEGINDATE"));
		MapTools.safePut(record, "FECFIN", res.get("MAJ_ENDDATE"));
		MapTools.safePut(record, "MINUTOS", res.get("MAJ_WORK_TIME"));
		MapTools.safePut(record, "CIF", res.get("CIF"));
		MapTools.safePut(record, "NOMB", companyValues.get("NOMB"));
		MapTools.safePut(record, "ORIGEN", "MANUAL");
		return record;

	}
	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent interactionmanagermodeevent) {
		this.getButton().setEnabled(true);
		for (String attr : IMInformeLaboralAddJournalActionListener.ATTRS_TO_INSERT) {
			this.getForm().enableDataField(attr);
		}
	}
}
