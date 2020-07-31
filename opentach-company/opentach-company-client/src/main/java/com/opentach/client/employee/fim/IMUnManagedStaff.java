package com.opentach.client.employee.fim;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.table.Table;
import com.opentach.common.labor.IUnManagedStaff;

public class IMUnManagedStaff extends BasicInteractionManager {
	@FormComponent(attr = IUnManagedStaff.ATTR.ENTITY)
	private Table unManagedStaffTable;

	public IMUnManagedStaff() {
		super();
	}

	@Override
	public void setInsertMode() {
		super.setUpdateMode();
		this.unManagedStaffTable.refreshInThread(0);
	}
}
