package com.opentach.client.comp;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.document.NIFDocument;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.StringTools;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

public class BotonLetraNIF extends UButton {

	private static Hashtable completeParameters(Hashtable parametros) {
		MapTools.safePut(parametros, "listener", NifLetterActionListener.class.getName());
		MapTools.safePut(parametros, "name", "FieldButton");

		MapTools.safePut(parametros, AbstractActionListenerButton.PARAM_ENABLE_FIM_UPDATE, "yes", true);
		MapTools.safePut(parametros, AbstractActionListenerButton.PARAM_ENABLE_FIM_INSERT, "yes", true);

		return parametros;
	}

	public BotonLetraNIF(Hashtable parametros) {
		super(BotonLetraNIF.completeParameters(parametros));
		this.setMargin(new Insets(0, 0, 0, 0));
	}

	public static class NifLetterActionListener extends AbstractActionListenerButton {
		/** The CONSTANT logger */
		private static final Logger	logger	= LoggerFactory.getLogger(BotonLetraNIF.NifLetterActionListener.class);

		protected String			attrCampoNIF;

		public NifLetterActionListener(UButton button, Hashtable params) throws Exception {
			super(button, button, params);
		}

		@Override
		protected void init(Map<?, ?> params) throws Exception {
			super.init(params);

			Object campo = params.get("nif");
			if (campo == null) {
				throw new IllegalArgumentException(this.getClass().toString() + ": El atributo 'nif' es obligatorio");
			}
			this.attrCampoNIF = campo.toString();

			((DataField) this.getForm().getDataFieldReference(this.attrCampoNIF)).addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChanged(ValueEvent e) {
					NifLetterActionListener.this.considerToEnableButton();
				}
			});
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Object nifValue = this.getForm().getDataFieldValue(this.attrCampoNIF);
				boolean nifEmpty = !(nifValue instanceof String) || StringTools.isEmpty((String) nifValue);
				if (nifEmpty) {
					return;
				}

				String v = (String) nifValue;
				if (v.length() == 8) {
					char letra = NIFDocument.calculateLetter(v);
					this.getForm().setDataFieldValue(this.attrCampoNIF, v + letra);
				} else if ((v.length() < 8) && (v.length() > 0)) {
					char letra = NIFDocument.calculateLetter(v);
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < (8 - v.length()); i++) {
						sb.append('0');
					}
					this.getForm().setDataFieldValue(this.attrCampoNIF, sb.toString() + v + letra);
				} else if (v.length() == 9) {
					char letra = NIFDocument.calculateLetter(v.substring(0, 8));
					char letraPuesta = v.charAt(8);
					if (letra != letraPuesta) {
						this.getForm().setDataFieldValue(this.attrCampoNIF, v.substring(0, 8) + letra);
					}
				}
			} catch (Exception ex) {
				NifLetterActionListener.logger.debug(null, ex);
			}
		}

		@Override
		protected boolean getEnableValueToSet() {
			Object nifValue = this.getForm().getDataFieldValue(this.attrCampoNIF);
			boolean nifnotEmpty = (nifValue instanceof String) && !StringTools.isEmpty((String) nifValue);
			return super.getEnableValueToSet() && nifnotEmpty;
		}
	}

}
