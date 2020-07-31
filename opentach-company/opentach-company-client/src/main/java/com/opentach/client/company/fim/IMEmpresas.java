package com.opentach.client.company.fim;

import java.awt.Container;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.swing.table.JTableHeader;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.company.tools.LicenseClientTools;
import com.opentach.common.company.naming.LicenseNaming;
import com.utilmize.client.fim.advanced.UBasicFIMSearch;
import com.utilmize.client.gui.field.table.GroupableColumnGroup;
import com.utilmize.client.gui.field.table.GroupableTableHeader;

public class IMEmpresas extends UBasicFIMSearch {

	@FormComponent(attr = "COMPANY.LICENSE.LIC_TACHOLABPLUS")
	protected Container tacholabPlusFilters;

	@FormComponent(attr = "EDfEmp")
	protected Table		table;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);

		this.addaptTacholabPlus();
	}

	// Show/Hide TachoLabPlus things only when ENABLED MODULE
	private void addaptTacholabPlus() {
		if (!LicenseClientTools.isTacholabPlusModuleEnabled()) {
			// Filters section
			this.tacholabPlusFilters.setVisible(false);

			// Table columns
			String[] plusCols = new String[] { "LIC_TACHOLABPLUS", "LIC_TACHOLABPLUS_FROM", "LIC_TACHOLABPLUS_TO", //
					"LIC_TACHOLABPLUS_DEMO", "LIC_TACHOLABPLUS_DEMO_FROM", "LIC_TACHOLABPLUS_DEMO_TO" };
			// Tune group headers
			final JTableHeader iheader = this.table.getJTable().getTableHeader();
			if (iheader instanceof GroupableTableHeader) {
				final GroupableTableHeader header = (GroupableTableHeader) iheader;
				Enumeration<GroupableColumnGroup> columnGroups = header.getColumnGroups(this.table.getJTable().getColumn(LicenseNaming.LIC_TACHOLABPLUS));
				while (columnGroups.hasMoreElements()) {
					GroupableColumnGroup group  = columnGroups.nextElement();
					final List<Object> columns = group.getColumns();
					columns.removeAll(Arrays.asList(plusCols));
				}

			}
			// Set as valid columns (table and model)
			this.table.deleteColumn(plusCols);
		}
	}
}
