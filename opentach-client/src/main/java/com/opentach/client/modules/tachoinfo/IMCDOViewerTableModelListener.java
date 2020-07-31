package com.opentach.client.modules.tachoinfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;

import org.apache.commons.codec.binary.Base64;
import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.jee.common.tools.ThreadTools;
import com.opentach.client.remotevehicle.modules.remotefleet.IMRemoteFleetManagement;
import com.utilmize.client.gui.AbstractTableModelListener;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.gis.client.gis.exception.GisException;
import com.utilmize.gis.client.gis.gui.map.OGisJFxComponent;
import com.utilmize.gis.client.gis.gui.map.element.PointElement;
import com.utilmize.gis.client.gis.gui.map.styles.DefaultGeometryStyler;
import com.utilmize.gis.client.gis.gui.map.styles.IGeometryStyler;
import com.utilmize.gis.client.gis.gui.map.styles.LineStyle;
import com.utilmize.gis.client.gis.gui.map.styles.PointStyle;
import com.utilmize.gis.client.gis.gui.map.styles.PolygonStyle;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class IMCDOViewerTableModelListener extends AbstractTableModelListener {

	private static final Logger	logger	= LoggerFactory.getLogger(IMCDOViewerTableModelListener.class);
	@FormComponent(attr = "gisJFxComponent")
	private OGisJFxComponent	gis;
	@FormComponent(attr = "EUsuariosCDO")
	private Table				table;

	private IGeometryStyler		styler;
	private String				logo;

	public IMCDOViewerTableModelListener(IUFormComponent formComponent, Hashtable params) throws Exception {
		super(formComponent, params);
		Platform.runLater(() -> {
			this.styler = new DefaultGeometryStyler(
					new PointStyle(new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/opentach/client/rsc/map_opentach_ico.png")), 24),
					new LineStyle(Color.RED, 2.0d, 1.0d), new PolygonStyle(Color.RED, 2.0d, Color.RED, 1.0d));
		});
	}

	@Override
	public void tableChanged(TableModelEvent evt) {

		try {
			Platform.runLater(() -> {
				try {
					this.gis.getGisComponent().getLayersManager().clearLayer(IMCDOViewer.LAYER_CURRENT);
				} catch (GisException err) {
					IMCDOViewerTableModelListener.logger.error(null, err);
				}
			});
			Map<Object, Object> hb = (Map) this.table.getValue();
			if (!(hb instanceof EntityResult)) {
				return;
			}
			if (this.gis.getGisComponent().getMapCanvas().getWidth() <= 0) {
				ThreadTools.sleep(100);
				SwingUtilities.invokeLater(() -> this.tableChanged(evt));
				return;
			}
			EntityResult er = (EntityResult) hb;
			int nrecords = er.calculateRecordNumber();
			if (this.logo == null) {
				this.logo = this.getLogoBase64EmbeddedImage();
			}
			for (int i = 0; i < nrecords; i++) {
				Map<String, Object> record = er.getRecordValues(i);
				Number latitude = (Number) record.get("LATITUDE");
				Number longitude = (Number) record.get("LONGITUDE");
				if ((latitude != null) && (longitude != null)) {
					String dscr = (String) record.get("DSCR");
					String name = (String) record.get("NAME");
					String prov = (String) record.get("PROVINCIA");
					String pobl = (String) record.get("POBL");
					String telefono = (String) record.get("TELEFONO");
					String content = new Template("com/opentach/client/comp/googlemapinfo.html").fillTemplate("#TITLE#", name, //
							"#DESCRIPCION#", dscr, //
							"#IMAGE#", this.logo, //
							"#PROVINCIA#", prov, //
							"#POBLACION#", pobl, //
							"#TELEFONO#", telefono);
					Map<Object, Object> userData = new HashMap<>();
					userData.put("popupContent", content);
					PointElement pointElement = new PointElement(new Coordinate(longitude.doubleValue(), latitude.doubleValue()), userData);
					Platform.runLater(() -> {
						this.gis.getGisComponent().addGeometry(pointElement, IMRemoteFleetManagement.LAYER_CURRENT, this.styler);
					});
				}
			}
		} catch (Throwable error) {
			IMCDOViewerTableModelListener.logger.error(null, error);
		}
	}

	private String getLogoBase64EmbeddedImage() {
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/opentach/client/rsc/map_opentach_ico.png")) {
			byte[] data = StreamUtils.copyToByteArray(is);
			String base64 = new String(Base64.encodeBase64(data));
			return "data:image/png;base64," + base64;
		} catch (IOException err) {
			IMCDOViewerTableModelListener.logger.error(null, err);
			return "";
		}
	}

}
