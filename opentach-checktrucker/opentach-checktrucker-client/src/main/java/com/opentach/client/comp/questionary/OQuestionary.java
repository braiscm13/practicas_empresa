package com.opentach.client.comp.questionary;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeDataComponent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.DataField;
import com.ontimize.util.ParseUtils;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.client.comp.questionary.QuestionaryModelEvent.QuestionaryModelEventType;
import com.opentach.common.surveys.ISurveyService;
import com.opentach.common.surveys.QuestionaryTO;
import com.utilmize.client.fim.UBasicFIM;

public class OQuestionary extends Questionary implements AccessForm, ValueChangeDataComponent {

	/** The attr. */
	private String							attr;
	private String							idWorkUnit;
	private Form							parentForm;
	private QuestionaryTO					questionaryTO;
	private final List<ValueChangeListener>	listeners;
	private boolean							isEstablshingFormValues;

	private final static Logger				logger	= LoggerFactory.getLogger(OQuestionary.class);

	public OQuestionary(Hashtable<?, ?> parameters) throws Exception {
		super();
		this.init(parameters);
		this.listeners = new ArrayList<>();
	}

	@Override
	public void setComponentLocale(Locale l) {}

	@Override
	public void setResourceBundle(ResourceBundle resourceBundle) {}

	@Override
	public Vector getTextsToTranslate() {
		return null;
	}

	@Override
	public void init(Hashtable parameters) throws Exception {
		this.attr = ParseUtils.getString((String) parameters.get("attr"), null);
		this.idWorkUnit = ParseUtils.getString((String) parameters.get("parentkey"), null);
		this.numRows = ParseUtils.getInteger((String) parameters.get("rows"), 3);
		this.disableChecks = ParseUtils.getBoolean((String) parameters.get("disableChecks"), true);
		this.isEstablshingFormValues = ParseUtils.getBoolean((String) parameters.get("isEstablshingFormValues"), true);
		this.setEditable(ParseUtils.getBoolean((String) parameters.get("editable"), false));
	}

	@Override
	public Object getConstraints(LayoutManager parentLayout) {
		return new GridBagConstraints(-1, -1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
	}

	@Override
	public void initPermissions() {}

	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public Object getAttribute() {
		return this.attr;
	}

	private void onParentKeyChange(Object oldValue, Object newValue) {
		if (this.isEstablshingFormValues) {
			if (!((UBasicFIM) this.parentForm.getInteractionManager()).isEstablishingFormValues()) {
				this.parentKeyChange(oldValue, newValue);
			}
		} else {
			this.parentKeyChange(oldValue, newValue);
		}
	}

	private void parentKeyChange(Object oldValue, Object newValue) {
		try {
			if ((newValue != null)) {
				AbstractOpentachClientLocator locator = (AbstractOpentachClientLocator) ApplicationManager.getApplication().getReferenceLocator();
				ISurveyService eSurveyService = locator.getRemoteService(ISurveyService.class);
				this.questionaryTO = eSurveyService.getSurvey(Integer.parseInt(newValue.toString()));
				this.getModel().removeQuestions();
				this.getModel().addQuestions(this.questionaryTO.getQuestions());
				this.getModel().getQuestionaryTO().setId(this.questionaryTO.getId());
			} else if (oldValue != null) {
				this.getModel().removeQuestions();
			}
		} catch (Exception e) {
			OQuestionary.logger.error(null, e);
		}
	}

	@Override
	public void setParentForm(Form form) {
		this.parentForm = form;
		DataField dataField = (DataField) this.parentForm.getElementReference(this.idWorkUnit);
		dataField.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				OQuestionary.this.onParentKeyChange(e.getOldValue(), e.getNewValue());
			}
		});
	}

	@Override
	public String getLabelComponentText() {
		return null;
	}

	@Override
	public Object getValue() {
		if (this.isEmpty() || (this.getModel() == null)) {
			return null;
		}
		return this.getModel().getQuestionaryTO();
	}

	@Override
	public void setValue(Object value) {
		this.getModel().removeQuestions();
		this.getModel().addQuestions(((QuestionaryTO) value).getQuestions());
		this.getModel().getQuestionaryTO().setId(((QuestionaryTO) value).getId());
	}

	@Override
	public void deleteData() {}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isModifiable() {
		return true;
	}

	@Override
	public void setModifiable(boolean modifiable) {}

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
		return true;
	}

	@Override
	public void setRequired(boolean required) {}

	@Override
	public void addValueChangeListener(ValueChangeListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeValueChangeListener(ValueChangeListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Fire question model change.
	 *
	 * @param event
	 *            the event
	 */
	protected void fireQuestionaryChange(ValueEvent event) {
		for (ValueChangeListener listener : this.listeners) {
			listener.valueChanged(event);
		}
	}

	@Override
	public void modelQuestionChanged(QuestionModelEvent event) {
		this.fireQuestionaryChange(new ValueEvent(this, this.getValue(), null, ValueEvent.PROGRAMMATIC_CHANGE));
		super.getModel().fireQuestionaryModelChange(new QuestionaryModelEvent(this.getModel(), QuestionaryModelEventType.NULL, 0));
	}

}
