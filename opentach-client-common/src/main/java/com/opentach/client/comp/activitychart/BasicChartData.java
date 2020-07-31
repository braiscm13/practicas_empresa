package com.opentach.client.comp.activitychart;

import java.awt.Shape;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;

/**
 * Dato básico representable en un Chart. El dato contendrá objetos con la
 * información de ordenadas y abscisas
 *
 *
 * @author Pablo Dorgambide
 * @version 1.0
 * @see ChartData
 */
public class BasicChartData implements ChartData {
	private Shape			shape;
	private List			x;
	private List			y;

	/** Identificador del dato. */
	private final Object	id;

	/** Identificador del tipo de dato. */
	private final Object	kind;

	/** Texto descriptivo del dato representable en el chart. */
	private String			description;

	/**
	 * @param x
	 * @param y
	 * @param id
	 * @param desc
	 */
	public BasicChartData(List x, List y, Object id, Object kind, String desc) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.kind = kind;
		this.description = desc;

	}

	/**
	 * getId
	 * 
	 * @return Object
	 */
	@Override
	public Object getId() {
		return this.id;
	}

	/**
	 * @return
	 */
	@Override
	public List getX() {
		return this.x;
	}

	/**
	 * @return
	 */
	@Override
	public List getY() {
		return this.y;
	}

	/**
	 *
	 *
	 *
	 * @param x
	 */
	@Override
	public void setX(List x) {
		this.x = x;
	}

	/**
	 *
	 *
	 *
	 * @param y
	 */
	@Override
	public void setY(List y) {
		this.y = y;
	}

	/**
	 * <p>
	 * Does ...
	 * </p>
	 *
	 *
	 * @param desc
	 *            Establece la descripcion del dato
	 */
	@Override
	public void setDescription(String desc) {
		this.description = desc;
	}

	/**
	 * <p>
	 * Does ...
	 * </p>
	 *
	 *
	 * @return
	 */
	@Override
	public String getDescription(Chart chart) {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm ");
		Date xdt = (Date) this.x.get(0);
		Date ydt = (Date) this.y.get(0);
		String str = this.description + df.format(ydt) + "<>" + df.format(xdt);

		return (str);

		// your code here
		// // return this.description;
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
	 * getKind
	 *
	 * @return Object
	 */
	@Override
	public Object getKind() {
		return this.kind;
	}

	@Override
	public void setShape(Shape sh) {
		this.shape = sh;
	}

	@Override
	public Shape getShape() {
		return this.shape;
	}

}
