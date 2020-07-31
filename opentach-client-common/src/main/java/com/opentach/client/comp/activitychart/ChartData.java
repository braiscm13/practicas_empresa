package com.opentach.client.comp.activitychart;

import java.awt.Shape;
import java.util.List;

import javax.swing.JComponent;

/**
 * Interface que deben implementar todos aquellos datos representables en un
 * chat.
 *
 *
 * @author Pablo Dorgambide
 * @version 1.0
 * @see ChartData
 */
public interface ChartData {
	/**
	 * Retorna la lista de objetos para el eje de ordenadas ( X ). Los puntos
	 * correspondientes a cada elemento en el eje de abscisas se obtendran con
	 * el metodo getY()
	 *
	 *
	 * @return Lista con los datos del eje de ordenadas
	 */
	public List getX();

	/**
	 *
	 *
	 *
	 * @return Lista con los datos del eje de abscisas
	 */
	public List getY();

	/**
	 * @return Identificador del dato
	 */
	public Object getId();

	/**
	 * @return Identificador del dato
	 */
	public Object getKind();

	/**
	 *
	 * Establece la lista de puntos del eje de ordenadas.
	 *
	 *
	 * @param x
	 */
	public void setX(List x);

	/**
	 * Establece la lista de puntos del eje de ordenadas.
	 *
	 *
	 * @param y
	 */
	public void setY(List y);

	/**
	 * Establece la descripcion del dato
	 *
	 *
	 * @param desc
	 *            Descripcion del dato
	 */
	public void setDescription(String desc);

	/**
	 * @return Descripcion del dato
	 */
	public String getDescription(Chart chart);

	/**
	 * @return JComponent componente visual que representa el dato
	 */
	public JComponent getVisualComponent();

	/**
	 * Shape que representará el dato en el grafico. Habitualmente lo
	 * establecerá el render asociado la primera vez que el dato sea pintado.
	 * 
	 * @param sh
	 *            Shape
	 */
	public void setShape(Shape sh);

	/** @return Shape Forma utilizada para representar el dato. */
	public Shape getShape();
}
