package com.opentach.adminclient.modules.surveys.listeners;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.adminclient.modules.surveys.IMQuestionCreate;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.client.comp.OpentachFormExt;
import com.opentach.client.comp.questionary.Question;
import com.opentach.client.comp.questionary.Questionary;
import com.opentach.common.surveys.ISurveyService;
import com.opentach.common.surveys.QuestionTO;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.tools.exception.UException;

public class ButtonQuestionCreateListener extends AbstractActionListenerButton {
	private static final Logger	LOGGER	= LoggerFactory.getLogger(ButtonQuestionCreateListener.class);

	private JTable				jtable;

	public ButtonQuestionCreateListener(UButton button, Hashtable<?, ?> params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Button button = (Button) this.getButton();
			Object questionType = ((UReferenceDataField) this.getForm().getElementReference("ID_QUESTION_TYPE_1")).getValue();
			TextDataField title = (TextDataField) this.getForm().getElementReference("QUEST_TITLE");
			Question question = (Question) this.getForm().getElementReference("QUEST");
			Questionary questionary = (Questionary) ((OpentachFormExt) this.getForm()).getParentForm().getElementReference("QUESTIONARY");
			Table tQuestionRep = (Table) this.getForm().getElementReference("ESurQuestions");

			if (button.getKey().equals("addcont") || button.getKey().equals("addexit")) {
				if ((questionType != null) && !title.getText().isEmpty() && !(question.getModel() == null)) {
					if (question.getModel().getOptions().size() == 0) {
						throw new UException("E_NO_OPTIONS_QUESTION");
					}
					this.addQuestion(question, questionary);
					if (button.getKey().equals("addexit")) {
						this.getForm().getJDialog().setVisible(false);
					}
				} else {
					throw new UException("E_QUESTION_REQUIRED");
				}
			} else if (button.getKey().equals("addselect")) {
				this.jtable = tQuestionRep.getJTable();
				if (this.jtable.getSelectedRow() != -1) {
					int[] selectedRows = this.jtable.getSelectedRows();
					this.addQuestionRepository(selectedRows, questionary);
					this.getForm().getJDialog().setVisible(false);
				} else {
					throw new UException("E_QUESTION_REQUIRED");

				}
			}
			((IMQuestionCreate) this.getForm().getInteractionManager()).setInitialState();
		} catch (Exception e2) {
			MessageManager.getMessageManager().showExceptionMessage(e2, this.getForm(), ButtonQuestionCreateListener.LOGGER);
		}
	}

	private void addQuestion(Question question, Questionary questionary) {
		questionary.getModel().addQuestion(questionary.getModel().getQuestions().size(), question.getModel().getQuestionTO());
	}

	private void addQuestionRepository(int[] selectedRows, Questionary questionary) throws Exception {
		ISurveyService eSurvey = ((AbstractOpentachClientLocator) this.getReferenceLocator()).getRemoteService(ISurveyService.class);
		List<Number> lIdQuestions = new ArrayList<Number>();
		for (int index : selectedRows) {
			Number idQuestion = (Number) this.jtable.getValueAt(index, this.getTableColumnIndex("ID_QUESTION", this.jtable));
			lIdQuestions.add(idQuestion);
		}

		this.checkRepeatedQuestions(lIdQuestions, questionary);

		List<QuestionTO> lQuestions = eSurvey.getQuestions(lIdQuestions);
		questionary.getModel().addQuestions(lQuestions);
	}

	private void checkRepeatedQuestions(List<Number> lIdQuestions, Questionary questionary) throws UException {
		StringBuilder sb = new StringBuilder();
		List<String> repeatedQuestions = new ArrayList<String>();
		for (Number idQuestion : lIdQuestions) {
			List<QuestionTO> lQuestions = questionary.getModel().getQuestions();
			for (QuestionTO questTO : lQuestions) {
				if ((questTO.getId() != null) && idQuestion.equals(questTO.getId())) {
					repeatedQuestions.add(questTO.getTitle());
				}
			}
		}

		for (String title : repeatedQuestions) {
			sb.append(title).append(", ");
		}

		if (sb.length() > 0) {
			String result = sb.substring(0, sb.length() - ", ".length());
			throw new UException("E_REPEATED_QUESTION", new Object[] { result });
		}
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
