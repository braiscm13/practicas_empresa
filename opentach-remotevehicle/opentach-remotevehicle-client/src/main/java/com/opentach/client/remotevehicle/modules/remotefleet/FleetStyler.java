package com.opentach.client.remotevehicle.modules.remotefleet;

import com.utilmize.gis.client.gis.gui.map.CacheManager;
import com.utilmize.gis.client.gis.gui.map.element.LineElement;
import com.utilmize.gis.client.gis.gui.map.element.PointElement;
import com.utilmize.gis.client.gis.gui.map.styles.DefaultGeometryStyler;
import com.utilmize.gis.client.gis.gui.map.styles.LineStyle;
import com.utilmize.gis.client.gis.gui.map.styles.PointStyle;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;

public class FleetStyler extends DefaultGeometryStyler {
	public FleetStyler() {
		super(new PointStyle(new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/opentach/client/rsc/delivery_truck24.png")), 24),
				new LineStyle(Color.web("#366581"), 2.0d, 0.8d),
				null);
	}

	@Override
	protected Node stylePoint(PointElement element, PointStyle style, CacheManager cacheManager) {
		Node node = super.stylePoint(element, style, cacheManager);
		Label label = new Label((String) element.getUserData().get("plate"));
		BorderPane res = new BorderPane();
		res.setCenter(node);
		res.setBottom(label);
		return res;
	}

	@Override
	protected Path styleLine(LineElement element, LineStyle lineStyle, CacheManager cacheManager) {
		return super.styleLine(element, lineStyle, cacheManager);
	}

}
