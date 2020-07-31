package com.opentach.client.remotevehicle.modules.remotefleet;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;

import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.table.Table;
import com.utilmize.client.gui.AbstractTableModelListener;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.gis.client.gis.exception.GisException;
import com.utilmize.gis.client.gis.gui.map.OGisJFxComponent;
import com.utilmize.gis.client.gis.gui.map.element.PointElement;
import com.utilmize.gis.client.gis.gui.map.layer.GisLayer;

import javafx.application.Platform;

public class IMRemoteFleetManagementTableModelListener extends AbstractTableModelListener {

	private static final Logger	logger	= LoggerFactory.getLogger(IMRemoteFleetManagementTableModelListener.class);
	@FormComponent(attr = "gisJFxComponent")
	private OGisJFxComponent	gis;
	@FormComponent(attr = "ojee.RemoteVehicleManagementService.downloadVehicleConfig")
	private Table				table;

	private final FleetStyler	styler			= new FleetStyler();

	public IMRemoteFleetManagementTableModelListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
	}


	@Override
	public void tableChanged(TableModelEvent evt) {
		try {
			Platform.runLater(() -> {
				try {
					this.gis.getGisComponent().getLayersManager().clearLayer(IMRemoteFleetManagement.LAYER_CURRENT);
				} catch (GisException err) {
					IMRemoteFleetManagementTableModelListener.logger.error(null, err);
				}
			});
			Hashtable hb = (Hashtable) this.table.getValue();
			if (!(hb instanceof EntityResult)) {
				return;
			}
			EntityResult er = (EntityResult) hb;
			int nrecords = er.calculateRecordNumber();
			GisLayer layer = new GisLayer();
			for (int i = 0; i < nrecords; i++) {
				Number latitude = (Number) ((List) er.get("RDV_LAST_LATITUDE")).get(i);
				Number longitude = (Number) ((List) er.get("RDV_LAST_LONGITUDE")).get(i);
				if ((latitude != null) && (longitude != null)) {
					Map<Object, Object> userData = new HashMap<>();
					userData.put("plate", ((List) er.get("VEH_ID")).get(i));
					this.gis.addGeometry(new PointElement(new Coordinate(longitude.doubleValue(), latitude.doubleValue()), userData),
							IMRemoteFleetManagement.LAYER_CURRENT, this.styler);
				}
			}
		} catch (Throwable error) {
			IMRemoteFleetManagementTableModelListener.logger.error(null, error);
		}
	}



}
