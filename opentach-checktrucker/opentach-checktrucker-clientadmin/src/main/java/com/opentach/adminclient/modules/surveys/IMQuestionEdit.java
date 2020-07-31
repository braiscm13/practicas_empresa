package com.opentach.adminclient.modules.surveys;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.ontimize.annotation.FormComponent;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.opentach.client.comp.questionary.Question;
import com.opentach.client.comp.questionary.QuestionModel;
import com.opentach.common.surveys.QuestionTO;
import com.utilmize.client.fim.UBasicFIM;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

/**
 * The Class IMQuestionEditay.
 */
public class IMQuestionEdit extends UBasicFIM {

	private QuestionModel		questionModel;
	@FormComponent(attr = "dummy")
	private Table				tableQuestion;
	@FormComponent(attr = "QUEST")
	private Question			question;
	@FormComponent(attr = "QUEST_TITLE")
	private TextDataField		title;
	@FormComponent(attr = "ID_QUESTION_TYPE_1")
	private UReferenceDataField	idClass;
	private TableModel			tableModel;
	private boolean				init	= true;
	private String[]			titleComplete;

	@FormComponent(attr = "OPTION_CORRECT")
	private TextComboDataField	optionCorrectCombo;
	private IMSurvey			parentImSurvey;

	public IMQuestionEdit() {
		super();
	}

	public void setQuestionTO(QuestionTO questionTO) {
		this.init = true;
		this.titleComplete = questionTO.getTitle().split("\\. ");// Number + Title
		this.questionModel = new QuestionModel(questionTO);
		this.question.setModel(this.questionModel);

		this.idClass.invalidateCache();
		Object id = this.questionModel.getIdType();
		this.idClass.setValue(id instanceof Integer ? new BigDecimal((Integer) id) : id);

		this.title.setValue(this.titleComplete[1]);

		Hashtable<Object, Object> values = new Hashtable<Object, Object>();
		this.tableQuestion.deleteData();
		for (String option : this.questionModel.getOptions()) {
			values.put("POSITION", this.questionModel.getOptions().indexOf(option));
			values.put("OPTION_TITLE", option);
			this.tableQuestion.addRow(values);
		}
		this.refreshOptionCorrect();
		this.init = false;
	}

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);

		this.idClass.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (!IMQuestionEdit.this.init) {
					Object newValue = e.getNewValue();
					if (newValue instanceof SearchValue) {
						newValue = ((List<?>) ((SearchValue) newValue).getValue()).get(0);
					}
					if (newValue != null) {
						Integer intId = Integer.valueOf(String.valueOf(newValue));
						IMQuestionEdit.this.questionModel.setIdClass(intId);
					}
				}
			}
		});

		this.title.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (!IMQuestionEdit.this.init) {
					String textTitle = IMQuestionEdit.this.title.getText();
					IMQuestionEdit.this.questionModel.setTitle(IMQuestionEdit.this.titleComplete[0] + ". " + textTitle);
				}
			}
		});
		this.tableModel = this.tableQuestion.getJTable().getModel();
		this.tableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent event) {
				if (!IMQuestionEdit.this.init) {
					switch (event.getType()) {
						case TableModelEvent.INSERT:
							IMQuestionEdit.this.insertOption(event);
							break;
						case TableModelEvent.UPDATE:
							// if (event.getLastRow() == Integer.MAX_VALUE) {
							// IMQuestionEdit.this.deleteOptions(IMQuestionEdit.this.tableQuestion.getSelectedRows());
							// break;
							// }
							IMQuestionEdit.this.updateOption(event);
							break;
						case TableModelEvent.DELETE:
							IMQuestionEdit.this.deleteOption(event);
							break;
						default:
							break;
					}
					IMQuestionEdit.this.refreshOptionCorrect();
				}
			}

		});

		this.optionCorrectCombo.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChanged(ValueEvent e) {
				String correctOption = (String) e.getNewValue();
				IMQuestionEdit.this.questionModel.setCorrectOption(correctOption);
			}
		});
	}

	private void refreshOptionCorrect() {
		Vector<String> options = new Vector<String>();

		if (!IMQuestionEdit.this.tableQuestion.isEmpty()) {
			EntityResult res = new EntityResult((Hashtable<String, Object>) IMQuestionEdit.this.tableQuestion.getValue());
			Vector<String> optionsTitle = (Vector<String>) res.get("OPTION_TITLE");
			for (int count = 0; count < optionsTitle.size(); count++) {
				String option = optionsTitle.get(count);
				options.add(option);
			}
			Object valueSelected = IMQuestionEdit.this.optionCorrectCombo.getValue();
			IMQuestionEdit.this.optionCorrectCombo.setValues(options);
			if ((valueSelected != null) && options.contains(valueSelected)) {
				IMQuestionEdit.this.optionCorrectCombo.setValue(valueSelected);
			}
		}
	}

	@Override
	public void setInitialState() {
		this.init = true;
		this.questionModel = new QuestionModel(new QuestionTO());
		this.tableQuestion.deleteData();
		super.setInitialState();
		super.setQueryMode();
		this.managedForm.enableTables();
		this.init = false;
	}

	protected void deleteOption(TableModelEvent e) {
		this.questionModel.removeOption(this.tableQuestion.getSelectedRow());
		// Regenerate Keys Table
		Hashtable<Object, Object> hashTable = new Hashtable<Object, Object>() {};
		int i, size = this.tableModel.getRowCount();
		for (i = e.getFirstRow(); i < (size - 1); i++) {
			Hashtable rowData = this.tableQuestion.getRowData(i);
			Hashtable newkv = new Hashtable<Object, Object>() {};
			newkv.put("POSITION", i);
			newkv.put("OPTION_TITLE", rowData.get("OPTION_TITLE"));
			this.tableQuestion.updateRowData(newkv, rowData);
		}
	}

	protected void deleteOptions(int[] index) {
		if (index.length != 0) {
			int maxIndex = 0;
			for (int i : index) {
				if (i > maxIndex) {
					maxIndex = i;
				}
			}
			if (maxIndex <= this.questionModel.getOptions().size()) {
				this.questionModel.removeOptions(index);
			}
		}
	}

	protected void updateOption(TableModelEvent e) {
		this.questionModel.updateOption(e.getLastRow(), (String) this.tableModel.getValueAt(e.getLastRow(), this.getTableModelColumnIndex("OPTION_TITLE", this.tableModel)));
	}

	protected void insertOption(TableModelEvent e) {
		this.questionModel.addOption(e.getLastRow(), (String) this.tableModel.getValueAt(e.getLastRow(), this.getTableModelColumnIndex("OPTION_TITLE", this.tableModel)));
	}

	protected int getTableModelColumnIndex(String columnName, TableModel tableModel) {
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			if (columnName.equals(tableModel.getColumnName(i))) {
				return i;
			}
		}
		throw new RuntimeException("COLUMN_NOT_FOUND");
	}

	protected int getTableColumnIndex(String columnName, JTable jtable) {
		for (int i = 0; i < jtable.getColumnCount(); i++) {
			if (columnName.equals(jtable.getColumnName(i))) {
				return i;
			}
		}
		throw new RuntimeException("COLUMN_NOT_FOUND");
	}

	public void setParentSurvey(IMSurvey imSurvey) {
		this.parentImSurvey = imSurvey;
	}

	public IMSurvey getParentImSurvey() {
		return this.parentImSurvey;
	}

}
