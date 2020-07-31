package com.opentach.client.remotevehicle.modules.remotefleet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.swing.event.ListSelectionEvent;

import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.HourDateDataField;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.field.ObjectDataField;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.MessageType;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.opentach.common.remotevehicle.services.IRemoteVehicleService;
import com.utilmize.client.gui.AbstractListSelectionListener;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.tasks.USwingWorker;
import com.utilmize.gis.client.gis.exception.GisException;
import com.utilmize.gis.client.gis.gui.map.OGisJFxComponent;
import com.utilmize.gis.client.gis.gui.map.element.LineElement;

import javafx.application.Platform;

public class IMRemoteFleetManagementTableSelectionListener extends AbstractListSelectionListener {

	private static final Logger	logger	= LoggerFactory.getLogger(IMRemoteFleetManagementTableSelectionListener.class);
	@FormComponent(attr = "ojee.RemoteVehicleManagementService.downloadVehicleConfig")
	private Table				table;
	@FormComponent(attr = "SelectedVehicleLabel")
	private Label				selectedVehicleLabel;
	@FormComponent(attr = "SelectedVehiclePlate")
	private ObjectDataField		selectedVehiclePlate;
	@FormComponent(attr = "SelectedVehicleCif")
	private ObjectDataField		selectedVehicleCif;
	@FormComponent(attr = "gisJFxComponent")
	private OGisJFxComponent	gis;
	@FormComponent(attr = "SelectedVehicleFrom")
	private HourDateDataField	selectedVehicleFrom;
	@FormComponent(attr = "SelectedVehicleTo")
	private HourDateDataField	selectedVehicleTo;

	private final FleetStyler	styler	= new FleetStyler();

	public IMRemoteFleetManagementTableSelectionListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}

	@Override
	public void parentFormSetted() {
		super.parentFormSetted();
		this.setUnselectedLabel();
		this.selectedVehicleFrom.setValue(DateTools.truncate(DateTools.addDays(new Date(), -1)));
		this.selectedVehicleTo.setValue(DateTools.truncate(DateTools.addDays(new Date(), 1)));
	}

	private void setUnselectedLabel() {
		this.selectedVehicleLabel.setText(ApplicationManager.getTranslation("remotevehicle.vehicle_no_selected", this.getForm().getResourceBundle()));
	}

	@Override
	public void valueChanged(ListSelectionEvent evt) {
		if (evt.getValueIsAdjusting()) {
			return;
		}
		int selectedRow = this.table.getSelectedRow();
		this.selectedVehicleCif.setValue(null);
		this.selectedVehiclePlate.setValue(null);
		this.setUnselectedLabel();
		Platform.runLater(() -> {
			try {
				this.gis.getGisComponent().getLayersManager().clearLayer(IMRemoteFleetManagement.LAYER_HISTORY);
			} catch (GisException err) {
				IMRemoteFleetManagementTableSelectionListener.logger.error(null, err);
			}
		});

		if (selectedRow < 0) {
			return;
		}
		final String selectedPlate = (String) this.table.getRowValue(selectedRow, "VEH_ID");
		final String selectedCif = (String) this.table.getRowValue(selectedRow, "COM_ID");

		final Date selectedFrom = (Date) this.selectedVehicleFrom.getValue();
		final Date selectedTo = (Date) this.selectedVehicleTo.getValue();

		this.selectedVehiclePlate.setValue(selectedPlate);
		this.selectedVehicleCif.setValue(selectedCif);
		this.selectedVehicleLabel.setText(ApplicationManager.getTranslation("remotevehicle.vehicle_selected", this.getForm().getResourceBundle(), new Object[] { selectedPlate }));

		new USwingWorker<List<Coordinate>, Void>() {

			@Override
			protected List<Coordinate> doInBackground() throws Exception {
				EntityResult resLocations = BeansFactory.getBean(IRemoteVehicleService.class).queryVehicleLocations(selectedCif, selectedPlate, selectedFrom, selectedTo);
				int nregs = resLocations.calculateRecordNumber();
				List<Coordinate> coordinates = new ArrayList<>();
				for (int i = 0; i < nregs; i++) {
					Coordinate coor = new Coordinate( //
							((Number) ((List<?>) resLocations.get("LOC_LONGITUDE")).get(i)).doubleValue(), //
							((Number) ((List<?>) resLocations.get("LOC_LATITUDE")).get(i)).doubleValue()//
							);
					coordinates.add(coor);
				}
				return coordinates;
			}

			@Override
			protected void done() {
				try {
					List<Coordinate> coordinates = this.uget();
					if (coordinates.size() == 0) {
						MessageManager.getMessageManager().showMessage(IMRemoteFleetManagementTableSelectionListener.this.getForm(), "remotevehicle.E_NO_COORDINATE_INFO",
								MessageType.WARNING, new Object[] { selectedPlate }, false);
					} else {
						LineElement lineElement = new LineElement(coordinates, new HashMap<>());
						Platform.runLater(() -> {
							IMRemoteFleetManagementTableSelectionListener.this.gis.getGisComponent().addGeometry(lineElement, IMRemoteFleetManagement.LAYER_HISTORY,
									IMRemoteFleetManagementTableSelectionListener.this.styler);
						});
					}
				} catch (Exception err) {
					MessageManager.getMessageManager().showExceptionMessage(err, IMRemoteFleetManagementTableSelectionListener.logger);
				}
			}
		}.executeOperation(this.getForm());
	}

}
