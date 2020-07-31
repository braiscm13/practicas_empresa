package com.opentach.client.comp.activitychart;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Marcador de los datos de un chart. Permite la representacion de simbolos
 * (shapes) en los puntos de los datos.
 * 
 * @author Pablo Dorgambide
 * @version 1.0
 */
public interface Marker {
	/**
	 * Pinta el simbolo en la posicion x,y del contexto grafico g.
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param datacolor
	 *            Color del dato, si se especifica prevalece sobre el color del
	 *            simbolo.
	 */
	public void draw(Graphics g, int x, int y, Color datacolor);
}
