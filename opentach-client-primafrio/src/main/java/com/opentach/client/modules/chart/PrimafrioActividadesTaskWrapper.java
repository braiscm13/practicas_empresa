package com.opentach.client.modules.chart;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.RectangularShape;
import java.util.HashMap;

import com.opentach.client.comp.activitychart.BarChartDataRender;
import com.opentach.client.comp.activitychart.Chart;
import com.opentach.client.comp.activitychart.ChartDataRender;
import com.opentach.client.comp.activitychart.taskwrapper.ActividadesTaskWrapper;

/**
 * <p> Título: </p> <p> Descripción: Clase envoltorio de Activades para que sus datos puedan ser representados en un chart. <p> Empresa: Imatia </p>
 *
 * @author Pablo Dorgambide Aviles
 * @version 1.0
 */

public class PrimafrioActividadesTaskWrapper extends ActividadesTaskWrapper {
	protected static PrimafrioActivityBarChartDataRender	render;


	protected class PrimafrioActivityBarChartDataRender extends ActividadesTaskWrapper.ActivityBarChartDataRender {

		public PrimafrioActivityBarChartDataRender(int width, int orient, Color cdefault, HashMap colours) {
			super(width, orient, cdefault, colours);
		}

		@Override
		protected void drawInfractionBackground(Graphics g, Rectangle r, Chart chart) {
		}

		@Override
		protected void drawSublinesBackground(Graphics g, RectangularShape r, ActividadesTaskWrapper data, Chart chart) {
			// Pintamos la barra por debajo de fuera de
			// ámbito/transporte en tren
			this.drawTransporteTren(g, r.getBounds(), data);

			// INICIO PRIMAFRIO
			this.drawRegimen(g, r.getBounds(), data);
			// FIN PRIMAFRIO
		}

	}

	@Override
	public ChartDataRender getChartDataRender() {
		if (PrimafrioActividadesTaskWrapper.render == null) {
			HashMap<String, Color> cmap1 = this.buildRendererColorMap();
			// Alto en función de la actividad.
			HashMap<String, Integer> heights = this.buildRendererTaskHeights();
			PrimafrioActivityBarChartDataRender barrender = new PrimafrioActivityBarChartDataRender(10, BarChartDataRender.HORIZONTAL_BAR, Color.red,
					cmap1);
			barrender.setDataWidths(heights);
			barrender.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			barrender.setRoundedRectangle(false);
			PrimafrioActividadesTaskWrapper.render = barrender;
		}
		return PrimafrioActividadesTaskWrapper.render;
	}

}
