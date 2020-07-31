package com.opentach.client.comp.activitychart;

import java.util.List;

import javax.swing.JComponent;

/**
 * Interface del manipulador de un conjunto o serie de datos representable en un Chart.
 *
 *
 * @version 1.0
 * @see ChartData
 * @see ChartDataRender
 */
public interface ChartDataSet {

	/**
	 * Gets the x class.
	 *
	 * @return Tipo de clase a la que pertenecen los datos del eje X.
	 */
	Class<?> getXClass();

	/**
	 * Establece el render para el conjunto de datos. Cuando se cambia el render deben eliminarse los shapes asociados a los datos para que el nuevo
	 * render pueda asignarle el adecuado.
	 *
	 * @param render
	 *            Renderizador de los datos.
	 */
	void setChartDataRender(ChartDataRender render);

	/**
	 * Gets the chart data render.
	 *
	 * @return the chart data render
	 */
	ChartDataRender getChartDataRender();

	/**
	 * Gets the y class.
	 *
	 * @return Tipo de clase a la que pertenecen los datos del eje Y.
	 */
	Class<?> getYClass();

	/**
	 * Establece la lista de datos del conjunto.
	 *
	 * @param data
	 *            the new data
	 */
	void setData(List<Task> data);

	/**
	 * Establece la lista de datos del conjunto.
	 *
	 * @return the data
	 */
	List<ChartData> getData();


	/**
	 * Gets the visual component.
	 *
	 * @return JComponent componente visual que representa la lista de datos.
	 */
	JComponent getVisualComponent();

	/**
	 * Gets the description.
	 *
	 * @return String Descripcion de la serie de datos.
	 */
	String getDescription();

	/**
	 * Sets the description.
	 *
	 * @param desc
	 *            the new description
	 */
	void setDescription(String desc);
}
