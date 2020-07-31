package com.opentach.client.comp.activitychart;

import java.awt.Shape;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JComponent;

/**
 * Tarea representable en Chart. Una tarea contiene una fecha hora de inicio y
 * fecha hora de fin.
 */
public class Task implements ChartData {

	private Integer	id;
	private String	tipo;
	private Date	start;
	private Date	end;
	private String	description;
	private Shape	shape;
	protected List<Object>	lstY;
	protected List<Object>	lstX;

	public Task() {
		super();
		this.setStart(new Date());
		this.setEnd(new Date());
	}

	/**
	 * @param id
	 * @param tipo
	 * @param start
	 * @param end
	 * @param desc
	 */
	public Task(Integer id, String tipo, Date start, Date end, String desc) {
		super();
		this.setId(id);
		this.setKind(tipo);
		if (start.after(end)) {
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yy HH:mm");
			System.out.println("ERROR: Task Start at " + sdf2.format(start) + " end at " + sdf2.format(end));
		}
		this.setStart(start);
		this.setEnd((start.after(end)) ? start : end);
		this.description = desc;
	}

	public void setKind(String tipo) {
		this.tipo = tipo;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setStart(Date start) {
		this.start = start;
		this.invalidateCoordinates();
	}

	public void setEnd(Date end) {
		this.end = end;
		this.invalidateCoordinates();
	}

	private void invalidateCoordinates() {
		this.lstY = null;
		this.lstX = null;
	}

	/**
	 * @return
	 */
	@Override
	public String getDescription(Chart chart) {
		try {
			if ((this.description == null) && (this.start != null) && (this.end != null)) {
				SimpleDateFormat dff = new SimpleDateFormat("(dd) HH:mm");
				SimpleDateFormat dfs = new SimpleDateFormat("(dd) HH:mm ");

				String str = dff.format(this.start) + " > " + dfs.format(this.end);
				return str;
			}
			// return null;
			return this.description;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param desc
	 */
	@Override
	public void setDescription(String desc) {
		this.description = desc;
	}

	/**
	 * @return
	 */
	@Override
	public String toString() {

		SimpleDateFormat dff = new SimpleDateFormat("dd/MM/yy HH:mm");
		SimpleDateFormat dfs = new SimpleDateFormat("HH:mm");
		String str = "";
		if ((this.id != null) && (this.start != null) && (this.end != null)) {
			str = "Task(" + this.id + "): " + dff.format(this.start) + "<->" + dfs.format(this.end);
		}
		return str;

		// return "Task ("+id+"). start:" + start + "  end:" + end +
		// "   description:"+description;
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
	 * getVisualComponent
	 * 
	 * @return JComponent
	 */
	@Override
	public JComponent getVisualComponent() {
		return null;
	}

	/**
	 * getY
	 * 
	 * @return List
	 */
	@Override
	public List<Object> getY() {
		if (this.lstY != null) {
			return this.lstY;
		} else {
			ArrayList<Object> lst = new ArrayList<Object>();
			GregorianCalendar cal = new GregorianCalendar();
			GregorianCalendar endcal = new GregorianCalendar();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

			cal.setTime(this.start);
			endcal.setTime(this.end);
			// old task for the same day have the y value mark to the start of
			// the day.
			cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));

			lst.add(cal.getTime());

			String strend = sdf.format(endcal.getTime());
			// if end in other day move one day and stored.
			while (sdf.format(cal.getTime()).equals(strend) == false) {
				lst.add(cal.getTime());
				cal.add(Calendar.DATE, 1);
				lst.add(cal.getTime());
			}
			lst.add(cal.getTime());
			this.lstY = lst;
			return lst;
		}
	}

	@Override
	public List<Object> getX() {
		if (this.lstX != null) {
			return this.lstX;
		} else {
			ArrayList<Object> lst = new ArrayList<Object>();
			GregorianCalendar cal = new GregorianCalendar();
			GregorianCalendar endcal = new GregorianCalendar();
			GregorianCalendar hstartofday, hendofday;

			cal.setTime(this.start);
			endcal.setTime(this.end);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

			hstartofday = new GregorianCalendar(1970, 0, 1, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			hendofday = new GregorianCalendar(1970, 0, 1, endcal.get(Calendar.HOUR_OF_DAY), endcal.get(Calendar.MINUTE), endcal.get(Calendar.SECOND));

			lst.add(hstartofday.getTime());
			hstartofday.set(Calendar.HOUR_OF_DAY, hstartofday.getActualMinimum(Calendar.HOUR_OF_DAY));
			hstartofday.set(Calendar.MINUTE, hstartofday.getActualMinimum(Calendar.MINUTE));
			hstartofday.set(Calendar.SECOND, hstartofday.getActualMinimum(Calendar.SECOND));
			hstartofday.set(Calendar.MILLISECOND, hstartofday.getActualMinimum(Calendar.MILLISECOND));

			hendofday.set(Calendar.HOUR_OF_DAY, hendofday.getActualMaximum(Calendar.HOUR_OF_DAY));
			hendofday.set(Calendar.MINUTE, hendofday.getActualMaximum(Calendar.MINUTE));
			hendofday.set(Calendar.SECOND, hendofday.getActualMaximum(Calendar.SECOND));
			hendofday.set(Calendar.MILLISECOND, hendofday.getActualMaximum(Calendar.MILLISECOND));

			String strend = sdf.format(endcal.getTime());
			while (sdf.format(cal.getTime()).equals(strend) == false) {
				cal.add(Calendar.DATE, 1);
				lst.add(hendofday.getTime());
				lst.add(hstartofday.getTime());
			}
			GregorianCalendar hourend = new GregorianCalendar(1970, 0, 1, endcal.get(Calendar.HOUR_OF_DAY), endcal.get(Calendar.MINUTE),
					endcal.get(Calendar.SECOND));
			lst.add(hourend.getTime());
			this.lstX = lst;
			return lst;
		}
	}

	/**
	 * setX
	 * 
	 * @param x
	 *            List
	 */
	@Override
	public void setX(List x) {}

	/**
	 * setY
	 * 
	 * @param y
	 *            List
	 */
	@Override
	public void setY(List y) {}

	/**
	 * getKind
	 * 
	 * @return Object
	 */
	@Override
	public String getKind() {
		return this.tipo;
	}

	/**
	 * getShape
	 * 
	 * @return Shape
	 */
	@Override
	public Shape getShape() {
		return this.shape;
	}

	/**
	 * setShape
	 * 
	 * @param sh
	 *            Shape
	 */
	@Override
	public void setShape(Shape sh) {
		this.shape = sh;
	}

	public Date getStart() {
		return this.start;
	}

	public Date getEnd() {
		return this.end;
	}

}
