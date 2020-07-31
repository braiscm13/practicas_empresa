package com.opentach.client.comp;

import java.util.Hashtable;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

public class GraficaExt extends GraficaExtColor {

	protected boolean	showlegend;

	public GraficaExt(Hashtable parametros) {
		super(parametros);
	}

	@Override
	public void init(Hashtable parametros) {
		super.init(parametros);
		this.showlegend = (parametros.get("showlegend") != null) && ((String) parametros.get("showlegend")).toUpperCase().startsWith("Y") ? true
				: false;
	}

	@Override
	protected JFreeChart createChart() {
		JFreeChart chart = super.createChart();
		if (!this.showlegend) {
			chart.getLegend().setVisible(false);
		}
		return chart;
	}

	@Override
	protected CategoryDataset getCategoryDataset(String colX, String[] colsY, String[] seriesNames) {
		CategoryDataset cd = super.getCategoryDataset(colX, colsY, seriesNames);
		return cd;
	}

	@Override
	public void setValue(Object valor) {
		super.setValue(valor);
	}
}
