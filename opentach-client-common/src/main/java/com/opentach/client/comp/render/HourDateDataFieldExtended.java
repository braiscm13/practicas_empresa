package com.opentach.client.comp.render;

	import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.document.DateDocument;
import com.ontimize.gui.field.document.HourDocument;

	/**
	 * This class implements a data field with date and hours. Moreover, it is possible to show an associated calendar with this field.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	public class HourDateDataFieldExtended extends com.ontimize.gui.field.HourDateDataField {

		private static final Logger logger = LoggerFactory.getLogger(HourDateDataFieldExtended.class);

	
		public HourDateDataFieldExtended(Hashtable parameters) {
			super(parameters);
			if (!this.isEnabled) {
				this.setEnabled(false);
			}
			// if (!this.isEnabled) {
			// this.hourField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			// }
			HourDocument doc = new HourDocument();
			this.hourField.setDocument(doc);
			doc.addDocumentListener(this.innerListener);
			this.hourField.setToolTipText(((HourDocument) this.hourField.getDocument()).getSampleHour());
			this.remove(this.dataField);
			GridBagConstraints constraints = ((GridBagLayout) this.getLayout()).getConstraints(this.dataField);
			constraints.gridx = GridBagConstraints.RELATIVE;
			this.add(this.dataField, constraints);
			
			constraints = ((GridBagLayout) this.getLayout()).getConstraints(this.dataField);
			this.add(this.hourField, constraints);
			
			if (this.calendarButton != null) {
				GridBagConstraints buttonConstraints = ((GridBagLayout) this.getLayout()).getConstraints(this.calendarButton);
				this.remove(this.calendarButton);
				this.add(this.calendarButton, buttonConstraints);
			}
			this.hourField.addFocusListener(new FocusAdapter() {

				@Override
				public void focusLost(FocusEvent e) {
					if (!e.isTemporary()) {
						HourDateDataFieldExtended.this.formatHour();
						DateDocument dF = (DateDocument) ((JTextField) HourDateDataFieldExtended.this.dataField).getDocument();
						if ((dF.getLength() == 0) && (((HourDocument) HourDateDataFieldExtended.this.hourField.getDocument()).getLength() > 0)) {
							if (HourDateDataFieldExtended.this.dateAttribute != null) {
								Object oDate = HourDateDataFieldExtended.this.parentForm.getDataFieldValue(HourDateDataFieldExtended.this.dateAttribute);
								if ((oDate == null) || (oDate instanceof Date)) {
									dF.setValue((Date) oDate);
								}
							} else {
								dF.setValue(new Date());
							}
							((JTextField) HourDateDataFieldExtended.this.dataField).selectAll();
						}
					}
				}
			});

			this.dataField.addFocusListener(new FocusAdapter() {

				@Override
				public void focusLost(FocusEvent e) {
					if (!e.isTemporary()) {
						HourDateDataFieldExtended.this.format();
						HourDocument dH = (HourDocument) HourDateDataFieldExtended.this.hourField.getDocument();
						DateDocument dF = (DateDocument) ((JTextField) HourDateDataFieldExtended.this.dataField).getDocument();
						if ((dH.getLength() == 0) && (dF.getLength() > 0) && dF.isValid()) {
							try {
								String stringHour = dH.getFormat().format(dF.getDate());
								dH.insertString(0, stringHour, null);
							} catch (BadLocationException ex) {
								HourDateDataFieldExtended.logger.error(null, ex);
							}
						}
					}
				}
			});
			Object dv = parameters.get("datevisible");
			if (dv != null) {
				if (dv.equals("no")) {
					this.dateVisible = false;
					this.dataField.setVisible(false);
					if (this.calendarButton != null) {
						this.calendarButton.setVisible(false);
					}
				}
			}
			Object date = parameters.get("date");
			if (date != null) {
				this.dateAttribute = date.toString();
			}

			this.hourField.addKeyListener(new KeyListenerSetDate());
		}
		
		protected class KeyListenerSetDate implements KeyListener {

			@Override
			public void keyPressed(KeyEvent e) {
				// Do nothing
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// Do nothing
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if ((e.getKeyChar() == 'h') || (e.getKeyChar() == 'H') || (e.getKeyChar() == 't') || (e.getKeyChar() == 'T')) {
					HourDateDataFieldExtended.this.setCurrentDate();
				}
			}
		}
}
