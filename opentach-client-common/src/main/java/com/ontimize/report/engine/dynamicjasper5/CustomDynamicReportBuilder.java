/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package com.ontimize.report.engine.dynamicjasper5;
import java.util.Iterator;

import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJCrosstab;
import ar.com.fdvs.dj.domain.DJValueFormatter;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.DJGroupVariable;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.GlobalGroupColumn;

public class CustomDynamicReportBuilder extends DynamicReportBuilder {

	public CustomDynamicReportBuilder() {
		super();
	}

	@Override
	public DynamicReport build() {
		this.report.setOptions(this.options);
		if (!this.globalVariablesGroup.getFooterVariables().isEmpty() || !this.globalVariablesGroup.getHeaderVariables().isEmpty()) {
			if (!this.report.getColumnsGroups().contains(this.globalVariablesGroup)) {
				this.report.getColumnsGroups().add(0, this.globalVariablesGroup);
			}
		}

		this.addGlobalCrosstabs();

		this.addSubreportsToGroups();

		this.concatenateReports();

		this.report.setAutoTexts(this.autoTexts);
		return this.report;
	}

	@Override
	public DynamicReportBuilder addGlobalFooterVariable(AbstractColumn col, DJCalculation op, Style style, DJValueFormatter valueFormatter) {
		DJGroupVariable djvariable = new DJGroupVariable(col, op, style, valueFormatter);
		if (!this.globalVariablesGroup.getFooterVariables().contains(djvariable)) {
			this.globalVariablesGroup.addFooterVariable(new DJGroupVariable(col, op, style, valueFormatter));
		}
		return this;
	}

	private void addGlobalCrosstabs() {
		// For header
		if (this.globalHeaderCrosstabs != null) {
			for (Iterator iterator = this.globalHeaderCrosstabs.iterator(); iterator.hasNext();) {
				DJCrosstab djcross = (DJCrosstab) iterator.next();
				DJGroup globalGroup = this.createDummyGroupForCrosstabs("crosstabHeaderGroup-" + this.globalHeaderCrosstabs.indexOf(djcross));
				globalGroup.getHeaderCrosstabs().add(djcross);
				this.report.getColumnsGroups().add(0, globalGroup);
			}
		}

		// For footer
		if (this.globalFooterCrosstabs != null) {
			for (Iterator iterator = this.globalFooterCrosstabs.iterator(); iterator.hasNext();) {
				DJCrosstab djcross = (DJCrosstab) iterator.next();
				DJGroup globalGroup = this.createDummyGroupForCrosstabs("crosstabFooterGroup-" + this.globalFooterCrosstabs.indexOf(djcross));
				globalGroup.getFooterCrosstabs().add(djcross);
				this.report.getColumnsGroups().add(0, globalGroup);
			}
		}

	}

	private DJGroup createDummyGroupForCrosstabs(String name) {
		DJGroup globalGroup = new DJGroup();
		globalGroup.setLayout(GroupLayout.EMPTY);
		GlobalGroupColumn globalCol = new GlobalGroupColumn(name);

		globalCol.setTitle("");
		// globalCol.setHeaderStyle(grandTotalStyle);
		// globalCol.setStyle(grandTotalStyle);

		globalGroup.setColumnToGroupBy(globalCol);
		return globalGroup;
	}

}
