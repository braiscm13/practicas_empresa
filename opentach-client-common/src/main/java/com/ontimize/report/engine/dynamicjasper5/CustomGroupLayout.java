/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
package com.ontimize.report.engine.dynamicjasper5;
import ar.com.fdvs.dj.domain.constants.GroupLayout;

public class CustomGroupLayout extends GroupLayout {

	public static GroupLayout JUST_HEADERS = new GroupLayout(false, false, false, false, true);

	public static GroupLayout VALUE_IN_HEADER_WITHOUT_HEADERS_AND_COLUMN_NAME = new GroupLayout(true, true, true, true, false);

	public static GroupLayout VALUE_IN_HEADER_WITH_HEADERS_AND_COLUMN_NAME_PRINT_EACH = new GroupLayout(true, true, true, true, true);

	public CustomGroupLayout(boolean showValueInHeader, boolean showValueForEach, boolean showColumnName, boolean hideColumn, boolean printHeaders) {
		super(showValueInHeader, showValueForEach, showColumnName, hideColumn, printHeaders);
	}

}
