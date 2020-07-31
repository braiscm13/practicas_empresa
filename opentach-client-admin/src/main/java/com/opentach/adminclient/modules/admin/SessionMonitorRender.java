package com.opentach.adminclient.modules.admin;

import java.awt.Component;
import java.awt.LayoutManager;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.table.CellRenderer;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.opentach.common.sessionstatus.AbstractStatusDto;
import com.opentach.common.sessionstatus.AdminClientStatusDto;
import com.opentach.common.sessionstatus.ClientStatusDto;
import com.opentach.common.sessionstatus.DownCenterStatusDto;
import com.utilmize.client.gui.field.table.render.IXmlTableCellRenderer;
import com.utilmize.client.gui.field.table.render.UXmlDateCellRenderer;
import com.utilmize.client.gui.field.table.render.UXmlDurationCellRenderer;
import com.utilmize.client.gui.field.table.render.UXmlNumberCellRenderer;
import com.utilmize.client.gui.field.table.render.UXmlTextCellRenderer;

public class SessionMonitorRender extends CellRenderer implements FormComponent, IXmlTableCellRenderer {

	private static final Logger			logger	= LoggerFactory.getLogger(SessionMonitorRender.class);
	/** The column name. */
	protected String					columnName;

	/** The global id. */
	protected Object					globalId;
	private UXmlTextCellRenderer		renderText;
	private UXmlDurationCellRenderer	renderDuration;
	private UXmlDateCellRenderer		renderDate;
	private UXmlNumberCellRenderer		renderInteger;

	/**
	 * Instantiates a new u xml text cell renderer.
	 *
	 * @param params
	 *            the params
	 * @throws Exception
	 *             the exception
	 */
	public SessionMonitorRender(Hashtable params) throws Exception {
		super();
		this.init(params);
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
		this.renderText = new UXmlTextCellRenderer(EntityResultTools.keysvalues());
		this.renderDuration = new UXmlDurationCellRenderer(EntityResultTools.keysvalues());
		this.renderDate = new UXmlDateCellRenderer(EntityResultTools.keysvalues("format", "dd/MM/yyyy HH:mm:ss"));
		this.renderInteger = new UXmlNumberCellRenderer(EntityResultTools.keysvalues("format", "#0"));
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.i18n.Internationalization#getTextsToTranslate()
	 */
	@Override
	public Vector getTextsToTranslate() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.i18n.Internationalization#setComponentLocale(java.util.Locale)
	 */
	@Override
	public void setComponentLocale(Locale locale) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.i18n.Internationalization#setResourceBundle(java.util.ResourceBundle)
	 */
	@Override
	public void setResourceBundle(ResourceBundle resourcebundle) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.gui.table.CellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
		AbstractStatusDto dto = this.getStatusDto(table, row);
		String columnName = table.getColumnName(column);
		Object fieldValue = (ObjectTools.isIn(columnName, "connectedTime", "app", "connected") || (dto == null)) ? null : ReflectionTools.getFieldValue(dto, columnName);

		try {
			if ("connectedTime".equals(columnName)) {
				if ((dto != null) && (dto.getStartupTime() != null)) {
					long time = System.currentTimeMillis() - dto.getStartupTime().getTime();
					return this.renderDuration.getTableCellRendererComponent(table, time, selected, hasFocus, row, column);
				}
			} else if ("app".equals(columnName)) {
				fieldValue = "";
				if (dto instanceof AdminClientStatusDto) {
					fieldValue = "Administrador";
				} else if (dto instanceof DownCenterStatusDto) {
					fieldValue = "Puesto de descarga";
				} else if (dto instanceof ClientStatusDto) {
					fieldValue = "Cliente";
				}
			} else if (fieldValue instanceof Date) {
				return this.renderDate.getTableCellRendererComponent(table, fieldValue, selected, hasFocus, row, column);
			} else if (fieldValue instanceof Integer) {
				return this.renderInteger.getTableCellRendererComponent(table, fieldValue, selected, hasFocus, row, column);
			}
			return this.renderText.getTableCellRendererComponent(table, fieldValue, selected, hasFocus, row, column);
		} catch (Exception ex) {
			SessionMonitorRender.logger.error(null, ex);
			return this;
		}
	}

	protected AbstractStatusDto getStatusDto(JTable table, int row) {
		int columnIndex = table.getColumnModel().getColumnIndex("STATUS");
		return (AbstractStatusDto) table.getValueAt(row, columnIndex);
	}

}
