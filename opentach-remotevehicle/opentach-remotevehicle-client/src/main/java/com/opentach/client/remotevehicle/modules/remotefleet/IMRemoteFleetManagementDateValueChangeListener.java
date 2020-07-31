package com.opentach.client.remotevehicle.modules.remotefleet;

import java.util.Date;
import java.util.Hashtable;

import javax.swing.DefaultListSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.HourDateDataField;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.DateTools;
import com.opentach.common.remotevehicle.services.IRemoteVehicleService;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class IMRemoteFleetManagementDateValueChangeListener extends AbstractValueChangeListener {

	private static final Logger	logger	= LoggerFactory.getLogger(IMRemoteFleetManagementDateValueChangeListener.class);

	@FormComponent(attr = "ojee.RemoteVehicleManagementService.downloadVehicleConfig")
	private Table				table;

	@FormComponent(attr = "SelectedVehicleFrom")
	private HourDateDataField	selectedVehicleFrom;
	@FormComponent(attr = "SelectedVehicleTo")
	private HourDateDataField	selectedVehicleTo;

	public IMRemoteFleetManagementDateValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void valueChanged(ValueEvent evt) {
		if (evt.getType() == ValueEvent.PROGRAMMATIC_CHANGE) {
			return;
		}
		try {
			if ((this.selectedVehicleFrom.getValue() == null) || (this.selectedVehicleTo.getValue() == null)) {
				return;
			}
			Date from = (Date) this.selectedVehicleFrom.getValue();
			Date to = (Date) this.selectedVehicleTo.getValue();
			if ((to.getTime() < from.getTime()) || (DateTools.countDaysBetween(from, to) > IRemoteVehicleService.MAX_DAYS)) {
				if (evt.getSource() == this.selectedVehicleFrom) {
					this.selectedVehicleTo.setValue(DateTools.addDays(from, 1));
				} else {
					this.selectedVehicleFrom.setValue(DateTools.addDays(to, -1));
				}
				// MessageManager.getMessageManager().showMessage(this.getForm(), "remotevehicle.E_TOO_MANY_DAYS", MessageType.WARNING, new Object[] {}, false);
			}
			int minSelectionIndex = ((DefaultListSelectionModel) this.table.getJTable().getSelectionModel()).getMinSelectionIndex();
			if (minSelectionIndex >= 0) { // forzamos la consulta del seleccionado
				((DefaultListSelectionModel) this.table.getJTable().getSelectionModel()).clearSelection();
				((DefaultListSelectionModel) this.table.getJTable().getSelectionModel()).setSelectionInterval(minSelectionIndex, minSelectionIndex);
			}
		} catch (Exception err) {
			IMRemoteFleetManagementDateValueChangeListener.logger.error(null, err);
		}
	}

}
