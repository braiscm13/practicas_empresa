package com.opentach.client.modules.data;

import java.util.Hashtable;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.modules.OpentachBasicInteractionManager;
import com.utilmize.client.gui.AbstractValueChangeListener;
import com.utilmize.client.gui.buttons.IUFormComponent;

public class IMDriverContracts extends OpentachBasicInteractionManager {

	private static final Logger logger = LoggerFactory.getLogger(IMDriverContracts.class);

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.managedForm.setCheckModifiedDataField("MARK_CURRENT_CONTRACT", false);
		this.setValueChangedEventListener("MARK_CURRENT_CONTRACT", false);

	}

	public static class IMDriverContractsTypeValueChangeListener extends AbstractValueChangeListener {
		private static String[]	ATTRS_FULLTIME		= new String[] {};
		private static String[]	ATTRS_PARTIALTIME	= new String[] { "DRC_PARTIAL_DAILY_MINUTES", "DRC_PARTIAL_WEEKLY_MINUTES", "DRC_PARTIAL_4WEEKLY_MINUTES","DRC_PARTIAL_BIWEEKLY_MINUTES", "DRC_PARTIAL_MONTHLY_MINUTES", "DRC_PARTIAL_4MONTHLY_MINUTES", "DRC_PARTIAL_ANNUAL_MINUTES", "DRC_COMPLEMENTARY_MINUTES", "DRC_COMPL_DAILY_MINUTES", "DRC_COMPL_WEEKLY_MINUTES", "DRC_COMPL_BIWEEKLY_MINUTES", "DRC_COMPL_4WEEKLY_MINUTES", "DRC_COMPL_MONTHLY_MINUTES", "DRC_COMPL_4MONTHLY_MINUTES", "DRC_COMPL_ANNUAL_MINUTES", "LABEL_WORKING", "LABEL_COMPL" };

		public IMDriverContractsTypeValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
			super(formComponent, params);
		}

		@Override
		public void valueChanged(ValueEvent e) {
			if (e.getNewValue() == null) {
				this.getUBasicInteractionManager().setDataFieldsVisible(false, IMDriverContractsTypeValueChangeListener.ATTRS_PARTIALTIME);
				this.getUBasicInteractionManager().setDataFieldsVisible(false, IMDriverContractsTypeValueChangeListener.ATTRS_FULLTIME);
				// this.getUBasicInteractionManager().setDataFieldsRequire(true, IMDriverContractsTypeValueChangeListener.ATTRS_PARTIALTIME);
				// this.getUBasicInteractionManager().setDataFieldsRequire(true, IMDriverContractsTypeValueChangeListener.ATTRS_FULLTIME);
			} else {
				this.getUBasicInteractionManager().setDataFieldsVisible("TIEMPO_PARCIAL".equals(e.getNewValue()), IMDriverContractsTypeValueChangeListener.ATTRS_PARTIALTIME);
				this.getUBasicInteractionManager().setDataFieldsVisible(!"TIEMPO_PARCIAL".equals(e.getNewValue()), IMDriverContractsTypeValueChangeListener.ATTRS_FULLTIME);
				// this.getUBasicInteractionManager().setDataFieldsRequire("TIEMPO_PARCIAL".equals(e.getNewValue()), IMDriverContractsTypeValueChangeListener.ATTRS_PARTIALTIME);
				// this.getUBasicInteractionManager().setDataFieldsRequire(!"TIEMPO_PARCIAL".equals(e.getNewValue()), IMDriverContractsTypeValueChangeListener.ATTRS_FULLTIME);
			}
		}
	}

	public static class IMDriverContractsCheckCurrentContractValueChangeListener extends AbstractValueChangeListener {
		@FormComponent(attr = "MARK_CURRENT_CONTRACT")
		private CheckDataField	checkCurrentContract;

		@FormComponent(attr = "DRC_DATE_TO")
		private DateDataField	drcDateTo;

		public IMDriverContractsCheckCurrentContractValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
			super(formComponent, params);
		}

		@Override
		public void valueChanged(ValueEvent e) {
			if (e.getType() == ValueEvent.USER_CHANGE) {
				if (this.checkCurrentContract.isSelected()) {
					if (this.drcDateTo.getValue() != null) {
						this.drcDateTo.setValue(null);
					}
					this.drcDateTo.setEnabled(false);
				} else {
					this.drcDateTo.setEnabled(true);
				}
			}
		}
	}

	public static class IMDriverContractsDateToValueChangeListener extends AbstractValueChangeListener {
		@FormComponent(attr = "MARK_CURRENT_CONTRACT")
		private CheckDataField	checkCurrentContract;

		@FormComponent(attr = "DRC_DATE_TO")
		private DateDataField	drcDateTo;

		public IMDriverContractsDateToValueChangeListener(IUFormComponent formComponent, Hashtable params) throws Exception {
			super(formComponent, params);
		}

		@Override
		public void valueChanged(ValueEvent e) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (IMDriverContractsDateToValueChangeListener.this.drcDateTo.getValue() != null) {
						IMDriverContractsDateToValueChangeListener.this.checkCurrentContract.setValue(Boolean.FALSE);
						IMDriverContractsDateToValueChangeListener.this.drcDateTo.setEnabled(true);
					} else {
						IMDriverContractsDateToValueChangeListener.this.checkCurrentContract.setValue(Boolean.TRUE);
						IMDriverContractsDateToValueChangeListener.this.drcDateTo.setEnabled(false);
					}
				}
			});
		}

	}
}
