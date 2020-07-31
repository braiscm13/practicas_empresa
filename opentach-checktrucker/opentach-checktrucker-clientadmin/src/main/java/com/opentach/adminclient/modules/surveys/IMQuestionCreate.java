package com.opentach.adminclient.modules.surveys;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
 * The Class IMQuestionay.
 */
public class IMQuestionCreate extends UBasicFIM {

	private QuestionTO			questionTO, questionTOrep;
	private QuestionModel		questionModel, questionModelRep;

	@FormComponent(attr = "QUEST")
	private Question			question;
	@FormComponent(attr = "QUEST_REPOSITORY")
	private Question			questionRep;
	@FormComponent(attr = "dummy")
	private Table				tableQuestion;
	@FormComponent(attr = "ESurQuestions")
	private Table				tQuestionRep;
	@FormComponent(attr = "QUEST_TITLE")
	private TextDataField		title;
	@FormComponent(attr = "OPTION_CORRECT")
	private TextComboDataField	optionCorrectCombo;
	private TableModel			tableModel;

	public IMQuestionCreate() {
		super();
	}

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);

		this.questionTO = new QuestionTO();
		this.questionModel = new QuestionModel(this.questionTO);
		this.question.setModel(this.questionModel);

		this.questionTOrep = new QuestionTO();
		this.questionModelRep = new QuestionModel(this.questionTOrep);
		this.questionRep.setModel(this.questionModelRep);

		((UReferenceDataField) this.managedForm.getElementReference("ID_QUESTION_TYPE")).addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent event) {
				Object newValue = event.getNewValue();
				if (newValue != null) {
					if (newValue instanceof SearchValue) {
						newValue = ((List<?>) ((SearchValue) newValue).getValue()).get(0);
					}
					Integer intId = Integer.valueOf(String.valueOf(newValue));
					IMQuestionCreate.this.questionModel.setIdClass(intId);
				}
			}
		});

		((UReferenceDataField) this.managedForm.getElementReference("ID_QUESTION_TYPE_1")).addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent event) {
				Object newValue = event.getNewValue();
				if (newValue != null) {
					if (newValue instanceof SearchValue) {
						newValue = ((List<?>) ((SearchValue) newValue).getValue()).get(0);
					}
					Integer intId = Integer.valueOf(String.valueOf(newValue));
					IMQuestionCreate.this.questionModel.setIdClass(intId);
				}
			}
		});

		this.title.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {

				String textTitle = IMQuestionCreate.this.title.getText();
				if (!textTitle.isEmpty()) {
					IMQuestionCreate.this.questionModel.setTitle(textTitle);
				}
			}
		});
		this.tQuestionRep.getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				JTable jtable = IMQuestionCreate.this.tQuestionRep.getJTable();
				if (jtable.getSelectedRow() != -1) {
					// Object oIndexQuest = jtable.getValueAt(jtable.getSelectedRow(),
					// IMQuestionCreate.this.getTableColumnIndex("ID_WU_REPORT_QUEST", jtable));
					// String wuBayesian = (String) IMQuestionCreate.this.managedForm.getDataFieldValue("WU_BAYESIAN_REP");
					// IAccidentWorkUnitService service = BeansFactory.getBean(IAccidentWorkUnitService.class);
					// QuestionTO modelTO = service.recoverWorkUnitQuestion(Integer.valueOf(oIndexQuest.toString()), null);
					QuestionTO modelTO = null;
					IMQuestionCreate.this.questionRep.setModel(new QuestionModel(modelTO));
				} else {
					IMQuestionCreate.this.questionRep.setModel(new QuestionModel(null));
				}
			}
		});

		this.tableModel = this.tableQuestion.getJTable().getModel();
		this.tableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent event) {
				switch (event.getType()) {
					case TableModelEvent.INSERT:
						IMQuestionCreate.this.insertOption(event);
						break;
					case TableModelEvent.UPDATE:
						if (event.getLastRow() == Integer.MAX_VALUE) {
							IMQuestionCreate.this.deleteOptions(IMQuestionCreate.this.tableQuestion.getSelectedRows());
							break;
						}
						IMQuestionCreate.this.updateOption(event);
						break;
					case TableModelEvent.DELETE:
						IMQuestionCreate.this.deleteOption(event);
						break;
					default:
						break;
				}
				Vector<String> options = new Vector<String>();

				if (!IMQuestionCreate.this.tableQuestion.isEmpty()) {
					EntityResult res = new EntityResult((Hashtable<String, Object>) IMQuestionCreate.this.tableQuestion.getValue());
					Vector<String> optionsTitle = (Vector<String>) res.get("OPTION_TITLE");
					for (int count = 0; count < optionsTitle.size(); count++) {
						String option = optionsTitle.get(count);
						options.add(option);
					}
					Object valueSelected = IMQuestionCreate.this.optionCorrectCombo.getValue();
					IMQuestionCreate.this.optionCorrectCombo.setValues(options);
					if ((valueSelected != null) && options.contains(valueSelected)) {
						IMQuestionCreate.this.optionCorrectCombo.setValue(valueSelected);
					}
				}

			}
		});

		this.optionCorrectCombo.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChanged(ValueEvent e) {
				String correctOption = (String) e.getNewValue();
				IMQuestionCreate.this.questionModel.setCorrectOption(correctOption);
			}
		});
	}

	@Override
	public void setInitialState() {
		this.questionModel = new QuestionModel(new QuestionTO());
		this.tableQuestion.deleteData();
		super.setInitialState();
		super.setQueryMode();
		this.managedForm.enableDataFields();
		this.managedForm.enableButtons();

		this.questionTO = new QuestionTO();
		this.questionModel = new QuestionModel(this.questionTO);
		this.question.setModel(this.questionModel);

		this.questionTOrep = new QuestionTO();
		this.questionModelRep = new QuestionModel(this.questionTOrep);
		this.questionRep.setModel(this.questionModelRep);
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
		this.questionModel.updateOption(e.getLastRow(),
				(String) this.tableModel.getValueAt(e.getLastRow(), this.getTableModelColumnIndex("OPTION_TITLE", this.tableModel)));
	}

	protected void insertOption(TableModelEvent e) {
		this.questionModel.addOption(e.getLastRow(),
				(String) this.tableModel.getValueAt(e.getLastRow(), this.getTableModelColumnIndex("OPTION_TITLE", this.tableModel)));
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
}
