package com.opentach.client.comp.activitychart;

import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

/**
 * Serie de datos representable en un Chart.
 *
 * @version 1.0
 */
public class BasicChartDataSet implements ChartDataSet {

	protected String			description;
	protected ChartDataRender	render;
	protected List<Task>		data;

	public BasicChartDataSet(String desc, List<Task> data) {
		this.description = desc;
		this.data = data;
	}

	/**
	 * @return Tipo de clase a la que pertenecen los datos del eje X.
	 */
	@Override
	public Class<?> getXClass() {
		try {
			List<Object> p = ((ChartData) this.data.get(0)).getX();
			return (p.iterator().next().getClass());
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * @param alpha
	 *            Valor de transparencia sobre el pintado de los datos.
	 * @param render
	 */
	@Override
	public void setChartDataRender(ChartDataRender render) {
		this.render = render;
		try {
			Iterator iter = this.data.iterator();
			while (iter.hasNext()) {
				((ChartData) iter.next()).setShape(null); // clear the shape of
				// all chart data to
				// let the
				// render asign new
				// one
			}
		} catch (Exception ex) {
		}

	}

	/**
	 * @return Tipo de clase a la que pertenecen los datos del eje Y.
	 */
	@Override
	public Class getYClass() {
		try {
			java.util.List p = ((ChartData) this.data.get(0)).getY();
			return (p.iterator().next().getClass());
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * @param data
	 */
	@Override
	public void setData(List<Task> data) {
		this.data = data;
	}

	/**
	 * getChartDataRender
	 *
	 * @return ChartDataRender
	 */
	@Override
	public ChartDataRender getChartDataRender() {
		return this.render;
	}

	/**
	 * getVisualComponent
	 *
	 * @return JComponent
	 */
	@Override
	public JComponent getVisualComponent() {
		return null;
	}

	/**
	 * getDescription
	 *
	 * @return String
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/**
	 * setDescription
	 *
	 * @param desc
	 *            String
	 * @return String
	 */
	@Override
	public void setDescription(String desc) {
		this.description = desc;
	}

	/**
	 * getData
	 *
	 * @return List
	 */
	@Override
	public java.util.List getData() {
		return this.data;
	}
}
