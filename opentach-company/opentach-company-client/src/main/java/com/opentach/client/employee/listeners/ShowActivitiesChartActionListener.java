package com.opentach.client.employee.listeners;

import java.awt.Window;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.Pair;
import com.opentach.client.comp.ShowFormManagerButton.ShowFormManagerListener;
import com.opentach.client.fim.IIMGraficaCond;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.fim.FIMUtils;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UFormHeaderButton;

public class ShowActivitiesChartActionListener extends ShowFormManagerListener {

	/** The CONSTANT logger */
	private static final Logger		logger			= LoggerFactory.getLogger(ShowActivitiesChartActionListener.class);

	@FormComponent(attr = OpentachFieldNames.CIF_FIELD)
	private DataField				cifField;
	@FormComponent(attr = OpentachFieldNames.IDCONDUCTOR_FIELD)
	private DataField				idConductorField;
	@FormComponent(attr = OpentachFieldNames.CG_CONTRATO_FIELD)
	private DataField				cgContratoField;

	@FormComponent(attr = "EInformeActivCond")
	protected Table					activitiesTable;

	protected Pair<Window, Form>	detail			= null;

	public ShowActivitiesChartActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public ShowActivitiesChartActionListener(UFormHeaderButton button, Hashtable params) throws Exception {
		super(button, button, params);
	}

	@Override
	public void parentFormSetted() {
		super.parentFormSetted();

		this.activitiesTable.getTableSorter().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				ShowActivitiesChartActionListener.this.considerToEnableButton();
			}
		});
	}

	@Override
	protected boolean getEnableValueToSet() {
		return this.hasValues() && super.getEnableValueToSet();
	}

	protected boolean hasValues() {
		return FIMUtils.getTableValueSafeER(this.activitiesTable).calculateRecordNumber() > 0;
	}

	@Override
	protected void completeFormWithValues(Form targetForm) {
		targetForm.getInteractionManager().setInitialState();
		super.completeFormWithValues(targetForm);
		try {
			((IIMGraficaCond) targetForm.getInteractionManager()).setInfracciones(new EntityResult());
			((IMDataRoot) targetForm.getInteractionManager()).doOnQuery();
		} catch (Exception err) {
			ShowActivitiesChartActionListener.logger.warn(null, err);
		}
	}

}
