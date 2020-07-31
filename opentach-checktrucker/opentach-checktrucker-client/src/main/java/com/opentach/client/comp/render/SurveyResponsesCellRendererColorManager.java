package com.opentach.client.comp.render;

import java.awt.Color;

import javax.swing.JTable;

import com.ontimize.gui.table.CellRenderer.CellRendererColorManager;

public class SurveyResponsesCellRendererColorManager implements CellRendererColorManager {
	protected static final Color	COLOR_CORRECT		= Color.decode("#43b01c");
	protected static final Color	COLOR_WRONG			= Color.decode("#e15353");
	protected static final Color	COLOR_FONT_CORRECT	= Color.decode("#ffffff");
	protected static final Color	COLOR_FONT_WRONG	= Color.decode("#ffffff");

	public SurveyResponsesCellRendererColorManager() {
		super();
	}

	@Override
	public Color getForeground(JTable t, int row, int col, boolean sel) {
		Number correctOption = this.getCorrectOption(t, row);
		Number responseOption = this.getResponseOption(t, row);
		if (correctOption.equals(responseOption)) {
			return SurveyResponsesCellRendererColorManager.COLOR_FONT_CORRECT;
		} else {
			return SurveyResponsesCellRendererColorManager.COLOR_FONT_WRONG;
		}
	}

	@Override
	public Color getBackground(JTable t, int row, int col, boolean sel) {
		Number correctOption = this.getCorrectOption(t, row);
		Number responseOption = this.getResponseOption(t, row);
		if (correctOption.equals(responseOption)) {
			return SurveyResponsesCellRendererColorManager.COLOR_CORRECT;
		} else {
			return SurveyResponsesCellRendererColorManager.COLOR_WRONG;
		}
	}

	private Number getResponseOption(JTable t, int row) {
		return (Number) t.getModel().getValueAt(row, t.getColumn("ID_OPTION").getModelIndex());
	}

	private Number getCorrectOption(JTable t, int row) {
		return (Number) t.getModel().getValueAt(row, t.getColumn("CORRECT_ID_OPTION").getModelIndex());
	}
}
