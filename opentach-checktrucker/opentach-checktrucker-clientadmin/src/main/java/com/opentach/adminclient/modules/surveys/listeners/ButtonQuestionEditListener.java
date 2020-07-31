package com.opentach.adminclient.modules.surveys.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.button.Button;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.adminclient.modules.surveys.IMQuestionEdit;
import com.opentach.client.comp.questionary.Question;
import com.opentach.client.comp.questionary.Questionary;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.tools.exception.UException;

public class ButtonQuestionEditListener extends AbstractActionListenerButton {
	private static final Logger	LOGGER	= LoggerFactory.getLogger(ButtonQuestionEditListener.class);

	public ButtonQuestionEditListener(UButton button, Hashtable<?, ?> params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Button button = (Button) this.getButton();
			Question question = (Question) this.getForm().getElementReference("QUEST");
			Questionary questionary = (Questionary) ((IMQuestionEdit) this.getInteractionManager()).getParentImSurvey().getManagedForm().getElementReference("QUESTIONARY");
			String correctOption = question.getModel().getCorrectOption();
			if (correctOption == null) {
				throw new UException("E_NO_CORRECT_OPTION");
			}

			if (button.getKey().equals("updateFormQuestionEdit")) {
				String title = question.getModel().getTitle();
				question.getModel().setTitle(title.substring(title.indexOf(". ") + 2, title.length()));
				questionary.updateModel();
				this.getForm().getJDialog().setVisible(false);
			}

		} catch (Exception e2) {
			MessageManager.getMessageManager().showExceptionMessage(e2, this.getForm(), ButtonQuestionEditListener.LOGGER);
		}
	}
}
