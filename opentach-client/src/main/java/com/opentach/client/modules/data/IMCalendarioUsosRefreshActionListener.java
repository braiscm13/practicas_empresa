package com.opentach.client.modules.data;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.IntegerDataField;
import com.ontimize.gui.field.MonthComboDataField;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.comp.calendar.UCalendarPanel;
import com.opentach.common.OpentachFieldNames;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.tasks.USwingWorker;

/**
 * The listener interface for receiving IMCalendarioUsosRefreshAction events. The class that is interested in processing a IMCalendarioUsosRefreshAction event implements this
 * interface, and the object created with that class is registered with a component using the component's <code>addIMCalendarioUsosRefreshActionListener<code> method. When the
 * IMCalendarioUsosRefreshAction event occurs, that object's appropriate method is invoked.
 *
 * @see IMCalendarioUsosRefreshActionEvent
 */
public class IMCalendarioUsosRefreshActionListener extends AbstractActionListenerButton {

	/** The Constant logger. */
	private static final Logger		logger		= LoggerFactory.getLogger(IMCalendarioUsosRefreshActionListener.class);

	/** The days by month. */
	private final List<Integer>		daysByMonth	= Arrays.asList(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);

	/** The calendar. */
	@FormComponent(attr = "CALENDAR")
	private UCalendarPanel			calendarField;

	/** The cif. */
	@FormComponent(attr = "CIF")
	private UReferenceDataField	cifField;

	/** The dni. */
	@FormComponent(attr = OpentachFieldNames.IDCONDUCTOR_FIELD)
	private UReferenceDataField	dniField;

	/** The table. */
	@FormComponent(attr = "EInformeUsoVehiculoConductor")
	private Table					table;

	/** The year inicio field. */
	@FormComponent(attr = "YEAR_INICIO")
	private TextComboDataField		yearInicioField;

	/** The month inicio field. */
	@FormComponent(attr = "MONTH_INICIO")
	private MonthComboDataField		monthInicioField;

	/** The year fin field. */
	@FormComponent(attr = "YEAR_FIN")
	private TextComboDataField		yearFinField;

	/** The month fin field. */
	@FormComponent(attr = "MONTH_FIN")
	private MonthComboDataField		monthFinField;

	/** The year field. */
	@FormComponent(attr = "YEAR")
	private IntegerDataField		yearField;

	/** The month field. */
	@FormComponent(attr = "MONTH")
	private IntegerDataField		monthField;

	/** The count month field. */
	@FormComponent(attr = "COUNT_MONTH")
	private IntegerDataField		countMonthField;

	/**
	 * Instantiates a new IM calendario usos report builder action listener.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public IMCalendarioUsosRefreshActionListener() throws Exception {
		super();
	}

	/**
	 * Instantiates a new IM calendario usos report builder action listener.
	 *
	 * @param button
	 *            the button
	 * @param formComponent
	 *            the form component
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMCalendarioUsosRefreshActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	/**
	 * Instantiates a new IM calendario usos report builder action listener.
	 *
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMCalendarioUsosRefreshActionListener(Hashtable params) throws Exception {
		super(params);
	}

	/**
	 * Instantiates a new IM calendario usos report builder action listener.
	 *
	 * @param button
	 *            the button
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public IMCalendarioUsosRefreshActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.buttons.AbstractActionListenerButton#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		new USwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				IMCalendarioUsosRefreshActionListener.this.refresh();
				return null;
			}

			@Override
			protected void done() {
				try {
					this.uget();
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(error, IMCalendarioUsosRefreshActionListener.logger);
				}
			}
		}.executeOperation(this.getForm());
	}

	/**
	 * Refresh.
	 *
	 * @throws Exception
	 *             the exception
	 */
	private void refresh() throws Exception {
		if (ObjectTools.isIn(null, this.cifField.getValue(), this.dniField.getValue(), this.monthInicioField.getValue(), this.yearInicioField.getValue(),
				this.monthFinField.getValue(), this.yearFinField.getValue())) {
			throw new Exception("FALTAN_DATOS");
		}

		int monthInicio = ((Integer) this.monthInicioField.getValue()).intValue();
		int yearInicio = (Integer.parseInt((String) this.yearInicioField.getValue()));
		int monthFin = ((Integer) this.monthFinField.getValue()).intValue();
		int yearFin = (Integer.parseInt((String) this.yearFinField.getValue()));

		this.monthField.setValue(monthInicio - 1);
		this.yearField.setValue(yearInicio);

		Vector<Integer> vyear = new Vector<>(12);
		Vector<Integer> vmonth = new Vector<>(12);

		if (yearInicio == yearFin) { // mismo año
			for (int i = monthInicio; i <= monthFin; i++) {
				vmonth.add(i);
				vyear.add(yearInicio);
			}
		} else { // distinto año de inicio que de fin
			for (int j = yearInicio; j <= yearFin; j++) {
				if (j == yearInicio) {
					for (int k = monthInicio; k <= 12; k++) {
						vmonth.add(k);
						vyear.add(yearInicio);
					}
				} else if (j == yearFin) {
					for (int k = 1; k <= monthFin; k++) {
						vmonth.add(k);
						vyear.add(yearFin);
					}
				} else {
					for (int k = 1; k <= 12; k++) {
						vmonth.add(k);
						vyear.add(j);
					}
				}
			}
		}
		this.countMonthField.setValue(vmonth.size());

		Calendar calendar = Calendar.getInstance();

		if (vmonth.size() > 12) {
			this.getForm().message("MAXIMO_12_MESES", Form.INFORMATION_MESSAGE);
			calendar.set(yearInicio, monthInicio - 1, 1, 0, 0, 0);
			this.getForm().setDataFieldValue("FILTERFECINI", calendar.getTime());
			calendar.set(vyear.get(11).intValue(), vmonth.get(11).intValue() - 1, this.daysByMonth.get(vmonth.get(11).intValue() - 1), 0, 0, 0);
			this.getForm().setDataFieldValue("FILTERFECFIN", calendar.getTime());
		} else {
			calendar.set(yearInicio, monthInicio - 1, 1, 0, 0, 0);
			this.getForm().setDataFieldValue("FILTERFECINI", calendar.getTime());
			calendar.set(yearFin, monthFin - 1, this.daysByMonth.get(monthFin - 1), 0, 0, 0);
			this.getForm().setDataFieldValue("FILTERFECFIN", calendar.getTime());

		}
		((IMCalendarioUsos) this.getInteractionManager()).refreshTables();
		this.calendarField.setShowingYear(vyear, vmonth);
		this.calendarField.repaint();
	}
}
