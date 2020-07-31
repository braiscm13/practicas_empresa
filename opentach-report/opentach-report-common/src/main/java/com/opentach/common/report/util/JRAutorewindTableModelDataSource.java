package com.opentach.common.report.util;

import javax.swing.table.TableModel;

import net.sf.jasperreports.engine.data.JRTableModelDataSource;

public class JRAutorewindTableModelDataSource extends JRTableModelDataSource {

	public JRAutorewindTableModelDataSource(TableModel model) {
		super(model);
	}

	@Override
	public boolean next() {
		boolean next = super.next();
		if (!next) {
			this.moveFirst();
		}
		return next;
	}

}
