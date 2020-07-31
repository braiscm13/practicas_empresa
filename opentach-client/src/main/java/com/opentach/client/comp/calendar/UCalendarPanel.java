package com.opentach.client.comp.calendar;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JScrollPane;

import com.ontimize.gui.Form;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;

import net.sf.nachocalendar.components.DayRenderer;
import net.sf.nachocalendar.components.HeaderRenderer;
import net.sf.nachocalendar.model.DataModel;

public class UCalendarPanel extends JScrollPane implements FormComponent, AccessForm, IdentifiedElement {

	protected CustomCalendarPanel	calendarPanel;
	protected Form					parentForm;
	protected Object				attr;

	protected IPanelRenderer		monthRightPanelRenderer;
	protected DayRenderer			dayRenderer;

	public UCalendarPanel(Hashtable parameters) throws Exception {
		super(new CustomCalendarPanel(12, 1));
		this.calendarPanel = (CustomCalendarPanel) this.getViewport().getView();
		this.init(parameters);
	}

	@Override
	public Object getConstraints(LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagConstraints) {
			return new GridBagConstraints(-1, -1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.CENTER, new Insets(0, 0, 0, 0), 0, 0);
		}
		return null;
	}

	@Override
	public void init(Hashtable parameters) throws Exception {
		this.attr = parameters.get("attr");
		this.calendarPanel.setEnabled(true);
		this.calendarPanel.setWorkingdays(new boolean[] { false, true, true, true, true, true, false });
		this.setAutoscrolls(true);
		this.setWheelScrollingEnabled(false);
		this.getVerticalScrollBar().setUnitIncrement(1);
		this.setMonthRightPanelRenderer((IPanelRenderer) this.parseClass((String) parameters.get("rightrenderer"), parameters));
		this.setHeaderRenderer((HeaderRenderer) this.parseClass((String) parameters.get("headerrenderer"), parameters));

		DataModel model = (DataModel) this.parseClass((String) parameters.get("model"), parameters);
		if (model != null) {
			this.calendarPanel.setModel(model);
		}
		this.setDayRenderer((DayRenderer) this.parseClass((String) parameters.get("dayrenderer"), parameters));
		this.setOpaque(false);
		this.calendarPanel.setOpaque(false);
	}

	public CustomCalendarPanel getCalendarPanel() {
		return this.calendarPanel;
	}

	protected Object parseClass(String className, Hashtable parameters) {
		if (className != null) {
			try {
				Class<?> cl = Class.forName(className);
				Constructor<?> constructor = cl.getConstructor(Hashtable.class);
				return constructor.newInstance(parameters);
			} catch (Exception e) {
				try {
					Class<?> cl = Class.forName(className);
					Constructor<?> constructor = cl.getConstructor();
					return constructor.newInstance();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public Vector<Object> getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(Locale arg0) {}

	@Override
	public void setResourceBundle(ResourceBundle arg0) {}

	@Override
	public void setParentForm(Form form) {
		this.parentForm = form;
		((CalendarDatesDescriptionRenderer) this.monthRightPanelRenderer).setParentForm(form);
		((DayRendererDecorator) this.dayRenderer).setParentForm(form);
	}

	@Override
	public Object getAttribute() {
		return this.attr;
	}

	@Override
	public void initPermissions() {

	}

	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(10, 10);
	}

	public void setMonthRightPanelRenderer(IPanelRenderer renderer) {
		if (renderer != null) {
			this.monthRightPanelRenderer = renderer;
			this.calendarPanel.setMonthRightPanelRenderer(renderer);
		}
	}

	public void setHeaderRenderer(HeaderRenderer renderer) {
		if (renderer != null) {
			this.calendarPanel.setHeaderRenderer(renderer);
		}
	}

	public void setDayRenderer(DayRenderer renderer) {
		if (renderer != null) {
			this.dayRenderer = renderer;
			this.calendarPanel.setRenderer(this.dayRenderer);
		}
	}

	public void setShowingYear(Vector year, Vector month) {
		this.calendarPanel.setShowingYear(year, month);
	}

}
