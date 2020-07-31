package com.opentach.client.comp.activitychart;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.List;

/**
 * Interface para renderizado de los datos.
 *
 *
 * @version 1.0
 * @see ChartData
 */
public interface ChartDataRender {
	/**
	 * Renderiza dentro del Graphic todos los datos de la lista.
	 *
	 *
	 * @param g
	 * @param data
	 */
	public void render(Graphics g, List<ChartData> data, Chart chart);

	/**
	 * Comprueba si la representacion grafica del dato contiene a r.
	 *
	 * @param data
	 *            ChartData Objeto del tipo ChartData
	 * @param r
	 *            Shape Forma
	 * @return true si el shape esta contenido dentro de la representacion
	 *         grafica del dato.
	 */
	// public boolean contains(ChartData data, Shape r);

	/**
	 * Retorna una lista con las formas utilizadas para representar el dato. Es
	 * una lista ya que pueden existir casos en los que un determinado dato
	 * pueda tener como representación varios rectangulos no conectados.
	 *
	 * @param data
	 *            ChartData Dato
	 * @return Lista de formas con las que se representará el dato.
	 */
	// public java.util.List getShape(ChartData data);

	/**
	 * Retorna el shape utilizado para representar el dato. Es una lista ya que
	 * pueden existir casos en los que un determinado dato pueda tener como
	 * representación varios rectangulos no conectados.
	 *
	 * @param data
	 *            ChartData Dato
	 * @return Shape Representacion del dato.
	 */
	public Shape getShapes(Graphics g, ChartData data, Chart chart);

	/**
	 * Establece el marcador para los datos.
	 *
	 * @param marker
	 *            marcador de inicio (marker [0]) y fin de datos (marker[1].
	 */
	public void setRenderMarker(Marker[] marker);

	/**
	 * Establece las reglas de pintado.
	 *
	 * @param c
	 *            Composite
	 */
	public void setComposite(java.awt.Composite c);

	/**
	 * Pinta el icono que representa los datos pintados.<br>
	 * Habitualmente para la representacion en la leyenda del grafico.<br>
	 * <icono> Descripción de la serie de datos.
	 *
	 * @param Graphics2D
	 *            g Objeto grafico.
	 * @param Rectangle2D
	 *            r espacio destinado para el icono.
	 */
	public void renderIcon(Graphics2D g, Rectangle r);

	/**
	 * Activa/desactiva la visualizacion de la descripcion de los datos.
	 *
	 * @param b
	 *            boolean
	 */
	public void showDataDescription(boolean b);

}
