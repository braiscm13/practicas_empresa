package com.opentach.client.comp.field;

import java.awt.Component;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JList;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.ComboDataField;
import com.ontimize.gui.field.CustomComboBoxModel;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.activities.IInfractionService.EngineAnalyzer;

/**
 * The Class InfracEngineChooser.
 */
public class InfracEngineChooser extends ComboDataField {

	/**
	 * Instantiates a new infrac engine chooser.
	 *
	 * @param params
	 *            the params
	 */
	public InfracEngineChooser(Hashtable params) {
		super();
		this.init(params);
		((JComboBox) this.dataField).setModel(new InfracEngineComboBoxModel());
		((JComboBox) this.dataField).setSelectedIndex(0);
		((JComboBox) this.dataField).setRenderer(new InfracEngineComboBoxRenderer());
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.field.DataComponent#getSQLDataType()
	 */
	@Override
	public int getSQLDataType() {
		return 0;
	}

	@Override
	public void deleteData() {
		// do nothing
	}

	@Override
	public void setValue(Object value) {
		if (value == null) {
			return;
		}
		super.setValue(value);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(true);
	}

	/**
	 * The Class InfracEngineComboBoxModel.
	 */
	class InfracEngineComboBoxModel extends CustomComboBoxModel {

		/**
		 * Instantiates a new infrac engine combo box model.
		 */
		public InfracEngineComboBoxModel() {
			super();
			this.setNullSelection(false);
			this.setDataVector(new Vector(Arrays.asList(new Object[] { IInfractionService.EngineAnalyzer.DEFAULT, EngineAnalyzer.ISLANDS })));
		}

	}

	class InfracEngineComboBoxRenderer extends DefaultCustomComboBoxRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
			EngineAnalyzer val = (EngineAnalyzer) value;
			if (val != null) {
				switch (val) {
					case DEFAULT:
						value = "ENGINE_ANALIZER_DEFAULT";
						break;
					case ISLANDS:
						value = "ENGINE_ANALIZER_ISLANDS";
						break;

					default:
						value = "ENGINE_ANALIZER_DEFAULT";
						break;
				}
				value = ApplicationManager.getTranslation((String) value);
			}
			return super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
		}
	}
}
