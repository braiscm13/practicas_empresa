package com.opentach.client.modules.tachoinfo;

import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.gis.client.gis.gui.map.OGisJFxComponent;
import com.utilmize.gis.client.gis.gui.map.element.WmsMapElement;

public class IMCDOViewer extends UBasicFIM {

	private static final Logger	logger			= LoggerFactory.getLogger(IMCDOViewer.class);

	public static final String	LAYER_CURRENT	= "CURRENT";

	@FormComponent(attr = "EUsuariosCDO")
	private Table				tableCdo;
	@FormComponent(attr = "gisJFxComponent")
	private OGisJFxComponent	gisComponent;

	public IMCDOViewer() {
		super();
	}

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		this.tableCdo.getJTable().getSelectionModel().addListSelectionListener(new CDOTableSelectionListener());
		this.tableCdo.getJTable().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.tableCdo.setEnabled(true);
		this.tableCdo.refreshInThread(0);
		this.initMap();
	}

	private void initMap() {
		new Thread((Runnable) () -> {
			this.gisComponent.setEditableNodesMode(false);
			this.gisComponent.getGisComponent().setEnableMousePresed(false);
			this.addWmsLayer();
		}, this.getClass().getName() + " add layers").start();
	}

	private void addWmsLayer() {
		try {
			this.gisComponent.addMapElement(new WmsMapElement(new URL("http://ows.terrestris.de/osm/service"), "OpenStreetMap WMS - by terrestris"));
		} catch (MalformedURLException error) {
			this.gisComponent.addMapElement(new WmsMapElement(this.gisComponent.getWmsURL(), this.gisComponent.getWmsLayer()));
			MessageManager.getMessageManager().showExceptionMessage(error, IMCDOViewer.logger);
		}
	}

	class CDOTableSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() || (IMCDOViewer.this.tableCdo.getSelectedRows().length != 1)) {
				return;
			}
			int rowIndex = IMCDOViewer.this.tableCdo.getSelectedRows()[0];
			Number latitude = (Number) IMCDOViewer.this.tableCdo.getJTable().getValueAt(rowIndex, IMCDOViewer.this.tableCdo.getColumnIndex("LATITUDE"));
			Number longitude = (Number) IMCDOViewer.this.tableCdo.getJTable().getValueAt(rowIndex, IMCDOViewer.this.tableCdo.getColumnIndex("LONGITUDE"));
			if ((latitude != null) && (longitude != null)) {
				ReflectionTools.setFieldValue(IMCDOViewer.this.gisComponent.getGisComponent().getZoomManager(), "zoomLevel", 13);
				java.awt.geom.Point2D.Double xyWorld = new Point2D.Double(longitude.doubleValue(), latitude.doubleValue());
				java.awt.geom.Point2D.Double xyScreen = new Point2D.Double(IMCDOViewer.this.gisComponent.getWidth() / 2.0, IMCDOViewer.this.gisComponent.getHeight() / 2.0);
				ReflectionTools.invoke(IMCDOViewer.this.gisComponent.getGisComponent().getZoomManager(), "zoom", xyScreen, xyWorld, 0);
			}
		}

	}
}
