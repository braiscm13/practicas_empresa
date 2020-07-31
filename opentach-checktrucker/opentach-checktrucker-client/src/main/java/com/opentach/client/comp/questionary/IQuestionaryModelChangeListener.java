package com.opentach.client.comp.questionary;


public interface IQuestionaryModelChangeListener {
	void modelQuestionaryChanged(QuestionaryModelEvent event);

	void modelQuestionaryEdit(String option, QuestionaryModel model, int index);

	public final class QuestionaryEditOptions {
		public static final String	DELETE	= "delete";
		public static final String	EDIT	= "edit";
	}
}
