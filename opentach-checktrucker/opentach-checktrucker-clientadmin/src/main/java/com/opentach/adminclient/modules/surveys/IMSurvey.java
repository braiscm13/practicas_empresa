package com.opentach.adminclient.modules.surveys;

import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.client.comp.questionary.IQuestionaryModelChangeListener;
import com.opentach.client.comp.questionary.Questionary;
import com.opentach.client.comp.questionary.QuestionaryModel;
import com.opentach.client.comp.questionary.QuestionaryModelEvent;
import com.opentach.common.surveys.QuestionaryTO;
import com.utilmize.client.fim.UBasicFIM;

public class IMSurvey extends UBasicFIM implements IQuestionaryModelChangeListener {
	@FormComponent(attr = "QUESTIONARY")
	private Questionary	questionary;
	@FormComponent(attr = "NUM_QUESTIONS")
	private Label		label;
	private Form		detailForm;

	public IMSurvey() {
		super();
	}

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		this.questionary.setModel(new QuestionaryModel(new QuestionaryTO()));
		this.questionary.setQuestionaryListener(this);
		this.detailForm = this.getManagedForm().getFormManager().getFormCopy("formQuestionEdit.xml");
	}
	@Override
	public void setInsertMode() {
		super.setInsertMode();
		this.questionary.setModel(new QuestionaryModel(new QuestionaryTO()));
		this.questionary.setQuestionaryListener(this);
		this.label.setText("");
		this.detailForm = this.getManagedForm().getFormManager().getFormCopy("formQuestionEdit.xml");
	}

	@Override
	public void modelQuestionaryChanged(QuestionaryModelEvent event) {
		this.label.setText(ApplicationManager.getTranslation("Total") + ": " + this.questionary.getModel().getQuestions()
				.size() + " " + ApplicationManager.getTranslation("Questions"));

	}

	@Override
	public void modelQuestionaryEdit(String option, QuestionaryModel model, int index) {
		ResourceBundle resourceBundle = this.getManagedForm().getResourceBundle();
		switch (option) {
			case QuestionaryEditOptions.EDIT:
				String title = ApplicationManager.getTranslation("Edit_question", resourceBundle);
				JDialog putInModalDialog = this.detailForm.putInModalDialog(title, this.getManagedForm());
				((IMQuestionEdit) this.detailForm.getInteractionManager()).setInitialState();
				this.detailForm.setDataFieldValue("ID_SURVEY", this.managedForm.getDataFieldValue("ID_SURVEY"));
				this.detailForm.setDataFieldValue("ID_QUESTION", model.getQuestions().get(index).getId());
				this.detailForm.setDataFieldValue("ID_QUESTION_TYPE_1", model.getQuestions().get(index).getIdType());
				this.detailForm.setDataFieldValue("OPTION_CORRECT", model.getQuestions().get(index).getCorrectOption());
				((IMQuestionEdit) this.detailForm.getInteractionManager()).setQuestionTO(model.getQuestions().get(index));
				((IMQuestionEdit) this.detailForm.getInteractionManager()).setParentSurvey(this);
				putInModalDialog.pack();
				putInModalDialog.setTitle(title);
				putInModalDialog.setVisible(true);
				break;
			case QuestionaryEditOptions.DELETE:
				int dialogResult = JOptionPane.showConfirmDialog(null, ApplicationManager.getTranslation("DELETE_QUESTION", resourceBundle));
				if (dialogResult == JOptionPane.YES_OPTION) {
					model.removeQuestion(index);
				}
				break;
			default:
				break;
		}
		this.modelQuestionaryChanged(null);

	}
}
