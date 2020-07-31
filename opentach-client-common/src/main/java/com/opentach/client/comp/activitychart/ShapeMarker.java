package com.opentach.client.comp.activitychart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * Marcador Basico. Representa los datos con un el shape determinado. Si el dato
 * a representar no posee color asignado se utilizará el color del marcador.
 *
 *
 * @author Fernando Vazquez - Pablo Dorgambide
 * @version 1.0
 */
public class ShapeMarker implements Marker {
	protected Color	olc;
	protected Color	c;
	protected Shape	sh;

	/** @param c */
	public ShapeMarker(Color c) {
		this.c = c;
		this.olc = Color.black;
		this.sh = new Rectangle(6, 6);
	}

	public ShapeMarker(Shape sh, Color fillc, Color outlinec) {
		this.c = fillc;
		this.olc = outlinec;
		this.sh = sh;
	}

	/**
	 * @param c
	 *            Color utilizado para representar los datos sin color
	 */
	public void setColor(Color c) {
		this.c = c;
	}

	/**
	 * @param c
	 *            Color del contorno
	 */
	public void setOutLineColor(Color c) {
		this.olc = c;
	}

	/**
	 * @param s
	 *            Forma con la que se representará el dato
	 */
	public void setShape(Shape s) {
		this.sh = s;
	}

	/**
	 * @param g
	 * @param x
	 * @param y
	 * @param datacolor
	 */
	@Override
	public void draw(Graphics g, int x, int y, Color datacolor) {
		Graphics2D gc = (Graphics2D) g;
		Color oldc = gc.getColor();
		java.awt.geom.Rectangle2D r = this.sh.getBounds2D();
		gc.translate(x - r.getCenterX(), y - r.getCenterY());
		if (datacolor != null) {
			gc.setColor(datacolor);
		} else {
			gc.setColor(this.c);
		}
		gc.fill(this.sh);
		if (this.olc != null) {
			gc.setColor(this.olc);
			gc.draw(this.sh);
		}
		gc.translate(-x + r.getCenterX(), -y + r.getCenterY());
		gc.setColor(oldc);
	}
}
