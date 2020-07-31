package com.opentach.client.comp.activitychart.taskwrapper;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.opentach.client.comp.activitychart.BarChartDataRender;
import com.opentach.client.comp.activitychart.ChartDataRender;
import com.opentach.client.comp.activitychart.ChartDataRenderProvider;
import com.opentach.client.comp.activitychart.Task;
import com.opentach.client.comp.activitychart.TaskParser;

public class FicherosTaskWrapper extends Task implements TaskParser, ChartDataRenderProvider {
	protected static ChartDataRender	render;
	protected Date						f_descarga;
	protected String					nombre;

	public FicherosTaskWrapper() {}

	private String getRow(String data1, Object data2) {
		if ((data1 != null) && (data2 != null)) {
			return "<tr><td>" + data1 + "</td><td>" + data2 + "</td></tr>";
		} else {
			return "";
		}
	}

	@Override
	public void parse(Map cv) {
		this.setId(Integer.valueOf(0));
		this.setKind((String) cv.get("TIPO"));
		this.nombre = (String) cv.get("NOMB");
		this.setStart((Date) cv.get("FECINI"));
		this.setEnd((Date) cv.get("FECFIN"));
		this.f_descarga = (Date) cv.get("F_DESCARGA_DATOS");
		this.buildDescription();
	}

	protected void buildDescription() {
		SimpleDateFormat dfs = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		StringBuffer str = new StringBuffer();
		str.append("<html> <body><h5> " + this.getKind() + "  </h5>");
		str.append("<table  border='1' frame='border'>");
		// str.append(getRow("Naturaleza", naturaleza));
		str.append(this.getRow("Fichero", this.nombre));
		str.append(this.getRow("Inicio", dfs.format(this.getStart())));
		str.append(this.getRow("Fin", dfs.format(this.getEnd())));
		str.append(this.getRow("F. Descarga", dfs.format(this.f_descarga)));

		str.append("</table>");
		str.append("</body></html>");
		this.setDescription(str.toString());
	}

	@Override
	public ChartDataRender getChartDataRender() {
		if (FicherosTaskWrapper.render == null) {
			FicherosTaskWrapper.render = new BarChartDataRender(4, BarChartDataRender.HORIZONTAL_BAR, Color.blue) {
				// Las infracciones y periodos tendrán un ancho la mitad del
				// especificado.
				@Override
				public void setBarWidth(int w) {
					super.setBarWidth(5);
				}

			};

			FicherosTaskWrapper.render.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
		}
		return FicherosTaskWrapper.render;
	}
}
