package com.opentach.client.comp.activitychart.taskwrapper;

import com.opentach.common.tacho.data.Actividad;

/**
 * <p>
 * Título:
 * </p>
 * <p>
 * Descripción: Clase envoltorio de Activades para que sus datos puedan ser
 * representados en un chart.
 * <p>
 * Empresa: Imatia
 * </p>
 *
 * @author Pablo Dorgambide Aviles
 * @version 1.0
 */

public class IndefinidaTaskWrapper extends ActividadesTaskWrapper {

	public IndefinidaTaskWrapper() {}

	public IndefinidaTaskWrapper(Actividad act) {
		super(act);
		ActividadesTaskWrapper.render = this.getChartDataRender();
	}

	@Override
	protected void buildDescription() {
		this.setDescription("");
	}
}
