package com.opentach.adminclient.modules.surveys.listeners;

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.NullValue;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.client.comp.questionary.OQuestionary;
import com.opentach.common.surveys.ISurveyService;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;

public class SaveButtonListener extends AbstractActionListenerButton {
	private static final Logger						LOGGER	= LoggerFactory.getLogger(SaveButtonListener.class);

	/** The update listener. */
	private BasicInteractionManager.UpdateListener	updateListener;

	public SaveButtonListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			ISurveyService eSurvey = ((AbstractOpentachClientLocator) this.getReferenceLocator()).getRemoteService(ISurveyService.class);
			Object surveyExpirationDate = this.getForm().getDataFieldValue("SURVEY_EXPIRATION_DATE");
			if (this.getForm().getInteractionManager().getModifiedFieldAttributes() != null) {
				boolean modifiedSurvExpDate = this.getForm().getInteractionManager().getModifiedFieldAttributes().contains("SURVEY_EXPIRATION_DATE");
				if (!modifiedSurvExpDate) {
					surveyExpirationDate = null;
				} else if (modifiedSurvExpDate && (surveyExpirationDate == null)) {
					surveyExpirationDate = new NullValue();
				}
			}

			String surveyName = (String) this.getForm().getDataFieldValue("SURVEY_NAME");
			Number idSurvey = (Number) this.getForm().getDataFieldValue("ID_SURVEY");

			OQuestionary questionary = (OQuestionary) this.getForm().getElementReference("QUESTIONARY");
			if (idSurvey == null) {
				eSurvey.createSurvey(surveyName, surveyExpirationDate, questionary.getModel().getQuestions(), this.getSessionId());
			} else {
				eSurvey.updateQuestions(idSurvey, surveyName, surveyExpirationDate, questionary.getModel().getQuestions(), this.getSessionId());
			}
			Table eSurveys = (Table) this.getFormManager().getFormReference("formSurveys.xml").getElementReference("ESurveys");
			eSurveys.refreshInThread(0);
			this.hideDialog();
		} catch (Exception e1) {
			SaveButtonListener.LOGGER.error(null, e1);
			MessageManager.getMessageManager().showExceptionMessage(e1, SaveButtonListener.LOGGER);
		}

	}

	/**
	 * Hide dialog.
	 */
	protected void hideDialog() {
		if (this.getForm().getDetailComponent() != null) {
			this.getForm().getDetailComponent().hideDetailForm();
		} else if (this.getForm().getJDialog() != null) {
			this.getForm().getJDialog().setVisible(false);
		}
	}

}
