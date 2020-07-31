package com.opentach.client.tasks.components.journal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.xml.DefaultXMLParametersManager;
import com.utilmize.client.gui.Column;
import com.utilmize.client.gui.Row;
import com.utilmize.client.gui.field.ULabel;
import com.utilmize.client.gui.field.UMemoDataField;

public class JournalEntryComponent extends Row implements DataComponent {

	/** The constant logger. */
	private final static Logger	logger		= LoggerFactory.getLogger(JournalEntryComponent.class);

	private static final SimpleDateFormat	DATE_FORMAT	= new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat	HOUR_FORMAT	= new SimpleDateFormat("HH:mm");

	protected Object			value;

	// Panel descriptors --------------------------------
	protected Row				mainPanel;
	protected Column			mainPanelInner;
	protected ULabel			titleLabel;
	protected UMemoDataField	descLabel;

	protected JournalEntryComponent(Hashtable parameters) {
		this((Object) parameters);
	}

	public JournalEntryComponent(Object value) {
		super(JournalEntryComponent.composeParams(value));
		JournalEntryComponent.this.setValue(value);
	}

	private static Hashtable composeParams(Object value) {
		Hashtable<Object, Object> params = new Hashtable<Object, Object>();
		params.put("expand", "no");
		params.put("attr", System.currentTimeMillis());
		return params;
	}

	@Override
	public void init(Hashtable parameters) {
		super.init(parameters);

		this.installComponents();
	}

	private void installComponents() {
		this.setLayout(new GridBagLayout());
		this.add(this.createMainPanel(), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}

	private Component createMainPanel() {
		this.mainPanel = this.createRow(EntityResultTools.keysvalues("expand", "yes"));
		this.mainPanel.setLayout(new BorderLayout());

		this.mainPanelInner = this.createColumn(EntityResultTools.keysvalues("expand", "yes", "expandlast", "yes"));
		this.mainPanelInner.setLayout(new GridBagLayout());

		// First column -----------------------------------------
		this.titleLabel = this.createObject(ULabel.class, EntityResultTools.keysvalues("attr", "JournalEntry.title", "dim", "text", "labelvisible", "no"));
		this.mainPanelInner.add(this.titleLabel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		this.descLabel = this.createObject(UMemoDataField.class,
				EntityResultTools.keysvalues("attr", "JournalEntry.desc", "dim", "text", "labelvisible", "no", "autoadjustsize", "yes"));
		((JTextArea) this.descLabel.getDataField()).setFocusable(false);
		this.mainPanelInner.add(this.descLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 0), 0, 0));

		JScrollPane scroll = (JScrollPane) ReflectionTools.getFieldValue(this.descLabel, "scroll");
		scroll.setWheelScrollingEnabled(false);

		this.mainPanel.add(this.mainPanelInner, BorderLayout.CENTER);
		return this.mainPanel;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //// Builder UI utilities ////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private Column createColumn(Hashtable<Object, Object> hashtable) {
		return this.createObject(Column.class, hashtable);
	}

	private Row createRow(Hashtable<Object, Object> hashtable) {
		return this.createObject(Row.class, hashtable);
	}

	private <T> T createObject(Class<? extends T> objectClass, Hashtable<Object, Object> hashtable) {
		Hashtable<Object, Object> parameters = DefaultXMLParametersManager.getParameters(objectClass.getName());
		if (hashtable != null) {
			parameters.putAll(hashtable);
		}
		try {
			return objectClass.getDeclaredConstructor(Hashtable.class).newInstance(parameters);
		} catch (Exception e) {
			JournalEntryComponent.logger.error(null, e);
			return null;
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //// Inner Methods ///////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void setResourceBundle(ResourceBundle resources) {
		super.setResourceBundle(resources);

		this.titleLabel.setResourceBundle(resources);
		this.descLabel.setResourceBundle(resources);
	}

	@Override
	public void setParentForm(Form f) {
		super.setParentForm(f);

		this.titleLabel.setParentForm(f);
		this.descLabel.setParentForm(f);
	}

	protected Form getParentForm() {
		return this.parentForm;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //// DataComponent Methods //////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getLabelComponentText() {
		return null;
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
		this.decorateFromValue();
	}

	private void decorateFromValue() {
		// Sets values to subcomponents based on this.value
		Map<String, Object> values = (Map<String, Object>) this.value;

		String user = (String) ObjectTools.coalesce(values.get("USUARIO"), "-");
		String date = "-";
		String hour = "-";
		Date oDate = (Date) values.get("TKJ_DATE");
		if (oDate != null) {
			date = JournalEntryComponent.DATE_FORMAT.format(oDate);
			hour = JournalEntryComponent.HOUR_FORMAT.format(oDate);
		}

		String title = ApplicationManager.getTranslation("tsk.JOURNAL_ENTRY_COMPONENT.TITLE", ApplicationManager.getApplicationBundle(), new Object[] { user, date, hour });
		this.titleLabel.setText(title);

		this.descLabel.setValue(ObjectTools.coalesce(values.get("TKJ_ENTRY"), ""));
	}

	@Override
	public void deleteData() {
		this.setValue(null);
	}

	@Override
	public boolean isEmpty() {
		return this.value == null;
	}

	@Override
	public boolean isModifiable() {
		return true;
	}

	@Override
	public void setModifiable(boolean modifiable) {
		// Do nothing
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public int getSQLDataType() {
		return 0;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public void setRequired(boolean required) {
		// Do nothing
	}
}
