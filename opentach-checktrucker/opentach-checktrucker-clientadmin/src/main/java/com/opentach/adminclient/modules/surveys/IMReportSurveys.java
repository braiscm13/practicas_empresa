package com.opentach.adminclient.modules.surveys;

import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.Form;
import com.ontimize.gui.manager.IFormManager;
import com.opentach.adminclient.modules.surveys.cards.QuestionsByAutonomyCard;
import com.opentach.adminclient.modules.surveys.cards.QuestionsByCompanyCard;
import com.opentach.adminclient.modules.surveys.cards.QuestionsBySurveyCard;
import com.opentach.adminclient.modules.surveys.cards.QuestionsCorrectVsWrongCard;
import com.opentach.adminclient.modules.surveys.cards.QuestionsOneSurveyCard;
import com.opentach.adminclient.modules.surveys.cards.QuestionsSurveyCorrectVsWrongCard;
import com.opentach.client.AbstractOpentachClientLocator;
import com.utilmize.client.fim.advanced.UBasicFIMSearch;
import com.utilmize.client.gui.cardboard.CardBoard;

public class IMReportSurveys extends UBasicFIMSearch {

	@FormComponent(attr = "CARD_BOARD")
	protected CardBoard cardBoard;

	@Override
	public void registerInteractionManager(Form form, IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		this.cardBoard.addCard("sur.Global",
				new QuestionsCorrectVsWrongCard((AbstractOpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sur.Global", new QuestionsBySurveyCard((AbstractOpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sur.Global", new QuestionsByAutonomyCard((AbstractOpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sur.Survey",
				new QuestionsSurveyCorrectVsWrongCard((AbstractOpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sur.Survey", new QuestionsOneSurveyCard((AbstractOpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
		this.cardBoard.addCard("sur.Company", new QuestionsByCompanyCard((AbstractOpentachClientLocator) this.getReferenceLocator(), this.managedForm, 1, 1));
	}

}
