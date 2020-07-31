package com.opentach.adminclient.modules.cdo;

import java.awt.Component;
import java.awt.LayoutManager;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.CellRenderer;
import com.utilmize.client.gui.field.table.render.IXmlTableCellRenderer;

public class MonitorCellRenderer extends CellRenderer implements FormComponent, IXmlTableCellRenderer {


	private static final String		IMAGE_YES		= "images-admin/link_24.png";
	private static final String		IMAGE_NO		= "images-admin/link_broken_24.png";
	private static final ImageIcon	selectIcon;
	private static final ImageIcon	deselectIcon;

	/** The column name. */
	protected String				columnName;

	/** The global id. */
	protected Object				globalId;
	static {
		selectIcon = ImageManager.getIcon(MonitorCellRenderer.IMAGE_YES);
		deselectIcon = ImageManager.getIcon(MonitorCellRenderer.IMAGE_NO);
	}

	public MonitorCellRenderer(Hashtable params) throws Exception {
		super();
		this.init(params);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean seleccionado, boolean tieneFoco, int row, int columna) {
		Component c = super.getTableCellRendererComponent(table, value, seleccionado, tieneFoco, row, columna);
		if (c instanceof JLabel) {
			JLabel label = (JLabel) c;
			Boolean ok = (Boolean) value;
			label.setIcon(ok == null ? null : (ok ? MonitorCellRenderer.selectIcon : MonitorCellRenderer.deselectIcon));
			label.setHorizontalAlignment(0);
		}
		return c;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.field.FormComponent#getConstraints(java.awt.LayoutManager)
	 */
	@Override
	public Object getConstraints(LayoutManager layoutmanager) {
		return this.columnName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.utilmize.client.gui.field.table.render.IXmlTableCellRenderer#getIdentifier()
	 */
	@Override
	public Object getIdentifier() {
		return this.globalId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.field.FormComponent#init(java.util.Hashtable)
	 */
	@Override
	public void init(Hashtable params) throws Exception {
		this.columnName = (String) params.get("column");
		this.globalId = params.get("globalid");
	}

	@Override
	public void setComponentLocale(Locale l) {

	}

	@Override
	public void setResourceBundle(ResourceBundle resourceBundle) {}

	@Override
	public Vector getTextsToTranslate() {
		return null;
	}

}
