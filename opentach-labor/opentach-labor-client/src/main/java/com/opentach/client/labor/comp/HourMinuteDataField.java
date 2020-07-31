package com.opentach.client.labor.comp;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.document.AdvancedIntegerDocument;
import com.utilmize.client.gui.field.UIntegerDataField;

public class HourMinuteDataField extends UIntegerDataField {
	private static final Logger	LOGGER			= LoggerFactory.getLogger(HourMinuteDataField.class);
	/** The Constant HH. */
	public static final int		HH				= 60 * 60 * 1000;
	/** The Constant MM. */
	public static final int		MM				= 60 * 1000;
	protected ELabel			labelHourMinute;

	public HourMinuteDataField(Hashtable params) {
		super(params);
		System.out.println(this.labelHourMinute.getText());
	}

	@Override
	public void init(Hashtable parameters) {
		super.init(parameters);
		this.labelHourMinute = new ELabel(-1);
		GridBagConstraints constraints = ((GridBagLayout) this.getLayout()).getConstraints(this.dataField);
		constraints.gridx = GridBagConstraints.RELATIVE;
		this.add(this.labelHourMinute, constraints);
		// All listener to change value added component
		((JTextField) this.dataField).getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				HourMinuteDataField.this.updateHourMinuteLabel(e);

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				HourMinuteDataField.this.updateHourMinuteLabel(e);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				HourMinuteDataField.this.updateHourMinuteLabel(e);

			}
		});
	}

	protected void updateHourMinuteLabel(DocumentEvent e) {
		Number value = ((AdvancedIntegerDocument) e.getDocument()).getValue();
		HourMinuteDataField.this.labelHourMinute.setText("(" + HourMinuteDataField.this.formatDuration(value) + ")");
	}

	/**
	 * Format duration.
	 *
	 * @param value
	 *            the value
	 * @return the string
	 */
	protected String formatDuration(Number value) {
		StringBuilder sb = new StringBuilder();
		try {
			long valueN = value.longValue() * 60 * 1000;
			long hours = valueN / HourMinuteDataField.HH;
			valueN -= hours * HourMinuteDataField.HH;
			long minutes = valueN / HourMinuteDataField.MM;
			valueN -= minutes * HourMinuteDataField.MM;
			long seconds = valueN / 1000;
			if (seconds >= 30) {
				minutes += 1;
			}
			if (sb.length() > 0) {
				sb.append(":");
			}
			sb.append(String.format("%02d", hours) + "h");
			if (sb.length() > 0) {
				sb.append(":");
			}
			sb.append(String.format("%02d", minutes) + "m");

			return sb.toString();
		} catch (Exception e) {
			HourMinuteDataField.LOGGER.error(null, e);
			return "Error";
		}
	}

}
