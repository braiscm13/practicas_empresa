package com.opentach.common.reports;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRAbstractChartCustomizer;
import net.sf.jasperreports.engine.JRChart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;

public class JRInfracChartCustomizer extends JRAbstractChartCustomizer {

	private static final Map<String, Color>	mTypeColor;

	static {
		mTypeColor = new HashMap<String, Color>();
		JRInfracChartCustomizer.mTypeColor.put("L", Color.GREEN);
		JRInfracChartCustomizer.mTypeColor.put("G", Color.BLUE);
		JRInfracChartCustomizer.mTypeColor.put("MG", Color.RED);
	}

	@Override
	public void customize(JFreeChart jfreechart, JRChart jrchart) {
		BarRenderer renderer = (BarRenderer) jfreechart.getCategoryPlot().getRenderer();
		CategoryPlot plot = jfreechart.getCategoryPlot();
		CategoryDataset cds = plot.getDataset();
		if (cds != null) {
			List<Object> l = cds.getRowKeys();
			if (l != null) {
				Color c = null;
				for (int i = 0; i < l.size(); i++) {
					c = JRInfracChartCustomizer.mTypeColor.get(l.get(i));
					renderer.setSeriesPaint(i, c);
				}
			}
		}
	}
}
