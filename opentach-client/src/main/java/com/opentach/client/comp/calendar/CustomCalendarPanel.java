package com.opentach.client.comp.calendar;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.nachocalendar.components.CalendarUtils;
import net.sf.nachocalendar.components.DayPanel;
import net.sf.nachocalendar.components.DayRenderer;
import net.sf.nachocalendar.components.DefaultDayRenderer;
import net.sf.nachocalendar.components.DefaultHeaderRenderer;
import net.sf.nachocalendar.components.HeaderRenderer;
import net.sf.nachocalendar.event.DateSelectionEvent;
import net.sf.nachocalendar.event.DateSelectionListener;
import net.sf.nachocalendar.model.DataModel;
import net.sf.nachocalendar.model.DateSelectionModel;
import net.sf.nachocalendar.model.DefaultDateSelectionModel;

public class CustomCalendarPanel extends JPanel implements ChangeListener {
	private boolean							antiAliased;
	private KeyListener						klistener;
	private MouseListener					mlistener;
	private DateSelectionListener			listlistener;
	private DateSelectionModel				dateSelectionModel;
	private DataModel						datamodel;
	private HeaderRenderer					headerrenderer;
	private DayRenderer						dayrenderer;
	private final Calendar					navigation, calendar;
	private Date							minDate, maxDate;
	private int								minimalDaysInFirstWeek;
	private boolean							printMoon;
	private boolean							showToday;

	/** Array with the panels. */
	private MonthWrapperPanel[]				months;

	/** Orientation. */
	private int								orientation;

	/**
	 * Horizontal orientation.
	 */
	public static final int					HORIZONTAL	= 0;
	/** Vertical orientation. */
	public static final int					VERTICAL	= 1;
	/** Left Position. */
	public static final int					LEFT		= 0;
	/** Right Position. */
	public static final int					RIGHT		= 1;
	/** Up Position. */
	public static final int					UP			= 0;
	/** Down Position. */
	public static final int					DOWN		= 1;

	/** Today Button */
	private final JButton					today;

	/** Array representing the working days. */
	private boolean[]						workingdays	= { false, true, true, true, true, true, true };

	/** Calendar used to calc dates. */
	private final Calendar					cal;

	private int								quantity;
	private Date							date;
	private final boolean					showWeekNumber;

	/** Utility field holding list of ChangeListeners. */
	private transient java.util.ArrayList	changeListenerList;

	// /** Holds value of property yearPosition. */
	// private int yearPosition;

	/** Default constructor, constructs a vertical panel with 3 months. */
	public CustomCalendarPanel() {
		this(3, CustomCalendarPanel.VERTICAL, 1, 2012);
	}

	public CustomCalendarPanel(int quantity, int orientation) {
		this(12, orientation, 1, 2012);
	}

	/**
	 * Constructs a panel with 3 months and the provided orientation.
	 *
	 * @param quantity
	 *            quantity of months to show at once
	 */
	public CustomCalendarPanel(int quantity, int month, int year) {
		this(quantity, CustomCalendarPanel.VERTICAL, month, year);
	}

	/**
	 * Constructrs a panel with 3 months, Vertical.
	 *
	 * @param showWeekNumbers
	 */
	public CustomCalendarPanel(boolean showWeekNumbers, int month, int year) {
		this(12, CustomCalendarPanel.VERTICAL, showWeekNumbers, month, year);
	}

	/**
	 * Constructs a panel with the provided quantity and orientation.
	 *
	 * @param quantity
	 *            months to show at once
	 * @param orientation
	 *            orientation
	 */
	public CustomCalendarPanel(int quantity, int orientation, int month, int year) {
		this(quantity, orientation, true, month, year);
	}

	/**
	 * Creates a new instance of CalendarPanel.
	 *
	 * @param showWeekNumber
	 *            true to show the week numbers
	 * @param quantity
	 *            months to show at once
	 * @param orientation
	 *            the orientation
	 */
	public CustomCalendarPanel(int quantity, int orientation, boolean showWeekNumber, int month, int year) {
		if (quantity < 1) {
			quantity = 1;
		}
		if (quantity > 12) {
			quantity = 12;
		}
		this.quantity = quantity;
		this.showWeekNumber = showWeekNumber;
		this.orientation = orientation;
		this.navigation = Calendar.getInstance();
		this.calendar = Calendar.getInstance();
		this.dateSelectionModel = new DefaultDateSelectionModel();
		this.today = new JButton(CalendarUtils.getMessage("today"));
		this.today.setVisible(false);
		this.cal = Calendar.getInstance();
		this.setLayout(new BorderLayout());

		this.createListeners();
		this.dateSelectionModel.addDateSelectionListener(this.listlistener);
		this.setQuantity(quantity);
		this.initDisplayPanel();
		this.setShowingMonth(month, year);

		if (orientation == CustomCalendarPanel.VERTICAL) {
			this.layoutVertical();
		} else {
			this.layoutHorizontal();
		}

		this.setValue(new Date());
		this.setRenderer(new DefaultDayRenderer());
		this.setHeaderRenderer(new DefaultHeaderRenderer());

	}

	/**
	 * Displays the panel right after the start
	 *
	 * @param quantity
	 * @param eternalScroll
	 */
	private void initDisplayPanel() {
		Vector<Object> vyear = new Vector<Object>();
		Vector<Object> vmonth = new Vector<Object>();
		for (int i = 0; i < 12; i++) {
			vmonth.add(i);
			vyear.add(this.cal.get(Calendar.YEAR));
		}
		this.setShowingYear(vyear, vmonth);

	}

	/** Utility method used during initialization. */
	private void createListeners() {
		// listener to pass events
		this.klistener = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				boolean changed = false;
				int keycode = e.getKeyCode();
				CustomCalendarPanel.this.navigation.setTime(CustomCalendarPanel.this.calendar.getTime());
				if ((keycode == KeyEvent.VK_LEFT) || (keycode == 226)) {
					CustomCalendarPanel.this.navigation.add(Calendar.DAY_OF_YEAR, -1);
					changed = true;
				}
				if ((keycode == KeyEvent.VK_RIGHT) || (keycode == 227)) {
					CustomCalendarPanel.this.navigation.add(Calendar.DAY_OF_YEAR, 1);
					changed = true;
				}
				if ((keycode == KeyEvent.VK_UP) || (keycode == 224)) {
					CustomCalendarPanel.this.navigation.add(Calendar.DAY_OF_YEAR, -7);
					changed = true;
				}

				if ((keycode == KeyEvent.VK_DOWN) || (keycode == 225)) {
					CustomCalendarPanel.this.navigation.add(Calendar.DAY_OF_YEAR, 7);
					changed = true;
				}
				if ((keycode == KeyEvent.VK_PAGE_UP)) {
					CustomCalendarPanel.this.navigation.add(Calendar.MONTH, -1);
					changed = true;
				}
				if ((keycode == KeyEvent.VK_PAGE_DOWN)) {
					CustomCalendarPanel.this.navigation.add(Calendar.MONTH, 1);
					changed = true;
				}
				if (changed) {
					if (!CustomCalendarPanel.this.isShowing(CustomCalendarPanel.this.navigation.getTime())) {
						CustomCalendarPanel.this.showMonth(CustomCalendarPanel.this.navigation.getTime());
					}
					if ((!e.isControlDown()) && (!e.isShiftDown())) {
						CustomCalendarPanel.this.dateSelectionModel.clearSelection();
						if (e.isShiftDown()) {
							CustomCalendarPanel.this.dateSelectionModel
							.addSelectionInterval(CustomCalendarPanel.this.dateSelectionModel.getLeadSelectionDate(),
									CustomCalendarPanel.this.navigation.getTime());
						} else {
							CustomCalendarPanel.this.dateSelectionModel.addSelectionInterval(CustomCalendarPanel.this.navigation.getTime(),
									CustomCalendarPanel.this.navigation.getTime());
						}
					} else {
						if (e.isShiftDown()) {
							CustomCalendarPanel.this.dateSelectionModel
							.addSelectionInterval(CustomCalendarPanel.this.dateSelectionModel.getLeadSelectionDate(),
									CustomCalendarPanel.this.navigation.getTime());
						} else {
							if (CustomCalendarPanel.this.dateSelectionModel.isSelectedDate(CustomCalendarPanel.this.navigation.getTime())) {
								CustomCalendarPanel.this.dateSelectionModel.removeSelectionInterval(CustomCalendarPanel.this.navigation.getTime(),
										CustomCalendarPanel.this.navigation.getTime());
							} else {
								CustomCalendarPanel.this.dateSelectionModel.addSelectionInterval(CustomCalendarPanel.this.navigation.getTime(),
										CustomCalendarPanel.this.navigation.getTime());
							}
						}
					}
					CustomCalendarPanel.this.dateSelectionModel.setLeadSelectionDate(CustomCalendarPanel.this.navigation.getTime());
					CustomCalendarPanel.this.calendar.setTime(CustomCalendarPanel.this.navigation.getTime());
					CustomCalendarPanel.this.refreshSelection();
					CustomCalendarPanel.this.repaint();
					// monthpanel.repaint();
				}
				CustomCalendarPanel.this.fireKeyListenerKeyPressed(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				CustomCalendarPanel.this.fireKeyListenerKeyReleased(e);
			}

			@Override
			public void keyTyped(KeyEvent e) {
				CustomCalendarPanel.this.fireKeyListenerKeyTyped(e);
			}
		};

		/*
		 * monthlistener = new MonthChangeListener() { public void
		 * monthIncreased(MonthChangeEvent e) { setDate(e.getDate()); } public
		 * void monthDecreased(MonthChangeEvent e) { setDate(e.getDate()); } };
		 */

		this.mlistener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				DayPanel dp = (DayPanel) e.getSource();
				if (!dp.isEnabled() || !dp.isComponentEnabled()) {
					return;
				}
				CustomCalendarPanel.this.dateSelectionModel.setValueIsAdjusting(true);
				if (!e.isControlDown()) {
					CustomCalendarPanel.this.dateSelectionModel.clearSelection();
					if (e.isShiftDown()) {
						CustomCalendarPanel.this.dateSelectionModel.addSelectionInterval(
								CustomCalendarPanel.this.dateSelectionModel.getLeadSelectionDate(), dp.getDate());
					} else {
						CustomCalendarPanel.this.dateSelectionModel.addSelectionInterval(dp.getDate(), dp.getDate());
					}
				} else {
					if (e.isShiftDown()) {
						CustomCalendarPanel.this.dateSelectionModel.addSelectionInterval(
								CustomCalendarPanel.this.dateSelectionModel.getLeadSelectionDate(), dp.getDate());
					} else {
						if (CustomCalendarPanel.this.dateSelectionModel.isSelectedDate(dp.getDate())) {
							CustomCalendarPanel.this.dateSelectionModel.removeSelectionInterval(dp.getDate(), dp.getDate());
						} else {
							CustomCalendarPanel.this.dateSelectionModel.addSelectionInterval(dp.getDate(), dp.getDate());
						}
					}
				}
				CustomCalendarPanel.this.dateSelectionModel.setLeadSelectionDate(dp.getDate());
				CustomCalendarPanel.this.repaint();
				dp.requestFocus();
				CustomCalendarPanel.this.calendar.setTime(dp.getDate());
				/*
				 * fireActionListenerActionPerformed(new ActionEvent(this, 0,
				 * "clicked"));
				 */
				CustomCalendarPanel.this.dateSelectionModel.setValueIsAdjusting(false);
				CustomCalendarPanel.this.refreshSelection();
				CustomCalendarPanel.this.repaint();
			}
		};

		// listener para la seleccion
		this.listlistener = new DateSelectionListener() {
			@Override
			public void valueChanged(DateSelectionEvent e) {
				for (int i = 0; i < CustomCalendarPanel.this.months.length; i++) {
					DayPanel[] daypanels = CustomCalendarPanel.this.months[i].getMonthPanel().getDaypanels();
					for (int j = 0; j < daypanels.length; j++) {
						if (CustomCalendarPanel.this.dateSelectionModel.isSelectedDate(daypanels[j].getDate())) {
							daypanels[j].setSelected(true);
						} else {
							daypanels[j].setSelected(false);
						}
					}
				}
				CustomCalendarPanel.this.repaint();
				CustomCalendarPanel.this.fireChangeListenerStateChanged(new ChangeEvent(CustomCalendarPanel.this));
			}
		};
	}

	/**
	 * Changes the orientation (horizontal or vertical).
	 *
	 * @param orientation
	 *            the new orientation
	 */
	public void setOrientation(int orientation) {
		if (orientation == this.orientation) {
			return;
		}
		int old = this.orientation;
		this.orientation = orientation;
		this.removeAll();
		if (orientation == CustomCalendarPanel.VERTICAL) {
			this.layoutVertical();
		} else {
			this.layoutHorizontal();
		}
		this.firePropertyChange("orientation", old, orientation);
	}

	/** Method used to layout vertical. */
	private void layoutVertical() {
		JPanel centro = new JPanel(new GridLayout(this.months.length, 1));
		for (int i = 0; i < this.months.length; i++) {
			centro.add(this.months[i]);
		}
		this.add(centro, BorderLayout.CENTER);
	}

	/** Method used to layout horizontal. */
	private void layoutHorizontal() {
		JPanel centro = new JPanel(new GridLayout(1, this.months.length));
		for (int i = 0; i < this.months.length; i++) {
			centro.add(this.months[i]);
		}
		this.add(centro, BorderLayout.CENTER);
	}

	/**
	 * Registers ChangeListener to receive events.
	 *
	 * @param listener
	 *            The listener to register.
	 */
	public synchronized void addChangeListener(javax.swing.event.ChangeListener listener) {
		if (listener == null) {
			return;
		}
		if (this.changeListenerList == null) {
			this.changeListenerList = new java.util.ArrayList();
		}
		this.changeListenerList.add(listener);
	}

	/**
	 * Removes ChangeListener from the list of listeners.
	 *
	 * @param listener
	 *            The listener to remove.
	 */
	public synchronized void removeChangeListener(javax.swing.event.ChangeListener listener) {
		if (this.changeListenerList != null) {
			this.changeListenerList.remove(listener);
		}
	}

	/**
	 * Notifies all registered listeners about the event.
	 *
	 * @param event
	 *            The event to be fired
	 */
	private void fireChangeListenerStateChanged(javax.swing.event.ChangeEvent event) {
		java.util.ArrayList<Object> list;
		synchronized (this) {
			if (this.changeListenerList == null) {
				return;
			}
			list = (java.util.ArrayList<Object>) this.changeListenerList.clone();
		}
		for (int i = 0; i < list.size(); i++) {
			((javax.swing.event.ChangeListener) list.get(i)).stateChanged(event);
		}
	}

	/**
	 * Event fired when the selected day changes.
	 *
	 * @param e
	 *            event fired
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		this.fireChangeListenerStateChanged(e);
	}

	/**
	 * Sets the month shown.
	 *
	 * @param month
	 *            month to show
	 */
	private void setShowingMonth(int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		for (int i = 0; i < this.months.length; i++) {
			this.months[i].getMonthPanel().setMonth(cal.getTime());
			cal.add(Calendar.MONTH, 1);
		}
		this.minDate = this.months[0].getMonthPanel().getMinDate();
		this.maxDate = this.months[this.months.length - 1].getMonthPanel().getMaxDate();
		this.refreshSelection();
	}

	/**
	 * Sets the year shown.
	 *
	 * @param year
	 *            the year to show
	 */
	public void setShowingYear(Vector year, Vector month) {
		this.setQuantity(month.size());

		for (int i = 0; i < this.months.length; i++) {
			this.cal.setTime(this.months[i].getMonthPanel().getMonth());
			this.cal.set(Calendar.MONTH, ((Integer) month.get(i)).intValue() - 1);
			this.cal.set(Calendar.YEAR, ((Integer) year.get(i)).intValue());
			this.months[i].getMonthPanel().setMonth(this.cal.getTime());
		}
		this.refreshSelection();
	}

	/**
	 * Getter for property date.
	 *
	 * @return Value of property date.
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * Setter for property date.
	 *
	 * @param date
	 *            New value of property date.
	 */
	public void setDate(Date date) {
		if (date == null) {
			return;
		}
		this.date = date;
		this.cal.setTime(date);
		if (!this.isShowing(date)) {
			this.showMonth(date);
		}
		this.dateSelectionModel.setSelectedDate(date);
		this.refreshSelection();
		this.repaint();
	}

	/**
	 * Getter for property workingdays.
	 *
	 * @return Value of property workingdays.
	 */
	public boolean[] getWorkingdays() {
		return this.workingdays;
	}

	/**
	 * Setter for property workingdays.
	 *
	 * @param workingdays
	 *            New value of property workingdays.
	 */
	public void setWorkingdays(boolean[] workingdays) {
		boolean[] old = this.workingdays;
		this.workingdays = workingdays;
		for (int i = 0; i < this.months.length; i++) {
			this.months[i].getMonthPanel().setWorkingdays(workingdays);
		}
		this.firePropertyChange("workingDays", old, workingdays);
	}

	/**
	 * Getter for property renderer.
	 *
	 * @return Value of property renderer.
	 */
	public DayRenderer getRenderer() {
		return this.months[0].getMonthPanel().getRenderer();
	}

	/**
	 * Setter for property renderer.
	 *
	 * @param renderer
	 *            New value of property renderer.
	 */
	public void setRenderer(DayRenderer renderer) {
		this.dayrenderer = renderer;
		for (int i = 0; i < this.months.length; i++) {
			this.months[i].getMonthPanel().setRenderer(renderer);
		}
	}

	/**
	 * Getter for property model.
	 *
	 * @return Value of property model.
	 */
	public DataModel getModel() {
		return this.months[0].getMonthPanel().getModel();
	}

	/**
	 * Setter for property model.
	 *
	 * @param model
	 *            New value of property model.
	 */
	public void setModel(DataModel model) {
		this.datamodel = model;
		for (int i = 0; i < this.months.length; i++) {
			this.months[i].getMonthPanel().setModel(model);
		}
	}

	/**
	 * Getter for property firstDayOfWeek.
	 *
	 * @return Value of property firstDayOfWeek.
	 */
	public int getFirstDayOfWeek() {
		return this.months[0].getMonthPanel().getFirstDayOfWeek();
	}

	/**
	 * Setter for property firstDayOfWeek.
	 *
	 * @param firstDayOfWeek
	 *            New value of property firstDayOfWeek.
	 */
	public void setFirstDayOfWeek(int firstDayOfWeek) {
		if ((firstDayOfWeek == Calendar.SUNDAY) || (firstDayOfWeek == Calendar.MONDAY)) {
			int old = this.months[0].getMonthPanel().getFirstDayOfWeek();
			if (firstDayOfWeek == this.months[0].getMonthPanel().getFirstDayOfWeek()) {
				return;
			}
			for (int i = 0; i < this.months.length; i++) {
				this.months[i].getMonthPanel().setFirstDayOfWeek(firstDayOfWeek);
			}
			this.refreshSelection();
			this.repaint();
			this.firePropertyChange("firstDayOfWeek", old, firstDayOfWeek);
		}
	}

	/** Refreshes the display of this month. */
	public void refresh() {
		for (int i = 0; i < this.months.length; i++) {
			this.months[i].getMonthPanel().refresh();
		}
	}

	/**
	 * Getter for property headerRenderer.
	 *
	 * @return Value of property headerRenderer.
	 */
	public HeaderRenderer getHeaderRenderer() {
		return this.months[0].getMonthPanel().getHeaderRenderer();
	}

	/**
	 * Setter for property headerRenderer.
	 *
	 * @param headerRenderer
	 *            New value of property headerRenderer.
	 */
	public void setHeaderRenderer(HeaderRenderer headerRenderer) {
		this.headerrenderer = headerRenderer;
		for (int i = 0; i < this.months.length; i++) {
			this.months[i].getMonthPanel().setHeaderRenderer(headerRenderer);
		}
	}

	/**
	 * Getter for property orientation.
	 *
	 * @return Value of property orientation.
	 */
	public int getOrientation() {
		return this.orientation;
	}

	/**
	 * Getter for property quantity.
	 *
	 * @return Value of property quantity.
	 */
	public int getQuantity() {
		return this.months.length;
	}

	/**
	 * Setter for property quantity.
	 *
	 * @param quantity
	 *            New value of property quantity.
	 */
	public void setQuantity(int quantity) {
		if (quantity < 1) {
			quantity = 1;
		}
		if (quantity > 12) {
			quantity = 12;
		}
		if ((this.months != null) && (this.months.length == quantity)) {
			return;
		}
		int old = this.quantity;
		this.months = new MonthWrapperPanel[quantity];
		for (int i = 0; i < this.months.length; i++) {
			this.months[i] = new MonthWrapperPanel(this.showWeekNumber);
			this.months[i].getMonthPanel().showTitle(true);
			this.months[i].getMonthPanel().setModel(this.datamodel);
			this.months[i].getMonthPanel().setRenderer(this.dayrenderer);
			this.months[i].getMonthPanel().setHeaderRenderer(this.headerrenderer);
			this.months[i].getMonthPanel().setMinimalDaysInFirstWeek(this.minimalDaysInFirstWeek);
			this.months[i].getMonthPanel().setDay(new Date());
			DayPanel[] daypanels = this.months[i].getMonthPanel().getDaypanels();
			for (int j = 0; j < daypanels.length; j++) {
				daypanels[j].addKeyListener(this.klistener);
				daypanels[j].addMouseListener(this.mlistener);
			}
		}
		int oldor = this.orientation;
		this.orientation = -1;
		this.setOrientation(oldor);
		this.quantity = quantity;
		if (old != 0) {
			this.firePropertyChange("quantity", old, quantity);
		}
	}

	/**
	 * Registers KeyListener to receive events.
	 *
	 * @param listener
	 *            The listener to register.
	 */
	@Override
	public synchronized void addKeyListener(java.awt.event.KeyListener listener) {
		if (this.listenerList == null) {
			this.listenerList = new javax.swing.event.EventListenerList();
		}
		this.listenerList.add(java.awt.event.KeyListener.class, listener);
	}

	/**
	 * Removes KeyListener from the list of listeners.
	 *
	 * @param listener
	 *            The listener to remove.
	 */
	@Override
	public synchronized void removeKeyListener(java.awt.event.KeyListener listener) {
		this.listenerList.remove(java.awt.event.KeyListener.class, listener);
	}

	/**
	 * Notifies all registered listeners about the event.
	 *
	 * @param event
	 *            The event to be fired
	 */
	private void fireKeyListenerKeyTyped(java.awt.event.KeyEvent event) {
		if (this.listenerList == null) {
			return;
		}
		Object[] listeners = this.listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == java.awt.event.KeyListener.class) {
				((java.awt.event.KeyListener) listeners[i + 1]).keyTyped(event);
			}
		}
	}

	/**
	 * Notifies all registered listeners about the event.
	 *
	 * @param event
	 *            The event to be fired
	 */
	private void fireKeyListenerKeyPressed(java.awt.event.KeyEvent event) {
		if (this.listenerList == null) {
			return;
		}
		Object[] listeners = this.listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == java.awt.event.KeyListener.class) {
				((java.awt.event.KeyListener) listeners[i + 1]).keyPressed(event);
			}
		}
	}

	/**
	 * Notifies all registered listeners about the event.
	 *
	 * @param event
	 *            The event to be fired
	 */
	private void fireKeyListenerKeyReleased(java.awt.event.KeyEvent event) {
		if (this.listenerList == null) {
			return;
		}
		Object[] listeners = this.listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == java.awt.event.KeyListener.class) {
				((java.awt.event.KeyListener) listeners[i + 1]).keyReleased(event);
			}
		}
	}

	/**
	 * Enables or disables the component
	 *
	 * @param enabled
	 *            true for enabling
	 */
	@Override
	public void setEnabled(boolean enabled) {
		for (int i = 0; i < this.months.length; i++) {
			this.months[i].setEnabled(enabled);
		}
		this.repaint();
		// this.enabled = enabled;
		super.setEnabled(enabled);
	}

	/**
	 * @return Returns the antiAliased.
	 */
	public boolean isAntiAliased() {
		return this.antiAliased;
	}

	/**
	 * @param antiAliased
	 *            The antiAliased to set.
	 */
	public void setAntiAliased(boolean antiAliased) {
		boolean old = this.antiAliased;
		this.antiAliased = antiAliased;
		for (int i = 0; i < this.months.length; i++) {
			this.months[i].getMonthPanel().setAntiAliased(antiAliased);
		}
		this.firePropertyChange("antiAliased", old, antiAliased);
	}

	/**
	 * @return Returns the selectionMode.
	 */
	public int getSelectionMode() {
		return this.dateSelectionModel.getSelectionMode();
	}

	/**
	 * @param selectionMode
	 *            The selectionMode to set.
	 */
	public void setSelectionMode(int selectionMode) {
		int old = this.dateSelectionModel.getSelectionMode();
		this.dateSelectionModel.setSelectionMode(selectionMode);
		this.refreshSelection();
		this.firePropertyChange("selectionMode", old, selectionMode);
	}

	private void refreshSelection() {
		for (int i = 0; i < this.months.length; i++) {
			DayPanel[] daypanels = this.months[i].getMonthPanel().getDaypanels();
			for (int j = 0; j < daypanels.length; j++) {
				if (!daypanels[j].isEnabled()) {
					daypanels[j].setSelected(false);
					continue;
				}
				daypanels[j].setSelected(this.dateSelectionModel.isSelectedDate(daypanels[j].getDate()));
			}
		}
	}

	private boolean isShowing(Date date) {
		if ((this.minDate == null) || date.before(this.minDate)) {
			return false;
		}
		if (date.after(this.maxDate)) {
			return false;
		}
		return true;
	}

	private void showMonth(Date d) {
		// Calendar cal = Calendar.getInstance();
		// cal.setTime(d);
		// setShowingYear(cal.get(Calendar.YEAR));
		// if (isShowing(d))
		// return;
		//
		// int month = cal.get(Calendar.MONTH);
		// int middle = (this.quantity / 2);
		// int show = 0;
		// if (month < this.scroll.getValue()) {
		// show = month;
		// } else {
		// show = month - this.quantity + 1;
		// }
		// if (show < 0) {
		// show = 0;
		// }
		// if (show > (11 - this.quantity + middle)) {
		// show = (11 - this.quantity + middle);
		// }
		// this.scroll.setValue(show);
	}

	/**
	 * Returns the selected date.
	 *
	 * @return Selected Date
	 */
	public Object getValue() {
		return this.dateSelectionModel.getSelectedDate();
	}

	/**
	 * Return the selected dates as an Array.
	 *
	 * @return Selected Dates
	 */
	public Object[] getValues() {
		return this.dateSelectionModel.getSelectedDates();
	}

	/**
	 * Sets the selected date.
	 *
	 * @param date
	 *            Date to select
	 */
	public void setValue(Object date) {
		try {
			this.setDate(CalendarUtils.convertToDate(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the selected dates.
	 *
	 * @param dates
	 *            Array with the Dates
	 */
	public void setValues(Object[] dates) {
		this.dateSelectionModel.setSelectedDates(dates);
		this.refreshSelection();
		this.repaint();
		this.showMonth((Date) this.dateSelectionModel.getSelectedDates()[0]);
	}

	/**
	 * @return Returns the dateSelectionModel.
	 */
	public DateSelectionModel getDateSelectionModel() {
		return this.dateSelectionModel;
	}

	/**
	 * @param dateSelectionModel
	 *            The dateSelectionModel to set.
	 */
	public void setDateSelectionModel(DateSelectionModel dateSelectionModel) {
		if (dateSelectionModel != null) {
			this.dateSelectionModel.removeDateSelectionListener(this.listlistener);
			this.dateSelectionModel = dateSelectionModel;
			dateSelectionModel.addDateSelectionListener(this.listlistener);
		}
	}

	/**
	 * Specifies how many days should the first week of the year contain <br>
	 * If not specified, i.e. 0, then no changes from the defaults are done
	 *
	 * @param number
	 */
	public void setMinimalDaysInFirstWeek(int number) {
		if (this.months[0].getMonthPanel().getMinimalDaysInFirstWeek() != number) {
			for (int i = 0; i < this.months.length; i++) {
				this.months[i].getMonthPanel().setMinimalDaysInFirstWeek(number);
			}
			this.refreshSelection();
			this.repaint();
			this.firePropertyChange("minimalDaysInFirstWeek", this.minimalDaysInFirstWeek, number);
			this.minimalDaysInFirstWeek = number;
		}
	}

	/**
	 * @return how many weekdays does the first week of the year have
	 */
	public int getMinimalDaysInFirstWeek() {
		return this.months[0].getMonthPanel().getMinimalDaysInFirstWeek();
	}

	/**
	 * @return Returns the printMoon.
	 */
	public boolean isPrintMoon() {
		return this.printMoon;
	}

	/**
	 * @param printMoon
	 *            The printMoon to set.
	 */
	public void setPrintMoon(boolean printMoon) {
		if (this.printMoon != printMoon) {
			this.printMoon = printMoon;
			for (int i = 0; i < this.months.length; i++) {
				this.months[i].getMonthPanel().setPrintMoon(printMoon);
			}
			this.refreshSelection();
			this.repaint();
			this.firePropertyChange("printMoon", printMoon, !printMoon);
		}
	}

	public void setTodayCaption(String caption) {
		if (caption == null) {
			this.today.setText(CalendarUtils.getMessage("today"));
		} else {
			this.today.setText(caption);
		}
	}

	/**
	 * @return Returns the showToday.
	 */
	public boolean isShowToday() {
		return this.showToday;
	}

	/**
	 * @param showToday
	 *            The showToday to set.
	 */
	public void setShowToday(boolean showToday) {
		this.today.setVisible(showToday);
		this.repaint();
		this.showToday = showToday;
	}

	// public void setMonthLeftPanelRenderer(IPanelRenderer renderer) {
	// for (MonthWrapperPanel panel : this.months) {
	// panel.setLeftRenderer(renderer);
	// }
	// }

	public void setMonthRightPanelRenderer(IPanelRenderer renderer) {
		for (MonthWrapperPanel panel : this.months) {
			panel.setRightRenderer(renderer);

		}

	}

	public MonthWrapperPanel[] getMonths() {
		return this.months;
	}
}
