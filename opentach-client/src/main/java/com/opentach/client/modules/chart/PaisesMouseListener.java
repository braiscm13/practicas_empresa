package com.opentach.client.modules.chart;

import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.SwingUtilities;

import com.opentach.client.comp.activitychart.ChartData;
import com.opentach.client.comp.activitychart.ChartDataField;
import com.opentach.client.comp.activitychart.ChartDataSet;
import com.opentach.client.comp.activitychart.taskwrapper.PaisesTaskWrapper;

public class PaisesMouseListener extends MouseAdapter {

	protected ChartDataField	chartDatafield;
	protected PaisesTaskWrapper	currentSelected;

	public PaisesMouseListener(ChartDataField chartDataField) {
		super();
		this.chartDatafield = chartDataField;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Iterator iter = this.chartDatafield.getChart().getCharts().iterator();
		while (iter.hasNext()) {
			ChartDataSet dataset = (ChartDataSet) iter.next();
			if (PaisesTaskWrapper.class.getName().equals(dataset.getDescription())) {
				List listData = dataset.getData();
				ListIterator iter2 = listData.listIterator(listData.size());
				while (iter2.hasPrevious()) {
					ChartData data = (ChartData) iter2.previous();
					if (data instanceof PaisesTaskWrapper) {
						Shape sh = data.getShape();
						if ((sh != null) && (sh.contains(new Point(e.getX(), e.getY())))) {
							this.setSelectedCountry((PaisesTaskWrapper) data);
							return;
						}
					}
				}
			}
		}
		this.setSelectedCountry(null);
	}

	protected void setSelectedCountry(PaisesTaskWrapper task) {
		if (this.currentSelected != null) {
			this.currentSelected.setClicked(false);
		}
		this.currentSelected = task;
		if (this.currentSelected != null) {
			this.currentSelected.setClicked(true);
		}
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				PaisesMouseListener.this.chartDatafield.getChart().refresh();
			}
		});
	}

}