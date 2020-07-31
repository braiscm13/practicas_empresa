package com.opentach.client.remotevehicle.modules.remotefleet;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ListSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.modules.OpentachBasicInteractionManager;
import com.opentach.common.ITGDFileConstants;
import com.utilmize.gis.client.gis.gui.map.OGisJFxComponent;
import com.utilmize.gis.client.gis.gui.map.element.WmsMapElement;

import javafx.application.Platform;

public class IMRemoteFleetManagement extends OpentachBasicInteractionManager implements ITGDFileConstants {

	private static final Logger	logger	= LoggerFactory.getLogger(IMRemoteFleetManagement.class);


	public static final String								LAYER_CURRENT	= "CURRENT";
	public static final String								LAYER_HISTORY	= "HISTORY";

	@FormComponent(attr = "gisJFxComponent")
	private OGisJFxComponent	gisComponent;
	@FormComponent(attr = "ojee.RemoteVehicleManagementService.downloadVehicleConfig")
	private Table				table;
	@FormComponent(attr = "CIF")
	private DataField			cifField;

	private IMRemoteFleetManagementThreadRefreshFleetStatus	refreshFleetThread;

	public IMRemoteFleetManagement() {
		super();
	}

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.refreshFleetThread = new IMRemoteFleetManagementThreadRefreshFleetStatus(this.managedForm);
		this.refreshFleetThread.start();
		this.table.getJTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Platform.runLater(() -> {
			this.gisComponent.getGisComponent().getLayersManager().addLayer(IMRemoteFleetManagement.LAYER_CURRENT);
			this.gisComponent.getGisComponent().getLayersManager().addLayer(IMRemoteFleetManagement.LAYER_HISTORY);
			this.gisComponent.getGisComponent().setEditableNodesModeProtected(false);
		});
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.initMap();
	}

	private void initMap() {
		new Thread((Runnable) () -> {
			this.addWmsLayer();
		}, this.getClass().getName() + " add layers").start();
	}

	private void addWmsLayer() {
		try {
			this.gisComponent.addMapElement(new WmsMapElement(new URL("http://ows.terrestris.de/osm/service"), "OpenStreetMap WMS - by terrestris"));
		} catch (MalformedURLException error) {
			this.gisComponent.addMapElement(new WmsMapElement(this.gisComponent.getWmsURL(), this.gisComponent.getWmsLayer()));
			MessageManager.getMessageManager().showExceptionMessage(error, IMRemoteFleetManagement.logger);
		}
	}
}
