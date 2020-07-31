package com.opentach.client.comp.activitychart;

import javax.swing.JComponent;

public abstract class Axis extends JComponent {

	protected Chart	chart;

	public Axis(Chart chart) {
		super();
		this.chart = chart;
	}

	/** @return double Rango del eje */
	public abstract double getRange();

	/**
	 * Configuracion del eje
	 * 
	 * @param minval
	 *            Object Valor minimo.
	 * @param maxval
	 *            Object Valor maximo
	 * @param numcells
	 *            int Numero de celdas o divisiones.
	 */
	public abstract void setAxisCfg(Object minval, Object maxval);

	/** @return double Valor maximo del eje. */
	public abstract double getMaxVal();

	/** @return double Valor minimo del eje. */
	public abstract double getMinVal();

	public abstract Class getClassData();

}